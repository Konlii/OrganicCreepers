package com.flashoverride.organiccreepers;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.flashoverride.organiccreepers.proxy.CommonProxy;

@Mod(modid = OrganicCreepers.MODID, name = OrganicCreepers.MODNAME, version = OrganicCreepers.MODVERSION, dependencies = "required-after:forge@[11.16.0.1865,)", useMetadata = true)
public class OrganicCreepers
{
    public static final String MODID = "organiccreepers";
    public static final String MODNAME = "Organic Creepers";
    public static final String MODVERSION = "1.2.2";

    @SidedProxy(clientSide = "com.flashoverride.organiccreepers.proxy.ClientProxy", serverSide = "com.flashoverride.organiccreepers.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static OrganicCreepers instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit(e);
    }
}
