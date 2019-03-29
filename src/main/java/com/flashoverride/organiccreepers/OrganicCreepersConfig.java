package com.flashoverride.organiccreepers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = OrganicCreepers.MODID, name = "OrganicCreepers")
@Mod.EventBusSubscriber(modid = OrganicCreepers.MODID)
@Config.LangKey("config." + OrganicCreepers.MODID)
public class OrganicCreepersConfig
{
    @Config.Comment("How fast the Creeper Plant will grow (higher = faster)")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.growthRate")
    public static double growthRate = 0.5d;

    @Config.Comment("Modifier for impact rain has on growth rate (higher = more impact)")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.rainDelta")
    public static double rainDelta = 1.0d;

    @Config.Comment("How likely the Creeper Plant will worldgen in a chunk (higher = more likely)")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.spawnDensity")
    public static double spawnDensity = 0.5d;

    @Config.Comment("How many Creeper Spores are thrown into the air from each creeper explosion")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.sporeCount")
    @Config.RangeInt(min = 0, max = 100)
    public static int sporeCount = 10;

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(OrganicCreepers.MODID))
        {
            OrganicCreepers.logger.warn("Config changed");
            ConfigManager.sync(OrganicCreepers.MODID, Config.Type.INSTANCE);
        }
    }
}
