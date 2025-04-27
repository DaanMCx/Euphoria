package nl.daanmc.euphoria.tileentity.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDryingTable extends Container {
    private final IItemHandler inventory;
    private int dryingSpeed;
    private int dryingProgress;
    private int totalDried;

    public ContainerDryingTable(InventoryPlayer playerInventory, IItemHandler dryingTableInventory) {
        this.dryingSpeed = 0;
        this.dryingProgress = 0;
        this.totalDried = 0;
        this.inventory = dryingTableInventory;

        this.addSlotToContainer(new SlotItemHandler(this.inventory, 0, 10, 10));
        this.addSlotToContainer(new SlotItemHandler(this.inventory, 1, 100, 10));

        for (int i = 0; i < 3; ++i) { //Player inventory
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) { //Player toolbar
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true; //TODO: this may cause issues in MP
    }
}