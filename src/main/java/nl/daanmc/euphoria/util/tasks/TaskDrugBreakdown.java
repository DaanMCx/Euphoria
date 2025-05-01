package nl.daanmc.euphoria.util.tasks;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.Drug;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;

public class TaskDrugBreakdown implements ITask {
    private final Drug drug;

    public TaskDrugBreakdown(Drug drug) {
        this.drug = drug;
    }

    @Override
    public void execute() {
        IPlayerDrugsCap drugCap = Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        drugCap.getBreakdownTicks().put(drug, drugCap.getClientTick());
        drugCap.getBreakdownAmounts().put(drug, drugCap.getPlayerDrugs().get(drug));
        //TODO remove
        System.out.println("BreakdownTask exec. for "+ drug.getRegistryName());
    }
}