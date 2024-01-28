package nl.daanmc.euphoria.tileentity.inventory.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import nl.daanmc.euphoria.Euphoria;

public class GuiDryingTable extends GuiContainer {
    private static final ResourceLocation DRYINGTABLE_GUI_TEXTURES = new ResourceLocation(Euphoria.MODID,"textures/gui/container/dryingtable.png");
    private final InventoryPlayer playerInventory;
    private final IInventory tileDryingTable;

    public GuiDryingTable(InventoryPlayer playerInv, IInventory furnaceInv) {
        super(new ContainerFurnace(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileDryingTable = furnaceInv;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}