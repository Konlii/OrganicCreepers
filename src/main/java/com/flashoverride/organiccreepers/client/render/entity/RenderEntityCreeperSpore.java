package com.flashoverride.organiccreepers.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.flashoverride.organiccreepers.OrganicCreepers;
import com.flashoverride.organiccreepers.client.model.ModelCreeperSpore;
import com.flashoverride.organiccreepers.entity.projectile.EntityCreeperSpore;

@SideOnly(Side.CLIENT)
public class RenderEntityCreeperSpore extends Render<EntityCreeperSpore>
{
    private static final ResourceLocation CREEPER_SPORE_TEXTURE = new ResourceLocation(OrganicCreepers.MODID, "textures/entity/creeper_spore.png");
    private final ModelCreeperSpore model = new ModelCreeperSpore();

    public RenderEntityCreeperSpore(RenderManager renderManager)
    {
        super(renderManager);
    }

    public void doRender(EntityCreeperSpore entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + 0.15F, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.model.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.03125F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityCreeperSpore entity)
    {
        return CREEPER_SPORE_TEXTURE;
    }
}