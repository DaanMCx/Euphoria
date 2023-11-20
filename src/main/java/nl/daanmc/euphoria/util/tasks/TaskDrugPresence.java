package nl.daanmc.euphoria.util.tasks;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class TaskDrugPresence implements ITask {
    private final DrugSubstance drugSubstance;
    private final float amount;

    public TaskDrugPresence(DrugSubstance drugSubstance, float amount) {
        this.drugSubstance = drugSubstance;
        this.amount = amount;
    }

    @Override
    public void execute() {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drugSubstance, 0L);
        drugCap.getDrugs().put(drugSubstance, Math.min(Math.max(drugCap.getDrugs().getOrDefault(drugSubstance, 0F) + amount, 0F), 100F));
        //TODO remove
        System.out.println("Added: "+drugSubstance.getRegistryName()+" by "+amount+" to "+drugCap.getDrugs().get(drugSubstance));
    }
}