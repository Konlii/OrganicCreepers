package com.flashoverride.organiccreepers.woldgen;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import com.flashoverride.organiccreepers.Config;
import com.flashoverride.organiccreepers.block.BlockCreeperPlant;
import com.flashoverride.organiccreepers.block.OrganicCreeperBlocks;

@ParametersAreNonnullByDefault
public class WorldGenCreeperPlant
{
    public static void generate(World worldIn, Random rand, ChunkPos chunkPos)
    {
        BlockPos blockPos = new BlockPos(chunkPos.getXStart(), worldIn.getHeight(chunkPos.getXStart(), chunkPos.getZStart()), chunkPos.getZStart()).add(rand.nextInt(16), 0, rand.nextInt(16));

        BlockCreeperPlant blockCreeperPlant = OrganicCreeperBlocks.blockCreeperPlant;
        IBlockState state = blockCreeperPlant.getDefaultState();
        if (Config.spawnDensity > 0f && rand.nextFloat() < Config.spawnDensity)
        {
            for (int i = 0; i < 8; i++)
            {
                blockPos = blockPos.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(4) - rand.nextInt(4));
                if (worldIn.isAreaLoaded(blockPos, 1) &&
                        !worldIn.provider.isNether() && !worldIn.isOutsideBuildHeight(blockPos) &&
                        worldIn.isAirBlock(blockPos) &&
                        blockCreeperPlant.canBlockStay(worldIn, blockPos, state))
                {
                    state = state.withProperty(BlockCreeperPlant.AGE, rand.nextInt(4));
                    worldIn.setBlockState(blockPos, state, 2);
                }
            }
        }
    }
}