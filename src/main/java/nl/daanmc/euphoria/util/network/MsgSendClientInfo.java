package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.drugs.DrugPresence;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

import java.util.HashMap;

public class MsgSendClientInfo implements IMessage {
    public MsgSendClientInfo() {}
    public IDrugCap capability = new DrugCap();
    public HashMap<DrugPresence, Long> activePresences = new HashMap<>();
    public long clientTicks;

    public MsgSendClientInfo(IDrugCap capabilityIn, HashMap<DrugPresence, Long> activePresences, long clientTicksIn) {
        this.capability = capabilityIn;
        this.activePresences = activePresences;
        this.clientTicks = clientTicksIn;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(clientTicks);
        buf.writeInt(capability.getDrugs().size());
        capability.getDrugs().forEach((drugSubstance, amount) -> {
            byte[] stringBytes = drugSubstance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
            buf.writeFloat(capability.getBreakdownAmounts().getOrDefault(drugSubstance, 0F));
            buf.writeLong(capability.getBreakdownTicks().getOrDefault(drugSubstance, 0L));
        });
        buf.writeInt(activePresences.size());
        activePresences.forEach(((presence, tick) -> {
            System.out.println(presence == null);
            System.out.println(presence.substance == null);
            byte[] stringBytes = presence.substance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(presence.amount);
            buf.writeInt(presence.delay);
            buf.writeLong(tick);
        }));
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientTicks = buf.readLong();
        int mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int stringLength = buf.readInt();
            byte[] stringData = new byte[stringLength];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            capability.getDrugs().put(substance, amount);
            amount = buf.readFloat();
            capability.getBreakdownAmounts().put(substance, amount);
            long tick = buf.readLong();
            capability.getBreakdownTicks().put(substance, tick);
        }
        mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int length = buf.readInt();
            byte[] stringData = new byte[length];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            int delay = buf.readInt();
            long tick = buf.readLong();
            activePresences.put(new DrugPresence(substance, amount, delay), tick);
        }
    }
}