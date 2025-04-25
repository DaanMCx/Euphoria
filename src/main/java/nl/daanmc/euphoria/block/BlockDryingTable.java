package nl.daanmc.euphoria.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.tileentity.TileEntityDryingTable;

import javax.annotation.Nullable;

public class BlockDryingTable extends BlockContainer {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public BlockDryingTable() {
        super(Material.WOOD);
        setTranslationKey("drying_table");
        setRegistryName("drying_table");
        setCreativeTab(Euphoria.Content.Tabs.EUPHORIA);
        setHardness(10);
        setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta == 2 ? getDefaultState().withProperty(ACTIVE, true) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state==getDefaultState() ? 1 : 2;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (worldIn.getTileEntity(pos) instanceof TileEntityDryingTable) {
                System.out.println("Opening DryingTable GUI");
                playerIn.openGui(Euphoria.instance, Euphoria.Content.GUI_DRYING_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDryingTable();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (stack.hasDisplayName() && tileEntity instanceof TileEntityDryingTable) {
            ((TileEntityDryingTable) tileEntity).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityDryingTable tileEntity = (TileEntityDryingTable) worldIn.getTileEntity(pos);
        IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        //TODO: drop inventory without InventoryHelper
        super.breakBlock(worldIn, pos, state);
    }
}