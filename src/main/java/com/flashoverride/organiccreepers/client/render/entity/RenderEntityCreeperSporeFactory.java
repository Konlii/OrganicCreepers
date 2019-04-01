package com.flashoverride.organiccreepers.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import com.flashoverride.organiccreepers.entity.projectile.EntityCreeperSpore;

public class RenderEntityCreeperSporeFactory implements IRenderFactory<EntityCreeperSpore>
{
    @Override
    public Render<? super EntityCreeperSpore> createRenderFor(RenderManager manager)
    {
        return new RenderEntityCreeperSpore(manager);
    }
}