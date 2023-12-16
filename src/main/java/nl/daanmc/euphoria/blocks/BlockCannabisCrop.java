package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements;

import java.util.Random;

public class BlockCannabisCrop extends BlockCrops {
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);
    public BlockCannabisCrop() {
        this.setRegistryName("cannabis_crops");
        this.setTranslationKey("cannabis_crops");
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this.getAge(state)==6 && worldIn.isAirBlock(pos.up()) && state.getValue(HALF)==EnumBlockHalf.LOWER) {
            super.updateTick(worldIn, pos, state, rand);
            if (this.getAge(state)==this.getMaxAge()) {
                worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(AGE, 0).withProperty(HALF, EnumBlockHalf.UPPER));
            }
        }
    }

    @Override
    protected int getBonemealAgeIncrease(World worldIn) {
        return MathHelper.getInt(worldIn.rand, 1, 2);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return super.canSustainBush(state) || state.equals(this.getDefaultState().withProperty(AGE, 7).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        super.grow(worldIn, pos, state);
    }

    @Override
    protected Item getSeed() {
        return Elements.Items.CANNABIS_SEEDS;
    }

    @Override
    protected Item getCrop() {
        return Elements.Items.CANNABIS_BUD;
    }
}