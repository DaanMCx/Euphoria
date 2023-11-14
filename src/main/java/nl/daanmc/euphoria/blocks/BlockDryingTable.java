package nl.daanmc.euphoria.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.IDrug;

import java.util.ArrayList;
import java.util.HashMap;

public class BlockDryingTable extends Block implements IDrug {
    private final HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();
    public BlockDryingTable() {
        super(Material.WOOD);
        setTranslationKey("drying_table");
        setRegistryName("drying_table");
        setCreativeTab(Tabs.EUPHORIA);
        setHardness(10);
        setLightLevel(1);
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        this.activateDrugPresences(playerIn);
        return true;
    }

    @Override
    public HashMap<String, ArrayList<DrugPresence>> getPresenceTable() {
        return presenceTable;
    }

    @Override
    public boolean isSmokable() {
        return false;
    }
}