package nl.daanmc.euphoria.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerDryingTable extends Container {
    private final IInventory tileDryingTable;
    private int dryingSpeed;
    private int dryingProgress;
    private int totalDried;

    public ContainerDryingTable(InventoryPlayer playerInventory, IInventory dryingTableInventory) {
        this.dryingSpeed = 0;
        this.dryingProgress = 0;
        this.totalDried = 0;

        this.tileDryingTable = dryingTableInventory;
        this.addSlotToContainer(new Slot(dryingTableInventory, 0, 10, 10));
        this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, dryingTableInventory, 1, 100, 10));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileDryingTable);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener icontainerlistener : this.listeners) {
            if (this.dryingSpeed != this.tileDryingTable.getField(0)) {
                icontainerlistener.sendWindowProperty(this, 0, this.tileDryingTable.getField(0));
            }

            if (this.dryingProgress != this.tileDryingTable.getField(1)) {
                icontainerlistener.sendWindowProperty(this, 1, this.tileDryingTable.getField(1));
            }

            if (this.totalDried != this.tileDryingTable.getField(2)) {
                icontainerlistener.sendWindowProperty(this, 2, this.tileDryingTable.getField(2));
            }
        }
        this.dryingSpeed = this.tileDryingTable.getField(0);
        this.dryingProgress = this.tileDryingTable.getField(1);
        this.totalDried = this.tileDryingTable.getField(2);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        this.tileDryingTable.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileDryingTable.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        //TODO: fix all this copy-paste from ContainerFurnace
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}