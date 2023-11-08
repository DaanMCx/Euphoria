package nl.daanmc.euphoria.drugs;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.IScheduledTask;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class DrugPresenceTask implements IScheduledTask {
    private final DrugSubstance drugSubstance;
    private final float amount;
    private final long tick;
    private final boolean startBreakdown;
    public DrugPresenceTask(DrugSubstance drugSubstance, float amount, long tick) {
        this.drugSubstance = drugSubstance;
        this.amount = amount;
        this.tick = tick;
        this.startBreakdown = false;
    }
    public DrugPresenceTask(DrugSubstance drugSubstance, long tick) {
        this.drugSubstance = drugSubstance;
        this.startBreakdown = true;
        this.tick = tick;
        this.amount = 0F;
    }

    @Override
    public long getTick() {
        return tick;
    }

    @Override
    public void execute() {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        if (startBreakdown) {
            drugCap.getBreakdownTicks().put(drugSubstance, tick);
            drugCap.getBreakdownAmounts().put(drugSubstance, drugCap.getDrugs().get(drugSubstance));
        } else {
            drugCap.getBreakdownTicks().put(drugSubstance, 0L);
            drugCap.getDrugs().put(drugSubstance, Math.min(Math.max(drugCap.getDrugs().getOrDefault(drugSubstance, 0F) + amount, 0F), 100F));
            System.out.println("Added: "+drugSubstance.getRegistryName()+" by "+amount+" to "+drugCap.getDrugs().get(drugSubstance));
        }

    }
}