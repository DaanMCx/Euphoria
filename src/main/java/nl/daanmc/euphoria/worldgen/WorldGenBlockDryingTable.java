package nl.daanmc.euphoria.worldgen;

import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.daanmc.euphoria.Elements;

import java.util.Random;

public class WorldGenBlockDryingTable implements IWorldGenerator {
    private final WorldGenerator blockGen;

    public WorldGenBlockDryingTable() {
        this.blockGen = new WorldGenMinable(Elements.Blocks.DRYING_TABLE.getDefaultState(), 1, BlockMatcher.forBlock(Blocks.GRASS));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == 0) {
            System.out.println("GENERATING");
            int plantsInGroup = 7 + random.nextInt(7); //beteen 7 and 14 plants per group.
            for(int i = 0; i < plantsInGroup; i++) {
                int x = chunkX * 16 + random.nextInt(20);//in an area of 20x20
                int z = chunkZ * 16 + random.nextInt(20);
                int y = world.getHeight(x, z);
                BlockPos pos = new BlockPos(x,y,z);
                if(y > 0) {
                    blockGen.generate(world, random, pos);
                    System.out.println("GENERATED AT "+pos.toString());
                }
            }
        }
    }
}