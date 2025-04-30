package nl.daanmc.euphoria.util.tasks;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;

public class TaskDrugPresence implements ITask {
    private final DrugSubstance drugSubstance;
    private final float amount;

    public TaskDrugPresence(DrugSubstance drugSubstance, float amount) {
        this.drugSubstance = drugSubstance;
        this.amount = amount;
    }

    @Override
    public void execute() {
        IPlayerDrugsCap drugCap = Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drugSubstance, 0L);
        drugCap.getPlayerDrugs().put(drugSubstance, Math.min(Math.max(drugCap.getPlayerDrugs().getOrDefault(drugSubstance, 0F) + amount, 0F), 100F));
        //TODO remove
        System.out.println("Added: "+drugSubstance.getRegistryName()+" by "+amount+" to "+drugCap.getPlayerDrugs().get(drugSubstance));
    }
}