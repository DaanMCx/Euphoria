package nl.daanmc.euphoria.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenBush;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.daanmc.euphoria.Elements;

import java.util.Random;

public class WorldGenDrugPlants implements IWorldGenerator {
    private final WorldGenerator gen;
    public WorldGenDrugPlants() {
        this.gen = new WorldGenBush(Elements.Blocks.CANNABIS_PLANT);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            int plantsInGroup = 7 + random.nextInt(7); //beteen 7 and 14 plants per group.
            for(int i = 0; i < plantsInGroup; i++) {
                int x = chunkX * 16 + random.nextInt(20);//in an area of 20x20
                int z = chunkZ * 16 + random.nextInt(20);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x,y,z);
                if(y > 0) {
                    gen.generate(world, random, pos);
                }
            }
        }
    }
}