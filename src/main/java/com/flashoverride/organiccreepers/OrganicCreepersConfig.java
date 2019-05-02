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
    @Config.Comment("The radius in blocks Creeper Plant will check to see if there are already too many creepers in the area to spawn more (number of creepers is set by biome)")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.creeperCheckDistance")
    @Config.RangeInt(min = 0, max = 128)
    public static int creeperCheckDistance = 16;

    @Config.Comment("Enable vanilla creeper spawning")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.enableVanillaSpawning")
    public static boolean enableVanillaSpawning = false;

    @Config.Comment("Enable spore generation from creeper explosions")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.enableCreeperSpores")
    public static boolean enableCreeperSpores = true;

    @Config.Comment("Enable spread of Creeper Plants without needing creeper explosions (recommend disabling if enableReplanting=true)")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.enablePlantSpread")
    public static boolean enablePlantSpread = true;

    @Config.Comment("Enable automatic replanting of Creeper Plant after spawning new creeper")
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.enableReplanting")
    public static boolean enableReplanting = false;

    @Config.Comment("How fast the Creeper Plant will grow (higher = faster)")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.growthRate")
    public static double growthRate = 0.5d;

    @Config.Comment("The percent chance that gunpowder will drop from broken grown Creeper Plants")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.gunpowderDropRate")
    public static double gunpowderDropRate = 1.0d;

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

    @Config.Comment("If enablePlantSpread=true, how likely the Creeper Plant will spread each update (higher = more likely)")
    @Config.RangeDouble(min = 0.0d, max = 1.0d)
    @Config.LangKey("config." + OrganicCreepers.MODID + ".general.spreadChance")
    public static double spreadChance = 0.5d;

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
