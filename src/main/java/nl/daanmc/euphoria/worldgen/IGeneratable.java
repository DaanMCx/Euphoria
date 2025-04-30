package nl.daanmc.euphoria.worldgen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

public interface IGeneratable {
    default int getSpawnDimension() {return 0;}

    boolean canSpawnHere(World worldIn, BlockPos pos, IChunkGenerator chunkGenerator);

    default void afterSpawn(World worldIn, BlockPos pos) {}
}