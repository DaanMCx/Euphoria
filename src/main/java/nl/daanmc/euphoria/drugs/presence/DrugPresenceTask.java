package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.IScheduledTask;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
    public boolean isPersistent() {
        return true;
    }

    @Override
    public void execute() {
        IDrugPresenceCap dpCap = Minecraft.getMinecraft().player.getCapability(DrugPresenceCap.Provider.CAP,null);
        if (this.startBreakdown) {
            dpCap.getBreakdownTickList().put(this.drugSubstance, this.tick);
            dpCap.getBreakdownAmountList().put(this.drugSubstance, dpCap.getDrugPresenceList().get(this.drugSubstance));
        } else {
            dpCap.getBreakdownTickList().put(this.drugSubstance, 0L);
            dpCap.getDrugPresenceList().put(this.drugSubstance, Math.min(Math.max(dpCap.getDrugPresenceList().getOrDefault(this.drugSubstance, 0F) + this.amount, 0F), 100F));
            System.out.println("Added: "+this.drugSubstance.getRegistryName()+" by "+this.amount+" to "+dpCap.getDrugPresenceList().get(this.drugSubstance));
        }

    }

    @Override
    public byte[] serialize() {
        byte typeByte = new Byte("0");
        byte[] substanceBytes = this.drugSubstance.getRegistryName().toString().getBytes(StandardCharsets.UTF_8);
        byte startBreakdownByte = this.startBreakdown ? new Byte("1") : new Byte("0");
        return ByteBuffer.allocate(1+substanceBytes.length+4+4+8+1)
                .put(typeByte)
                .putInt(substanceBytes.length)
                .put(substanceBytes)
                .putFloat(this.amount)
                .putLong(this.tick)
                .put(startBreakdownByte)
                .array();
    }
}