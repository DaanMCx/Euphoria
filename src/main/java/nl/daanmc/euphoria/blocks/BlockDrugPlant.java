package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockDrugPlant extends BlockBush {
    public static final PropertyEnum<EnumBlockHalf> HALF = PropertyEnum.create("half", EnumBlockHalf.class);
    public static final PropertyBool ISDOUBLE = PropertyBool.create("isdouble");
    private Item drops;
    public BlockDrugPlant(String name) {
        super(Material.PLANTS);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setSoundType(SoundType.PLANT);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ISDOUBLE, false).withProperty(HALF, EnumBlockHalf.LOWER));
    }

    public void setDrops(Item drops) {
        this.drops=drops;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF, ISDOUBLE);
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

    public void placeAt(World worldIn, BlockPos pos, int height) {
        AtomicBoolean canFit = new AtomicBoolean(true);
        for (int i = 0; i < height; i++) {
            if (!worldIn.isAirBlock(pos.up(i)) && !(worldIn.getBlockState(pos).getMaterial()==Material.PLANTS) && !(worldIn.getBlockState(pos).getMaterial()==Material.VINE)) {
                canFit.set(false);
            }
        }
        if (canFit.get() && worldIn.getBlockState(pos.down()).getBlock()==Blocks.GRASS) {
            if (height==1) {
                worldIn.setBlockState(pos, this.getDefaultState());
            } else {
                for (int i = 0; i < height-1; i++) {
                    worldIn.setBlockState(pos.up(i), this.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER).withProperty(ISDOUBLE, true));
                }
                worldIn.setBlockState(pos.up(height-1), this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(ISDOUBLE, true));
            }
        }
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos, state)) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock()==this && state.getValue(HALF)==EnumBlockHalf.LOWER) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (state.getValue(HALF) == EnumBlockHalf.UPPER) {
            if (worldIn.getBlockState(pos.down()).getBlock() == this) {
                if (player.capabilities.isCreativeMode) {
                    worldIn.setBlockToAir(pos.down());
                } else {
                    if (worldIn.isRemote) {
                        worldIn.setBlockToAir(pos.down());
                    } else if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == Items.SHEARS) {
                        spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this)));
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
                } else if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == Items.SHEARS) {
                    spawnAsEntity(worldIn, pos, new ItemStack(Item.getItemFromBlock(this)));
                    worldIn.setBlockToAir(pos);
                } else {
                    worldIn.destroyBlock(pos, true);
                }
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.drops;
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
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return world.getBiome(pos).getTemperature(pos)>0.6F? this.getDefaultState().withProperty(ISDOUBLE, true) : this.getDefaultState();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (state.getValue(ISDOUBLE)) {
            worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER).withProperty(ISDOUBLE, true), 2);
        }
    }
}