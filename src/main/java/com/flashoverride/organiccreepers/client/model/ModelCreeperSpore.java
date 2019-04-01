package com.flashoverride.organiccreepers.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCreeperSpore extends ModelBase
{
    private final ModelRenderer main;

    public ModelCreeperSpore()
    {
        this(0.0F);
    }

    public ModelCreeperSpore(float scaleFactor)
    {
        textureWidth = 8;
        textureHeight = 8;

        this.main = new ModelRenderer(this);
        this.main.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, scaleFactor);
        this.main.setTextureOffset(0, 0).addBox(0.0F, -4.0F, -4.0F, 0, 8, 8, scaleFactor);
        this.main.setTextureOffset(0, 0).addBox(-4.0F, 0.0F, -4.0F, 8, 0, 8, scaleFactor);
        this.main.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.main.render(scale);
    }
}