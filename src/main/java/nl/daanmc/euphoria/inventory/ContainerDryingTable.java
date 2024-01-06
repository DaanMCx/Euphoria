package nl.daanmc.euphoria.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;

public class ContainerDryingTable extends Container {
    private final IInventory tileDryingTable;

    public ContainerDryingTable(InventoryPlayer playerInventory, IInventory dryingTableInventory) {
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
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileDryingTable.isUsableByPlayer(playerIn);
    }
}