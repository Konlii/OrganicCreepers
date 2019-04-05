package com.flashoverride.organiccreepers.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import com.flashoverride.organiccreepers.OrganicCreepersConfig;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;

@ParametersAreNonnullByDefault
public class BlockCreeperPlant extends BlockBush implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    private static final AxisAlignedBB PLANT_SHORTEST_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.40625D, 0.875D);
    private static final AxisAlignedBB PLANT_SHORTER_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.8125D, 0.75D);
    private static final AxisAlignedBB PLANT_SHORT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.21875D, 0.75D);
    private static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.625D, 0.75D);
    private static final AxisAlignedBB PLANT_FULL_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);
    private static final PropertyEnum<EnumBlockPart> PART = PropertyEnum.create("part", EnumBlockPart.class);
    private int explosionRadius = 3;

    public BlockCreeperPlant()
    {
        super(Material.PLANTS);
        setCreativeTab(CreativeTabs.MISC);
        setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);
        Blocks.FIRE.setFireInfo(this, 5, 20);
        setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(PART, EnumBlockPart.SINGLE));
    }

    public double getGrowthRate(World world, BlockPos blockPos)
    {
        return (world.isRainingAt(blockPos)) ? OrganicCreepersConfig.growthRate + (world.getRainStrength((float) OrganicCreepersConfig.rainDelta)) : OrganicCreepersConfig.growthRate;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos, worldIn.getBlockState(pos));
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        if (Loader.isModLoaded("tfc")) return BlocksTFC.isSoil(state);
        else return super.canSustainBush(state);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (worldIn.getBlockState(fromPos).getMaterial() == Material.FIRE || worldIn.getBlockState(fromPos).getMaterial() == Material.LAVA || worldIn.getBlockState(fromPos).getBlock().isBurning(worldIn, fromPos))
        {
            explode(worldIn, pos, state);
        }

    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (OrganicCreepersConfig.enablePlantSpread && rand.nextDouble() < OrganicCreepersConfig.spreadChance &&
                !worldIn.isRemote && worldIn.getLightFromNeighbors(pos) >= 9)
        {
            spread(worldIn, rand, pos, state);
        }

        int j = state.getValue(AGE);

        if (rand.nextDouble() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true) && canGrow(worldIn, pos, state, worldIn.isRemote))
        {
            if (j == 3)
            {
                grow(worldIn, rand, pos, state);
            }
            else if (j < 3)
            {
                worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(PART, getPlantPart(worldIn, pos)));
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
        }

        for (EnumFacing facing : EnumFacing.values())
        {
            if (worldIn.getBlockState(pos.offset(facing)).getMaterial() == Material.FIRE || worldIn.getBlockState(pos.offset(facing)).getMaterial() == Material.LAVA || worldIn.getBlockState(pos.offset(facing)).getBlock().isBurning(worldIn, pos.offset(facing)))
            {
                explode(worldIn, pos, state);
            }
        }

        super.updateTick(worldIn, pos, state, rand);
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        if (worldIn.getBlockState(pos.down(2)).getBlock() == this) return false;
        if (state.getBlock() == this)
        {
            return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
        }
        return this.canSustainBush(soil);
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (getPlantPart(source, pos) == EnumBlockPart.SINGLE)
        {
            switch (state.getValue(AGE))
            {
                case 0:
                case 1:
                    return PLANT_SHORTEST_AABB.offset(state.getOffset(source, pos));
                default:
                    return PLANT_SHORTER_AABB.offset(state.getOffset(source, pos));
            }
        }
        else if (getPlantPart(source, pos) == EnumBlockPart.UPPER && source.getBlockState(pos.down()).getBlock() == this)
        {
            switch (source.getBlockState(pos.down()).getValue(AGE))
            {
                case 0:
                case 1:
                    return PLANT_SHORT_AABB.offset(state.getOffset(source, pos));
                default:
                    return PLANT_AABB.offset(state.getOffset(source, pos));
            }
        }
        return PLANT_FULL_AABB.offset(state.getOffset(source, pos));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        if (state.getValue(PART) == EnumBlockPart.SINGLE)
        {
            int i;
            //noinspection StatementWithEmptyBody
            for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) ;
            return i < 2 && worldIn.isAirBlock(pos.up()) && canBlockStay(worldIn, pos.up(), state);
        }
        else if (state.getValue(PART) == EnumBlockPart.LOWER)
        {
            return worldIn.getBlockState(pos.up()).getBlock() == this;
        }
        return false;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (state.getValue(PART) == EnumBlockPart.LOWER)
        {
            int check = OrganicCreepersConfig.creeperCheckDistance;
            List<Entity> entityList = worldIn.getEntitiesWithinAABB(EntityCreeper.class, new AxisAlignedBB(pos.getX() - check, pos.getY() - check, pos.getZ() - check, pos.getX() + check, pos.getY() + check, pos.getZ() + check));
            for (Biome.SpawnListEntry entry : worldIn.getBiome(pos).getSpawnableList(EnumCreatureType.MONSTER))
            {
                if (entry.entityClass.getSimpleName().equals("EntityCreeper") && entityList.size() < entry.maxGroupCount)
                {
                    EntityCreeper creeper = new EntityCreeper(worldIn);
                    float yaw = Math.round(rand.nextInt(360) / 90f) * 90f;
                    creeper.setPositionAndRotation(pos.getX() + 0.5d + state.getOffset(worldIn, pos).x, pos.getY(), pos.getZ() + 0.5d + state.getOffset(worldIn, pos).z, yaw, 0);
                    creeper.setRotationYawHead(yaw);

                    if (!worldIn.isRemote) worldIn.spawnEntity(creeper);

                    worldIn.destroyBlock(pos.up(), false);
                    if (OrganicCreepersConfig.enableReplanting) worldIn.setBlockState(pos, this.getDefaultState());
                    else worldIn.destroyBlock(pos, false);
                }
            }
        }
        else
        {
            worldIn.setBlockState(pos.up(), this.getDefaultState());
            IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(PART, getPlantPart(worldIn, pos));
            worldIn.setBlockState(pos, iblockstate);
            iblockstate.neighborChanged(worldIn, pos.up(), this, pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(PART, getPlantPart(worldIn, pos));
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 10;
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if (state.getValue(AGE) == 0)
        {
            return Items.AIR;
        }
        return Items.GUNPOWDER;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = playerIn.getHeldItem(hand);

        if (!itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT_AND_STEEL || itemstack.getItem() == Items.FIRE_CHARGE || (Loader.isModLoaded("tfc") && itemstack.getItem() == ItemsTFC.FIRESTARTER)))
        {
            if (itemstack.getItem() == Items.FLINT_AND_STEEL || (Loader.isModLoaded("tfc") && itemstack.getItem() == ItemsTFC.FIRESTARTER))
            {
                worldIn.playSound(playerIn, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, playerIn.getSoundCategory(), 1.0F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
                itemstack.damageItem(1, playerIn);
            }
            else if (!playerIn.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            this.explode(worldIn, pos, state);

            return true;
        }
        else
        {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote && entityIn instanceof EntityArrow)
        {
            EntityArrow entityarrow = (EntityArrow) entityIn;

            if (entityarrow.isBurning())
            {
                this.explode(worldIn, pos, state);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AGE, PART);
    }

    @Override
    @Nonnull
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XYZ;
    }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
    {
        IBlockState plant = plantable.getPlant(world, pos.offset(direction));

        if (plant.getBlock() == this)
        {
            return true;
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    private void spread(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isAreaLoaded(pos, 4)) return;

        if (state.getValue(PART) == EnumBlockPart.LOWER)
        {
            BlockPos blockPos = pos.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));
            int check = Math.max(OrganicCreepersConfig.creeperCheckDistance - 1, 0);
            List<Entity> entityList = worldIn.getEntitiesWithinAABB(EntityCreeper.class, new AxisAlignedBB(blockPos.getX() - check, blockPos.getY() - check, blockPos.getZ() - check, blockPos.getX() + check, blockPos.getY() + check, blockPos.getZ() + check));

            for (Biome.SpawnListEntry entry : worldIn.getBiome(blockPos).getSpawnableList(EnumCreatureType.MONSTER))
            {
                if (entry.entityClass.getSimpleName().equals("EntityCreeper") && entityList.size() < entry.maxGroupCount)
                {
                    IBlockState soilBlockState = worldIn.getBlockState(blockPos.down());

                    if (!worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockPos) &&
                            worldIn.getLightFromNeighbors(blockPos) >= 4 && soilBlockState.getLightOpacity(worldIn, blockPos.up()) > 2 &&
                            worldIn.isAirBlock(blockPos) &&
                            this.canSustainBush(soilBlockState))
                    {
                        worldIn.setBlockState(blockPos, this.getDefaultState());
                    }
                }
            }
        }
    }

    private void explode(World world, BlockPos pos, IBlockState state)
    {
        if (!world.isRemote)
        {
            EntityCreeper creeper = new EntityCreeper(world);
            creeper.setPosition(pos.getX() + 0.5d + state.getOffset(world, pos).x, pos.getY(), pos.getZ() + 0.5d + state.getOffset(world, pos).z);

            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, creeper);

            if (getPlantPart(world, pos) == EnumBlockPart.UPPER && world.getBlockState(pos.down()).getBlock() == this)
            {
                float f = (world.getBlockState(pos.down()).getValue(AGE) + 1f) / 2f;
                world.createExplosion(creeper, pos.down().getX() + 0.5d + state.getOffset(world, pos).x, pos.down().getY(), pos.down().getZ() + 0.5d + state.getOffset(world, pos).z, (float) this.explosionRadius * f, flag);
                world.setBlockToAir(pos);
                world.setBlockToAir(pos.down());
            }
            else if (getPlantPart(world, pos) == EnumBlockPart.LOWER)
            {
                float f = (state.getValue(AGE) + 1f) / 2f;
                world.createExplosion(creeper, pos.getX() + 0.5d + state.getOffset(world, pos).x, pos.getY(), pos.getZ() + 0.5d + state.getOffset(world, pos).z, (float) this.explosionRadius * f, flag);
                world.setBlockToAir(pos);
                world.setBlockToAir(pos.up());
            }
            else if (getPlantPart(world, pos) == EnumBlockPart.SINGLE)
            {
                float f = (state.getValue(AGE) + 1f) / 4f;
                world.createExplosion(creeper, pos.getX() + 0.5d + state.getOffset(world, pos).x, pos.getY(), pos.getZ() + 0.5d + state.getOffset(world, pos).z, (float) this.explosionRadius * f, flag);
                world.setBlockToAir(pos);
            }
        }
    }

    private EnumBlockPart getPlantPart(IBlockAccess world, BlockPos pos)
    {
        if (world.getBlockState(pos.down()).getBlock() != this && world.getBlockState(pos.up()).getBlock() == this)
        {
            return EnumBlockPart.LOWER;
        }
        if (world.getBlockState(pos.down()).getBlock() == this && world.getBlockState(pos.up()).getBlock() != this)
        {
            return EnumBlockPart.UPPER;
        }
        return EnumBlockPart.SINGLE;
    }

    public enum EnumBlockPart implements IStringSerializable
    {
        UPPER,
        LOWER,
        SINGLE;

        public String toString()
        {
            return this.getName();
        }

        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
