package nl.daanmc.euphoria.util;

import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceTask;

import java.nio.ByteBuffer;

public interface IScheduledTask {
    long getTick();
    boolean isPersistent();
    void execute();
    byte[] serialize();
    static IScheduledTask deserialize(byte[] byteArray) {
        ByteBuffer buf = ByteBuffer.wrap(byteArray);
        byte[] typeByte = new byte[1];
        buf.get(typeByte);
        String type = new String(typeByte);
        if (type.equals("0")) {
            int stringLength = buf.getInt();
            byte[] stringData = new byte[stringLength];
            buf.get(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.getFloat();
            long tick = buf.getLong();
            byte[] boolData = new byte[1];
            buf.get(boolData);
            String boolValue = new String(boolData);
            boolean startBreakdown = boolValue.equals("1");
            return startBreakdown ? new DrugPresenceTask(substance, tick) : new DrugPresenceTask(substance, amount, tick);
        } else {return null;}

    }
}