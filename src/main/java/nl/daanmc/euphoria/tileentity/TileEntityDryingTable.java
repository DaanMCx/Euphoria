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
import nl.daanmc.euphoria.tileentity.inventory.ContainerDryingTable;

public class TileEntityDryingTable extends TileEntityLockable implements ITickable, ISidedInventory {
    public TileEntityDryingTable() {}

    private NonNullList<ItemStack> dryingTableItemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private int dryingSpeed;
    private int dryingProgress;
    private int totalDried;
    private String dryingTableCustomName;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.dryingTableItemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.dryingTableItemStacks);
        this.dryingSpeed = compound.getInteger("Speed");
        this.dryingProgress = compound.getInteger("Progress");
        this.totalDried = compound.getInteger("TotalDried");
        if (compound.hasKey("CustomName", 8)) {
            this.dryingTableCustomName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Speed", this.dryingSpeed);
        compound.setInteger("Progress", this.dryingProgress);
        compound.setInteger("TotalDried", this.totalDried);
        ItemStackHelper.saveAllItems(compound, this.dryingTableItemStacks);
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.dryingTableCustomName);
        }
        return compound;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) {
            return new int[] {1};
        } else {
            return new int[] {0};
        }
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (index != 0) {
            return false;
        } else {
            return this.isItemValidForSlot(index, itemStackIn);
        }
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == 1 && direction == EnumFacing.DOWN;
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
        ItemStack itemstack = this.dryingTableItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        this.dryingTableItemStacks.set(index, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

        if (index == 0 && !flag) {
            //this.totalCookTime = this.getCookTime(stack);
            //this.cookTime = 0;
            this.markDirty();
        }
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
        switch (id) {
            case 0:
                return this.dryingSpeed;
            case 1:
                return this.dryingProgress;
            case 2:
                return this.totalDried;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.dryingSpeed = value;
                break;
            case 1:
                this.dryingProgress = value;
                break;
            case 2:
                this.totalDried = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 3;
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
        return "euphoria:drying_table";
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.dryingTableCustomName : "container.drying_table";
    }

    @Override
    public boolean hasCustomName() {
        return this.dryingTableCustomName != null && !this.dryingTableCustomName.isEmpty();
    }

    public float getSunLevel() {
        if (this.world.canBlockSeeSky(this.pos)) {
            return (float) Math.max(0.0F, Math.sin((Math.PI*this.world.getWorldTime())/12000));
        } else return 0.0F;
    }
}