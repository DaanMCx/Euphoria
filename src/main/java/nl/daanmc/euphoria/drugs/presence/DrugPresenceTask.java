package nl.daanmc.euphoria.drugs.presence;

import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.PacketHandler;
import nl.daanmc.euphoria.util.ScheduledTask;
import nl.daanmc.euphoria.util.EventHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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
        
        
        PacketHandler.INSTANCE.sendTo(new DrugPresenceMsg(this.drugSubstance, player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(this.drugSubstance)), (EntityPlayerMP) player);
        System.out.println("Presence for " + this.drugSubstance.getRegistryName() + " changed to " + this.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(this.drugSubstance));
    }

    @Override
    public void add(int times) {
        for (int i=0; i<times; i++) {
            EventHandler.pendingTasks.add(this);
        }
    }
}
