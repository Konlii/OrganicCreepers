package com.flashoverride.organiccreepers.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.flashoverride.organiccreepers.block.OrganicCreeperBlocks;

public class EntityCreeperSpore extends Entity implements IProjectile
{
    private Entity creeper;

    public EntityCreeperSpore(World worldIn)
    {
        super(worldIn);
    }

    public EntityCreeperSpore(World worldIn, double x, double y, double z, double motionX, double motionY, double motionZ, EntityLivingBase creeper)
    {
        super(worldIn);

        this.creeper = creeper;
        this.rotationPitch = this.rand.nextFloat() * 10f;
        this.rotationYaw = this.rand.nextFloat() * 10f;
        this.setPosition(x, y, z);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(y, (double) f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    public void onHit(RayTraceResult rayTraceResult)
    {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        if ((rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY && rayTraceResult.entityHit != null && rayTraceResult.entityHit != creeper) || (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && !world.getBlockState(blockPos).getBlock().isPassable(world, blockPos)))
        {
            this.motionX = 0d;
            this.motionY = 0d;
            this.motionZ = 0d;
        }
        if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK && !world.getBlockState(blockPos).getBlock().isPassable(world, blockPos) && rayTraceResult.sideHit == EnumFacing.UP)
        {
            int i = 0;
            while (world.getBlockState(blockPos.add(0, -i, 0)).getBlock().isReplaceable(world, blockPos.add(0, -i, 0)))
            {
                world.setBlockToAir(blockPos.add(0, -i, 0));
                i++;
            }
            i--;
            if (OrganicCreeperBlocks.blockCreeperPlant.canBlockStay(world, blockPos.add(0, -i, 0), OrganicCreeperBlocks.blockCreeperPlant.getDefaultState()) && world.getBlockState(blockPos.add(0, -i, 0)).getBlock().isReplaceable(world, blockPos.add(0, -i, 0)))
            {
                world.setBlockState(blockPos.add(0, -i, 0), OrganicCreeperBlocks.blockCreeperPlant.getDefaultState());
            }

            this.setDead();
        }
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);

        if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult))
        {
            this.onHit(raytraceresult);
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, (double) f) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            ;

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
        {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
        {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
        {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        float f1 = 0.99F;
        float f2 = 0.06F;

        if (this.ticksExisted > 200)
        {
            this.setDead();
        }
        else if (this.isInWater())
        {
            this.setDead();
        }
        else
        {
            this.motionX *= f1;
            this.motionY *= f1;
            this.motionZ *= f1;

            if (!this.hasNoGravity())
            {
                this.motionY -= f2;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float) (MathHelper.atan2(y, (double) f) * (180D / Math.PI));
            this.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public boolean isImmuneToExplosions()
    {
        return true;
    }
}
