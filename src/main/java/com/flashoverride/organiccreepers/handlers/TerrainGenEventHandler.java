package com.flashoverride.organiccreepers.handlers;

import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.flashoverride.organiccreepers.woldgen.WorldGenCreeperPlant;

public class TerrainGenEventHandler
{
    @SubscribeEvent
    public void decorateBiomeEvent(DecorateBiomeEvent.Decorate e)
    {
        if (e.getType() == DecorateBiomeEvent.Decorate.EventType.FLOWERS)
            WorldGenCreeperPlant.generate(e.getWorld(), e.getRand(), e.getChunkPos());
    }
}
