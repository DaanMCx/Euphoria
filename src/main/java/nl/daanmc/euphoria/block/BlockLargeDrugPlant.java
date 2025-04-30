package nl.daanmc.euphoria.block;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

public class BlockLargeDrugPlant extends BlockDrugPlant{
    public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.create("half", BlockDoublePlant.EnumBlockHalf.class);

    public BlockLargeDrugPlant(String name) {
        super(name);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
            default: return null;
            case 1: return getDefaultState();
            case 2: return getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state==getDefaultState() ? 1 : 2;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return super.canSustainBush(state) || state.equals(this.getDefaultState());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock()==this && state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            if (worldIn.getBlockState(pos.down()).getBlock() == this) {
                if (player.capabilities.isCreativeMode) {
                    worldIn.setBlockToAir(pos.down());
                } else {
                    if (worldIn.isRemote) {
                        worldIn.setBlockToAir(pos.down());
                    } else if (player.getHeldItemMainhand().getItem() == Items.SHEARS) {
                        spawnAsEntity(worldIn, pos, new ItemStack(this));
                        worldIn.setBlockToAir(pos.down());
                    } else {
                        worldIn.destroyBlock(pos.down(), true);
                    }
                }
            }
        } else {
            if (player.capabilities.isCreativeMode) {
                worldIn.setBlockToAir(pos);
            } else {
                if (worldIn.isRemote) {
                    worldIn.setBlockToAir(pos);
                } else if (player.getHeldItemMainhand().getItem() == Items.SHEARS) {
                    spawnAsEntity(worldIn, pos, new ItemStack(this));
                    worldIn.setBlockToAir(pos);
                } else {
                    worldIn.destroyBlock(pos, true);
                }
            }
        }
    }

    @Override
    public boolean canSpawnHere(World worldIn, BlockPos pos, IChunkGenerator chunkGenerator) {
        boolean canFit = worldIn.isAirBlock(pos) && worldIn.isAirBlock(pos.up());
        boolean rightLocation = worldIn.getBlockState(pos.down()).getBlock()== Blocks.GRASS && worldIn.getBiome(pos).getTemperature(pos) > 0.6F;
        return canFit && rightLocation;
    }

    @Override
    public void afterSpawn(World worldIn, BlockPos pos) {
        worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER));
    }
}