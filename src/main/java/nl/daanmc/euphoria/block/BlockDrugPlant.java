package nl.daanmc.euphoria.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.gen.IChunkGenerator;
import nl.daanmc.euphoria.worldgen.IGeneratable;

import java.util.Random;

public class BlockDrugPlant extends BlockBush implements IGeneratable {
    private Item drops;

    public BlockDrugPlant(String name) {
        super(Material.PLANTS);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setSoundType(SoundType.PLANT);
    }

    public void setDrops(Item drops) {
        this.drops=drops;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
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
        if (state.getBlock()==this) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        if (worldIn.isRemote || player.capabilities.isCreativeMode) {
            worldIn.setBlockToAir(pos);
        } else if (player.getHeldItemMainhand().getItem() == Items.SHEARS) {
            spawnAsEntity(worldIn, pos, new ItemStack(this));
        } else {
            worldIn.destroyBlock(pos, worldIn.rand.nextInt(3) == 0);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return this.drops;
    }

    @Override
    protected boolean canSustainBush(IBlockState state) {
        return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT;
    }

    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState();
    }

    @Override
    public boolean canSpawnHere(World worldIn, BlockPos pos, IChunkGenerator chunkGenerator) {
        return worldIn.isAirBlock(pos) && worldIn.getBlockState(pos.down()).getBlock()== Blocks.GRASS;
    }
}