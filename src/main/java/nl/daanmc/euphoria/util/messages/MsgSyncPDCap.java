package nl.daanmc.euphoria.util.messages;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.util.Drug;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;

public class MsgSyncPDCap implements IMessage {
    public MsgSyncPDCap() {}

    public IPlayerDrugsCap capability = new PlayerDrugsCap();
    public boolean isInitialSync;

    public MsgSyncPDCap(IPlayerDrugsCap capability) {
        this.capability = capability;
        this.isInitialSync = false;
    }

    public MsgSyncPDCap(IPlayerDrugsCap capability, boolean isInitialSync) {
        this.capability = capability;
        this.isInitialSync = isInitialSync;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(isInitialSync ? 1 : 0);
        buf.writeLong(capability.getClientTick());
        buf.writeInt(capability.getActivePresences().size());
        capability.getActivePresences().forEach(((presence, tick) -> {
            byte[] stringBytes = presence.drug.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(presence.amount);
            buf.writeInt(presence.delay);
            buf.writeInt(presence.comeUp);
            buf.writeLong(tick);
        }));
        buf.writeInt(capability.getPlayerDrugs().size());
        capability.getPlayerDrugs().forEach((drug, amount) -> {
            byte[] stringBytes = drug.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
            buf.writeFloat(capability.getBreakdownAmounts().getOrDefault(drug, 0F));
            buf.writeLong(capability.getBreakdownTicks().getOrDefault(drug, 0L));
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
            Drug drug = Drug.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            int incubation = buf.readInt();
            int delay = buf.readInt();
            long tick = buf.readLong();
            capability.getActivePresences().put(new DrugPresence(drug, amount, incubation, delay), tick);
        }
        mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int stringLength = buf.readInt();
            byte[] stringData = new byte[stringLength];
            buf.readBytes(stringData);
            Drug drug = Drug.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            capability.getPlayerDrugs().put(drug, amount);
            amount = buf.readFloat();
            capability.getBreakdownAmounts().put(drug, amount);
            long tick = buf.readLong();
            capability.getBreakdownTicks().put(drug, tick);
        }
    }
}