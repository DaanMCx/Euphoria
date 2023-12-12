package nl.daanmc.euphoria.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.daanmc.euphoria.Elements;

import java.util.Random;

public class WorldGenCannabisPlant implements IWorldGenerator {
    public WorldGenCannabisPlant() {}

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0 && random.nextInt(24)==0) {
            for (int i = 0; i < random.nextInt(4); i++) {
                int x = chunkX * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int z = chunkZ * 16 + 8 + random.nextInt(8) - random.nextInt(8);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x,y,z);
                if(y>world.getSeaLevel() && world.getBiome(pos).getTemperature(pos)>0.5F) {
                    Elements.Blocks.CANNABIS_PLANT.placeAt(world, pos, 2);
                }
            }
        }
    }
}