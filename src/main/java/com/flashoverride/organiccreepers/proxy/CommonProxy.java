package com.flashoverride.organiccreepers.proxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.flashoverride.organiccreepers.handlers.EventHandler;
import com.flashoverride.organiccreepers.handlers.TerrainGenEventHandler;

@Mod.EventBusSubscriber
public class CommonProxy
{
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
