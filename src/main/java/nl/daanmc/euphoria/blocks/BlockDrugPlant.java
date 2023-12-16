package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockDrugPlant extends BlockBush implements IGrowable {
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);
    public static final PropertyBool ISDOUBLE = PropertyBool.create("isdouble");
    private final ItemStack drops;
    public BlockDrugPlant(String name, ItemStack drops) {
        super(Material.PLANTS);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ISDOUBLE, false).withProperty(HALF, EnumBlockHalf.LOWER));
        this.drops = drops;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, ISDOUBLE);
    }

    public void placeAt(World worldIn, BlockPos pos, int height) {
        AtomicBoolean canFit = new AtomicBoolean(true);
        for (int i = 0; i < height; i++) {
            if (!worldIn.isAirBlock(pos.up(i)) && !(worldIn.getBlockState(pos).getMaterial()==Material.PLANTS) && !(worldIn.getBlockState(pos).getMaterial()==Material.VINE)) {
                canFit.set(false);
            }
        }
        if (canFit.get() && worldIn.getBlockState(pos.down()).getBlock()==Blocks.GRASS) {
            if (height==1) {
                worldIn.setBlockState(pos, Elements.Blocks.CANNABIS_PLANT.getDefaultState());
            } else {
                for (int i = 0; i < height-1; i++) {
                    worldIn.setBlockState(pos.up(i), Elements.Blocks.CANNABIS_PLANT.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(ISDOUBLE, true));
                }
                worldIn.setBlockState(pos.up(height-1), Elements.Blocks.CANNABIS_PLANT.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(ISDOUBLE, true));
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
            default: return null;
            case 1: return getDefaultState();
            case 2: return getDefaultState().withProperty(ISDOUBLE, true).withProperty(HALF, EnumBlockHalf.LOWER);
            case 3: return getDefaultState().withProperty(ISDOUBLE, true).withProperty(HALF, EnumBlockHalf.UPPER);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state==getDefaultState()) {
            return 1;
        } else return state.getValue(HALF)==EnumBlockHalf.LOWER? 2 : 3;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        breakBlock(worldIn, pos, worldIn.getBlockState(pos));
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        Blocks.DOUBLE_PLANT.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.FARMLAND || state.equals(this.getDefaultState().withProperty(ISDOUBLE, true).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state==getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER)) {
            worldIn.setBlockToAir(pos.down());
        }
        worldIn.setBlockToAir(pos);
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            breakBlock(worldIn, pos, state);
        }
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
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {}

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.set(fortune, this.drops);
    }
}