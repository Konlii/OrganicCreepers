package com.flashoverride.organiccreepers;

import org.apache.logging.log4j.Level;
import net.minecraftforge.common.config.Configuration;

import com.flashoverride.organiccreepers.proxy.CommonProxy;

public class Config
{

    public static float spawnDensity = 0.5f;
    public static int sporeCount = 10;

    public static void readConfig()
    {
        Configuration cfg = CommonProxy.config;
        try
        {
            cfg.load();
            initGeneralConfig(cfg);
        }
        catch (Exception e1)
        {
            OrganicCreepers.logger.log(Level.ERROR, "Problem loading config file!", e1);
        }
        finally
        {
            if (cfg.hasChanged())
            {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg)
    {
        cfg.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "General configuration");
        spawnDensity = cfg.getFloat("spawnDensity", Configuration.CATEGORY_GENERAL, 0.5f, 0.0f, 1.0f, "How likely the Creeper Plant will worldgen in a chunk");
        sporeCount = cfg.getInt("sporeCount", Configuration.CATEGORY_GENERAL, 10, 0, 100, "How many Creeper Spores are thrown into the air from each creeper explosion");
    }
}