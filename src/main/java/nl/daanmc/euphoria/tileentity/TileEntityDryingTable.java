package nl.daanmc.euphoria.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import nl.daanmc.euphoria.Euphoria;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileEntityDryingTable extends TileEntity implements ITickable {
    public TileEntityDryingTable() {
        DRYABLE_ITEMS.put(Euphoria.Content.Items.CANNABIS_BUD, Euphoria.Content.Items.CANNABIS_BUD_DRIED);
    }

    private ItemStackHandler handler = new ItemStackHandler(2);
    private String customName;

    private int dryingProgress, totalDried;
    private float sunLevel, dryingSpeed;

    public static HashMap<Item, Item> DRYABLE_ITEMS = new HashMap<>();

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public String getName() {
        return this.hasCustomName() ? this.customName : "container.drying_table";
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) this.handler : super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.dryingProgress = compound.getInteger("Progress");
        this.totalDried = compound.getInteger("TotalDried");
        this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Progress", this.dryingProgress);
        compound.setInteger("TotalDried", this.totalDried);
        compound.setTag("Inventory", this.handler.serializeNBT());
        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public float getSunLevel() {
        if (this.world.canBlockSeeSky(this.pos)) {
            return (float) Math.max(0.0F, Math.sin((Math.PI*this.world.getWorldTime())/12000));
        } else return 0.0F;
    }

    public int getDryingProgress() {
        return dryingProgress;
    }

    public void setDryingProgress(int dryingProgress) {
        this.dryingProgress = dryingProgress;
        markDirty();
    }

    public int getTotalDried() {
        return totalDried;
    }

    public void setTotalDried(int totalDried) {
        this.totalDried = totalDried;
        markDirty();
    }

    public float getDryingSpeed() {
        return dryingSpeed;
    }

    public void setDryingSpeed(float dryingSpeed) {
        this.dryingSpeed = dryingSpeed;
        markDirty();
    }

    @Override
    public void update() {
        //todo
    }

//    @Override
//    public int[] getSlotsForFace(EnumFacing side) {
//        if (side == EnumFacing.DOWN) {
//            return new int[] {1};
//        } else {
//            return new int[] {0};
//        }
//    }
//
//    @Override
//    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
//        if (index != 0) {
//            return false;
//        } else {
//            return this.isItemValidForSlot(index, itemStackIn);
//        }
//    }
//
//    @Override
//    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
//        return index == 1 && direction == EnumFacing.DOWN;
//    }
//
//    @Override
//    public int getSizeInventory() {
//        return this.dryingTableItemStacks.size();
//    }
//
//    @Override
//    public boolean isEmpty() {
//        for (ItemStack itemstack : this.dryingTableItemStacks) {
//            if (!itemstack.isEmpty()) {
//                return false;
//            }
//        } return true;
//    }
//
//    @Override
//    public ItemStack getStackInSlot(int index) {
//        return this.dryingTableItemStacks.get(index);
//    }
//
//    @Override
//    public ItemStack decrStackSize(int index, int count) {
//        return ItemStackHelper.getAndSplit(this.dryingTableItemStacks, index, count);
//    }
//
//    @Override
//    public ItemStack removeStackFromSlot(int index) {
//        return ItemStackHelper.getAndRemove(this.dryingTableItemStacks, index);
//    }
//
//    @Override
//    public void setInventorySlotContents(int index, ItemStack stack) {
//        ItemStack itemstack = this.dryingTableItemStacks.get(index);
//        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
//        this.dryingTableItemStacks.set(index, stack);
//
//        if (stack.getCount() > this.getInventoryStackLimit()) {
//            stack.setCount(this.getInventoryStackLimit());
//        }
//
//        if (index == 0 && !flag) {
//            //this.totalCookTime = this.getCookTime(stack);
//            //this.cookTime = 0;
//            this.markDirty();
//        }
//    }
//
//    @Override
//    public int getInventoryStackLimit() {
//        return 64;
//    }
//
//    @Override
//    public boolean isItemValidForSlot(int index, ItemStack stack) {
//        return index==0;
//    }
//
//    @Override
//    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
//        return new ContainerDryingTable(playerInventory, this);
//    }
}