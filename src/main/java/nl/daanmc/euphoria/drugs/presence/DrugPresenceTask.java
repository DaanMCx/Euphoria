package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.entity.player.EntityPlayer;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.ScheduledTask;

public class DrugPresenceTask implements ScheduledTask {
    private final DrugSubstance drugSubstance;
    private final float amount;
    private final long tick;
    private final EntityPlayer player;
    public DrugPresenceTask(DrugSubstance drugSubstance, float amount, long tick, EntityPlayer player) {
        this.drugSubstance = drugSubstance;
        this.amount = amount;
        this.tick = tick;
        this.player = player;
    }

    @Override
    public long getTick() {
        return this.tick;
    }

    @Override
    public void execute() {
        this.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(this.drugSubstance, Math.min(Math.max(this.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().getOrDefault(this.drugSubstance, 0F) + this.amount, 0F), 100F));
    }
}