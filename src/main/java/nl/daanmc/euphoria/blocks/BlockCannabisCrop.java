package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements;

import java.util.Random;

public class BlockCannabisCrop extends BlockCrops {
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);
    public BlockCannabisCrop() {
        this.setRegistryName("cannabis_crop");
        this.setTranslationKey("cannabis_crop");
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta<8) {
            return this.withAge(meta);
        } else {
            return this.withAge(meta-8).withProperty(HALF, EnumBlockHalf.UPPER);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getBlock()==this) {
            if (state.getValue(HALF)==EnumBlockHalf.LOWER) {
                return this.getAge(state);
            } else {
                return this.getAge(state) + 8;
            }
        } else return 0;
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
        return 1;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return super.canSustainBush(state) || state.equals(this.getDefaultState().withProperty(AGE, 7).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(HALF)==EnumBlockHalf.LOWER) {
            if ((this.getAge(state)==this.getMaxAge()-1 && worldIn.isAirBlock(pos.up())) || this.getAge(state)<this.getMaxAge()-1) {
                super.grow(worldIn, pos, state);
            } else if (this.getAge(state)==this.getMaxAge()) {
                if (worldIn.isAirBlock(pos.up())) {
                    worldIn.setBlockState(pos.up(), this.withAge(0).withProperty(HALF, EnumBlockHalf.UPPER), 2);
                } else if (worldIn.getBlockState(pos.up()).getBlock() == this) {
                    this.grow(worldIn, pos.up(), state);
                }
            }
        } else {
            worldIn.setBlockState(pos, this.withAge(Math.min(this.getAge(state) + this.getBonemealAgeIncrease(worldIn), this.getMaxAge())).withProperty(HALF, EnumBlockHalf.UPPER));
        }

        super.grow(worldIn, pos, state);
        if (this.getAge(worldIn.getBlockState(pos))==this.getMaxAge() && state.getValue(HALF)==EnumBlockHalf.LOWER && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.withAge(0).withProperty(HALF, EnumBlockHalf.UPPER));
        }
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