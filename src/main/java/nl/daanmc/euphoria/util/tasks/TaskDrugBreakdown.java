package nl.daanmc.euphoria.util.tasks;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;

public class TaskDrugBreakdown implements ITask {
    private final DrugSubstance drugSubstance;

    public TaskDrugBreakdown(DrugSubstance drugSubstance) {
        this.drugSubstance = drugSubstance;
    }

    @Override
    public void execute() {
        IPlayerDrugsCap drugCap = Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drugSubstance, drugCap.getClientTick());
        drugCap.getBreakdownAmounts().put(drugSubstance, drugCap.getPlayerDrugs().get(drugSubstance));
        //TODO remove
        System.out.println("BreakdownTask exec. for "+drugSubstance.getRegistryName());
    }
}