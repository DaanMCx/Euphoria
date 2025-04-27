package nl.daanmc.euphoria.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.tileentity.TileEntityDryingTable;
import nl.daanmc.euphoria.tileentity.inventory.ContainerDryingTable;

public class GuiDryingTable extends GuiContainer {
    private static final ResourceLocation DRYINGTABLE_GUI_TEXTURES = new ResourceLocation(Euphoria.MODID,"textures/gui/container/dryingtable.png");
    private final InventoryPlayer playerInventory;
    private final TileEntityDryingTable tileDryingTable;

    public GuiDryingTable(InventoryPlayer playerInv, TileEntityDryingTable dryingTable) {
        super(new ContainerDryingTable(playerInv, dryingTable.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)));
        this.playerInventory = playerInv;
        this.tileDryingTable = dryingTable;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String tileName = this.tileDryingTable.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2) + 3, 8, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 122, this.ySize - 96 + 2, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(DRYINGTABLE_GUI_TEXTURES);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    private int getSunLevelScaled(int pixels) {
        return (int) (this.tileDryingTable.getSunLevel() * pixels);
    }

    private int getDryingProgressScaled(int pixels) {
        float relativeProgress = (float) this.tileDryingTable.getDryingProgress() / this.tileDryingTable.getCurrentDryingTime();
        return (int) (relativeProgress * pixels);
    }
}