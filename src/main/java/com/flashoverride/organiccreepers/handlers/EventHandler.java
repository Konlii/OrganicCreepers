package com.flashoverride.organiccreepers.handlers;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.flashoverride.organiccreepers.Config;
import com.flashoverride.organiccreepers.entity.projectile.EntityCreeperSpore;

public class EventHandler
{
    @SubscribeEvent
    public void checkSpawn(LivingSpawnEvent.CheckSpawn e)
    {
        if (e.getEntity() instanceof EntityCreeper)
        {
            if (e.getSpawner() == null)
                e.setResult(Event.Result.DENY);
            else e.setResult(Event.Result.DEFAULT);
        }
    }

    @SubscribeEvent
    public void explosionEvent(ExplosionEvent.Detonate e)
    {
        World world = e.getWorld();
        Random rand = world.rand;
        BlockPos explosionPos = new BlockPos(e.getExplosion().getPosition());

        if (e.getExplosion().getExplosivePlacedBy() instanceof EntityCreeper)
        {
            for (Entity entity : e.getAffectedEntities())
            {
                BlockPos entityPos = entity.getPosition();
                EntityCreeperSpore entityCreeperSpore = new EntityCreeperSpore(world, entityPos.getX(), entityPos.getY(), entityPos.getZ(), entity.motionX, entity.motionY, entity.motionZ);
                entityCreeperSpore.shoot(0d, 1d, 0d, 0F, 10.0F);
                if (!world.isRemote) world.spawnEntity(entityCreeperSpore);
            }
            for (int i = 0; i < Config.sporeCount; i++)
            {
                EntityCreeperSpore entityCreeperSpore = new EntityCreeperSpore(world, explosionPos.getX(), explosionPos.getY(), explosionPos.getZ(), e.getExplosion().getExplosivePlacedBy().motionX, e.getExplosion().getExplosivePlacedBy().motionY, e.getExplosion().getExplosivePlacedBy().motionZ);
                double d0 = rand.nextDouble() - rand.nextDouble();
                double d1 = 2d + rand.nextDouble() - rand.nextDouble();
                double d2 = rand.nextDouble() - rand.nextDouble();
                float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
                entityCreeperSpore.shoot(d0, d1 + (double) f, d2, rand.nextFloat() * 2F, 10.0F);
                if (!world.isRemote) world.spawnEntity(entityCreeperSpore);
            }
        }
    }
}
