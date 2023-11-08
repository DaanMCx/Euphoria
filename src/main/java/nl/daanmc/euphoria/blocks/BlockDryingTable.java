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
import nl.daanmc.euphoria.Elements.DrugSubstances;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.drugs.DrugPresence;

public class BlockDryingTable extends Block {
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
        DrugPresence.activatePresence(new DrugPresence(DrugSubstances.THC, 30, 2400), worldIn);
        return true;
    }
}