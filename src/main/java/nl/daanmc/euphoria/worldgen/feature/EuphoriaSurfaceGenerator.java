package nl.daanmc.euphoria.worldgen.feature;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.daanmc.euphoria.worldgen.ISurfaceGen;

import java.util.Random;

public class EuphoriaSurfaceGenerator<T extends Block & ISurfaceGen> implements IWorldGenerator {
    private final ResourceLocation blockName;
    private T block = null;
    private final int chunkSpawnRate, maxGroupSize;

    public EuphoriaSurfaceGenerator(ResourceLocation blockRegistryName, int chunkSpawnRate, int maxGroupSize) {
        this.blockName = blockRegistryName;
        this.chunkSpawnRate = chunkSpawnRate;
        this.maxGroupSize = maxGroupSize;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == block.getSpawnDimension() && random.nextInt(this.chunkSpawnRate)==0) {
            for (int i = 0; i < random.nextInt(this.maxGroupSize); i++) {
                int x = chunkX * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int z = chunkZ * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x,y,z);
                if(y > world.getSeaLevel() && block.canSpawnHere(world, pos, chunkGenerator)) {
                    world.setBlockState(pos, block.getDefaultState());
                    block.afterSpawn(world, pos);
                }
            }
        }
    }

    public void postInitBlockUpdate() {
        this.block = (T) Block.REGISTRY.getObject(this.blockName);
    }
}