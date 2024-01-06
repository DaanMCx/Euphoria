package nl.daanmc.euphoria.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.daanmc.euphoria.inventory.ContainerDryingTable;

public class TileEntityDryingTable extends TileEntityLockable implements ITickable, ISidedInventory {
    public TileEntityDryingTable() {}

    private NonNullList<ItemStack> dryingTableItemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private String dryingTableCustomName;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.dryingTableItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.dryingTableItemStacks);
        //todo
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        //todo
        return compound;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        //todo
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        //todo
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        //todo
        return false;
    }

    @Override
    public int getSizeInventory() {
        return this.dryingTableItemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.dryingTableItemStacks) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        } return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.dryingTableItemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.dryingTableItemStacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.dryingTableItemStacks, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        //todo
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {
        //todo??
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        //todo??
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index==0;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.dryingTableItemStacks.clear();
    }

    @Override
    public void update() {
        //todo
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerDryingTable(playerInventory, this);
    }

    @Override
    public String getGuiID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    public float getSunLevel(World worldIn, BlockPos pos) {
        if (worldIn.canBlockSeeSky(pos)) {
            return (float) Math.max(0.0F, Math.sin((Math.PI*worldIn.getWorldTime())/12000));
        } else return 0.0F;
    }
}