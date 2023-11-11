package nl.daanmc.euphoria.drugs;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.ITask;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class DrugPresenceTask implements ITask {
    private final DrugSubstance drugSubstance;
    private final float amount;

    public DrugPresenceTask(DrugSubstance drugSubstance, float amount) {
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