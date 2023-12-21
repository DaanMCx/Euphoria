package nl.daanmc.euphoria.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.daanmc.euphoria.Elements.Blocks;

import java.util.Random;

public class WorldGenCannabisPlant implements IWorldGenerator {
    public WorldGenCannabisPlant() {}

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0 && random.nextInt(50)==0) {
            for (int i = 0; i < random.nextInt(6); i++) {
                int x = chunkX * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int z = chunkZ * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x,y,z);
                if(y>world.getSeaLevel()) {
                    if (world.getBiome(pos).getTemperature(pos)>0.6F) {
                        Blocks.CANNABIS_PLANT.placeAt(world, pos, 2);
                    } else {
                        Blocks.CANNABIS_PLANT.placeAt(world, pos, 1);
                    }
                }
            }
        }
    }
}