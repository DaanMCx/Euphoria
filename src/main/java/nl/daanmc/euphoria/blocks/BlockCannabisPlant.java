package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockCannabisPlant extends BlockBush implements IGrowable {
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);
    public BlockCannabisPlant(String name) {
        super(Material.VINE);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 3).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, AGE);
    }

    public void placeAt(World worldIn, BlockPos pos, int height) {
        AtomicBoolean canFit = new AtomicBoolean(true);
        for (int i = 0; i < height; i++) {
            if (!worldIn.isAirBlock(pos.up(i)) && !(worldIn.getBlockState(pos).getMaterial() ==Material.PLANTS) && !(worldIn.getBlockState(pos).getMaterial() ==Material.VINE)) {
                canFit.set(false);
            }
        }
        if (canFit.get() && worldIn.getBlockState(pos.down()).getBlock()==Blocks.GRASS) {
            if (height==1) {
                worldIn.setBlockState(pos, Elements.Blocks.CANNABIS_PLANT.getDefaultState());
            } else {
                for (int i = 0; i < height-1; i++) {
                    worldIn.setBlockState(pos.up(i), Elements.Blocks.CANNABIS_PLANT.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(AGE, 4));
                }
                worldIn.setBlockState(pos.up(height-1), Elements.Blocks.CANNABIS_PLANT.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(AGE, 2));
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta<5) {
            return getDefaultState().withProperty(AGE, meta);
        } else return getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(AGE, meta-5);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF)==EnumBlockHalf.LOWER? state.getValue(AGE) : state.getValue(AGE)+5;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND || state.equals(Elements.Blocks.CANNABIS_PLANT.getDefaultState().withProperty(AGE, 4).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return false;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {

    }
}