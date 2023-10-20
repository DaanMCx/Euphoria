package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.IScheduledTask;

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
        return this.tick;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void execute() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (this.startBreakdown) {
            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().put(this.drugSubstance, this.tick);
            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownAmountList().put(this.drugSubstance, player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(this.drugSubstance));
        } else {
            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().put(this.drugSubstance, 0L);
            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(this.drugSubstance, Math.min(Math.max(player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().getOrDefault(this.drugSubstance, 0F) + this.amount, 0F), 100F));
            System.out.println("Added: "+this.drugSubstance.getRegistryName()+" by "+this.amount+" to "+player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(this.drugSubstance));
        }

    }
}