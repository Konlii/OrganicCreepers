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
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;

import com.flashoverride.organiccreepers.Config;
import net.dries007.tfc.objects.blocks.BlocksTFC;

@ParametersAreNonnullByDefault
public class BlockCreeperPlant extends BlockBush implements IGrowable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);
    private static final AxisAlignedBB PLANT_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    private static final PropertyEnum<EnumBlockPart> PART = PropertyEnum.create("part", EnumBlockPart.class);

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

    public float getGrowthRate(World world, BlockPos blockPos)
    {
        return (world.isRainingAt(blockPos)) ? Config.growthRate + (world.getRainStrength(Config.rainDelta)) : Config.growthRate;
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
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        int j = state.getValue(AGE);

        if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true) && canGrow(worldIn, pos, state, worldIn.isRemote))
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
        return PLANT_AABB.offset(state.getOffset(source, pos));
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
            List<Entity> entityList = worldIn.getEntitiesWithinAABB(EntityCreeper.class, new AxisAlignedBB(pos.getX() - 32, pos.getY() - 32, pos.getZ() - 32, pos.getX() + 32, pos.getY() + 32, pos.getZ() + 32));
            for (Biome.SpawnListEntry entity : worldIn.getBiome(pos).getSpawnableList(EnumCreatureType.MONSTER))
            {
                if (entity.entityClass.getSimpleName().equals("EntityCreeper") && entityList.size() < entity.maxGroupCount)
                {
                    EntityCreeper creeper = new EntityCreeper(worldIn);
                    float yaw = Math.round(rand.nextInt(360) / 90f) * 90f;
                    creeper.setPositionAndRotation(pos.getX() + 0.5d + state.getOffset(worldIn, pos).x, pos.getY(), pos.getZ() + 0.5d + state.getOffset(worldIn, pos).z, yaw, 0);
                    creeper.setRotationYawHead(yaw);

                    if (!worldIn.isRemote) worldIn.spawnEntity(creeper);

                    worldIn.destroyBlock(pos.up(), false);
                    worldIn.setBlockState(pos, this.getDefaultState());
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