package com.flashoverride.organiccreepers.proxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import com.flashoverride.organiccreepers.OrganicCreepers;
import com.flashoverride.organiccreepers.entity.projectile.EntityCreeperSpore;
import com.flashoverride.organiccreepers.handlers.EventHandler;
import com.flashoverride.organiccreepers.handlers.TerrainGenEventHandler;

@Mod.EventBusSubscriber
public class CommonProxy
{
    @SubscribeEvent
    public static void registerEntities(final RegistryEvent.Register<EntityEntry> event)
    {
        int ID = 0;
        final EntityEntry CREEPER_SPORE = EntityEntryBuilder.create()
                .entity(EntityCreeperSpore.class)
                .id(new ResourceLocation(OrganicCreepers.MODID, "creeper_spore"), ID++)
                .name("creeper_spore")
                .tracker(64, 5, true)
                .build();

        event.getRegistry().register(CREEPER_SPORE);
    }

    public void preInit(FMLPreInitializationEvent e)
    {
    }

    public void init(FMLInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.TERRAIN_GEN_BUS.register(new TerrainGenEventHandler());
    }

    public void postInit(FMLPostInitializationEvent e)
    {
    }
}
