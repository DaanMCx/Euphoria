package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockDrugPlant extends BlockDoublePlant implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 4);
    public BlockDrugPlant(String name) {
        super();
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(AGE, 3).withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, FACING, AGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta<5) {
            return getDefaultState().withProperty(AGE, meta);
        } else return getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(AGE, Math.min(meta-5, 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF)==EnumBlockHalf.LOWER? state.getValue(AGE) : state.getValue(AGE)+5;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }
}