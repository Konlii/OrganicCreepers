package com.flashoverride.organiccreepers.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import com.flashoverride.organiccreepers.OrganicCreepers;

@ObjectHolder(OrganicCreepers.MODID)
public class OrganicCreeperBlocks
{
    @ObjectHolder("creeper_plant")
    public static final BlockCreeperPlant blockCreeperPlant = null;

    @ObjectHolder("creeper_plant")
    public static final ItemBlock itemBlockCreeperPlant = null;

    @SideOnly(Side.CLIENT)
    public static void registerBlockModel(Block block, int metaData)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), metaData,
                new ModelResourceLocation(OrganicCreepers.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemBlockModels()
    {
        registerItemBlockModel(itemBlockCreeperPlant, 0);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemBlockModel(ItemBlock block, int metaData)
    {
        ModelLoader.setCustomModelResourceLocation(block, metaData,
                new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }

    private static Block setBlockName(Block block, String blockName)
    {
        block.setRegistryName(blockName);
        block.setUnlocalizedName(blockName);
        return block;
    }

    private static Item setItemName(Item item, String itemName)
    {
        item.setRegistryName(itemName);
        item.setUnlocalizedName(itemName);
        return item;
    }

    @EventBusSubscriber(modid = OrganicCreepers.MODID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void onEvent(final RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            registry.register(setBlockName(new BlockCreeperPlant(), "creeper_plant"));
        }

        @SubscribeEvent
        public static void registerItemBlocks(final RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();

            registry.register(setItemName(new ItemBlock(blockCreeperPlant), blockCreeperPlant.getRegistryName().getResourcePath()));
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onModelEvent(final ModelRegistryEvent event)
        {
            registerBlockModel(blockCreeperPlant, 0);
            registerItemBlockModels();
        }
    }
}
