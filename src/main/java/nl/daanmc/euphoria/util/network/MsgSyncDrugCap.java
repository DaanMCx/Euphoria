package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.drugs.DrugPresence;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class MsgSyncDrugCap implements IMessage {
    public MsgSyncDrugCap() {}

    public IDrugCap capability = new DrugCap();
    public boolean isInitialSync;

    public MsgSyncDrugCap(IDrugCap capability) {
        this.capability = capability;
        this.isInitialSync = false;
    }

    public MsgSyncDrugCap(IDrugCap capability, boolean isInitialSync) {
        this.capability = capability;
        this.isInitialSync = isInitialSync;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(isInitialSync ? 1 : 0);
        buf.writeLong(capability.getClientTick());
        buf.writeInt(capability.getActivePresences().size());
        capability.getActivePresences().forEach(((presence, tick) -> {
            byte[] stringBytes = presence.substance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(presence.amount);
            buf.writeInt(presence.incubation);
            buf.writeInt(presence.delay);
            buf.writeLong(tick);
        }));
        buf.writeInt(capability.getDrugs().size());
        capability.getDrugs().forEach((drugSubstance, amount) -> {
            byte[] stringBytes = drugSubstance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
            buf.writeFloat(capability.getBreakdownAmounts().getOrDefault(drugSubstance, 0F));
            buf.writeLong(capability.getBreakdownTicks().getOrDefault(drugSubstance, 0L));
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isInitialSync = buf.readByte() == 1;
        capability.setClientTick(buf.readLong());
        capability.getActivePresences().clear();
        int mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int length = buf.readInt();
            byte[] stringData = new byte[length];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            int incubation = buf.readInt();
            int delay = buf.readInt();
            long tick = buf.readLong();
            capability.getActivePresences().put(new DrugPresence(substance, amount, incubation, delay), tick);
        }
        mapSize = buf.readInt();
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
    }
}