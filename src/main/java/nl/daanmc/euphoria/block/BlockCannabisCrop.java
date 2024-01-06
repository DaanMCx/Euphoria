package nl.daanmc.euphoria.block;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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
        if (state.getValue(HALF)==EnumBlockHalf.LOWER) {
            if ((this.getAge(state)==this.getMaxAge()-1 && worldIn.isAirBlock(pos.up())) || this.getAge(state)<this.getMaxAge()-1) {
                super.updateTick(worldIn, pos, state, rand);
            }
            if (this.isMaxAge(worldIn.getBlockState(pos))) {
                if (worldIn.isAirBlock(pos.up())) {
                    worldIn.setBlockState(pos.up(), this.withAge(0).withProperty(HALF, EnumBlockHalf.UPPER));
                }
            }
        } else {
            super.updateTick(worldIn, pos, state, rand);
            IBlockState newState = worldIn.getBlockState(pos);
            if (newState.getBlock()==this && newState.getValue(HALF)==EnumBlockHalf.LOWER) {
                worldIn.setBlockState(pos, this.withAge(newState.getValue(AGE)).withProperty(HALF, EnumBlockHalf.UPPER), 2);
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (state.getValue(HALF)==EnumBlockHalf.UPPER) {
            worldIn.setBlockState(pos.down(), this.withAge(6), 2);
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !isDoubleCropFullyGrown(worldIn, pos, state);
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
            }
            if (this.isMaxAge(worldIn.getBlockState(pos))) {
                if (worldIn.isAirBlock(pos.up())) {
                    worldIn.setBlockState(pos.up(), this.withAge(0).withProperty(HALF, EnumBlockHalf.UPPER));
                } else if (worldIn.getBlockState(pos.up()).getBlock() == this) {
                    this.grow(worldIn, pos.up(), worldIn.getBlockState(pos.up()));
                }
            }
        } else {
            worldIn.setBlockState(pos, this.withAge(Math.min(this.getAge(state) + this.getBonemealAgeIncrease(worldIn), this.getMaxAge())).withProperty(HALF, EnumBlockHalf.UPPER));
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF)==EnumBlockHalf.UPPER? super.getItemDropped(state, rand, fortune) : this.getSeed();
    }

    @Override
    protected Item getSeed() {
        return Elements.Items.CANNABIS_SEEDS;
    }

    @Override
    protected Item getCrop() {
        return Elements.Items.CANNABIS_BUD;
    }

    public static boolean isDoubleCropFullyGrown(World worldin, BlockPos pos, IBlockState state) {
        if (state.getValue(HALF)==EnumBlockHalf.LOWER) {
            if (worldin.getBlockState(pos.up()).getBlock()==state.getBlock()) {
                return worldin.getBlockState(pos.up()).getValue(AGE)==7;
            } else return false;
        } else {
            return state.getValue(AGE)==7;
        }
    }
}