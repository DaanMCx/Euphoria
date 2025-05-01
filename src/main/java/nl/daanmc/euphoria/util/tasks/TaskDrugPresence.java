package nl.daanmc.euphoria.util.tasks;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.Drug;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;

public class TaskDrugPresence implements ITask {
    private final Drug drug;
    private final float amount;

    public TaskDrugPresence(Drug drug, float amount) {
        this.drug = drug;
        this.amount = amount;
    }

    @Override
    public void execute() {
        IPlayerDrugsCap drugCap = Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drug, 0L);
        drugCap.getPlayerDrugs().put(drug, Math.min(Math.max(drugCap.getPlayerDrugs().getOrDefault(drug, 0F) + amount, 0F), 100F));
        //TODO remove
        System.out.println("Added: "+ drug.getRegistryName()+" by "+amount+" to "+drugCap.getPlayerDrugs().get(drug));
    }
}