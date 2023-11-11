package nl.daanmc.euphoria.drugs;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.ITask;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class DrugBreakdownTask implements ITask {
    private final DrugSubstance drugSubstance;

    public DrugBreakdownTask(DrugSubstance drugSubstance) {
        this.drugSubstance = drugSubstance;
    }

    @Override
    public void execute() {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drugSubstance, drugCap.getClientTick());
        drugCap.getBreakdownAmounts().put(drugSubstance, drugCap.getDrugs().get(drugSubstance));
        //TODO remove
        System.out.println("BreakdownTask exec. for "+drugSubstance.getRegistryName());
    }
}