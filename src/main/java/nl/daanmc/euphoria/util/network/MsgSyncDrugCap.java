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

    public MsgSyncDrugCap(IDrugCap cap) {
        this.capability = cap;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(capability.getClientTicks());
        buf.writeInt(capability.getActivePresences().size());
        capability.getActivePresences().forEach(((presence, tick) -> {
            System.out.println(presence == null);
            System.out.println(presence.substance == null);
            byte[] stringBytes = presence.substance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(presence.amount);
            buf.writeInt(presence.delay);
            buf.writeLong(tick);
        }));
        buf.writeInt(this.capability.getDrugs().size());
        this.capability.getDrugs().forEach((drugSubstance, amount) -> {
            byte[] stringBytes = drugSubstance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
            buf.writeFloat(this.capability.getBreakdownAmounts().getOrDefault(drugSubstance, 0F));
            buf.writeLong(this.capability.getBreakdownTicks().getOrDefault(drugSubstance, 0L));
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        capability.setClientTicks(buf.readLong());
        int mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int length = buf.readInt();
            byte[] stringData = new byte[length];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            int delay = buf.readInt();
            long tick = buf.readLong();
            capability.getActivePresences().put(new DrugPresence(substance, amount, delay), tick);
        }
        mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int stringLength = buf.readInt();
            byte[] stringData = new byte[stringLength];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            this.capability.getDrugs().put(substance, amount);
            amount = buf.readFloat();
            this.capability.getBreakdownAmounts().put(substance, amount);
            long tick = buf.readLong();
            this.capability.getBreakdownTicks().put(substance, tick);
        }
    }
}