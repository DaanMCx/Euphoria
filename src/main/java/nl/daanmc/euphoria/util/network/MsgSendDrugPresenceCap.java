package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCap;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;

public class MsgSendDrugPresenceCap implements IMessage {
    public MsgSendDrugPresenceCap() {}

    public IDrugPresenceCap capability = new DrugPresenceCap();

    public MsgSendDrugPresenceCap(IDrugPresenceCap cap) {
        this.capability = cap;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.capability.getDrugPresenceList().size());
        this.capability.getDrugPresenceList().forEach((drugSubstance, amount) -> {
            byte[] stringBytes = drugSubstance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
            buf.writeFloat(this.capability.getBreakdownAmountList().getOrDefault(drugSubstance, 0F));
            buf.writeLong(this.capability.getBreakdownTickList().getOrDefault(drugSubstance, 0L));
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int stringLength = buf.readInt();
            byte[] stringData = new byte[stringLength];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            this.capability.getDrugPresenceList().put(substance, amount);
            amount = buf.readFloat();
            this.capability.getBreakdownAmountList().put(substance, amount);
            long tick = buf.readLong();
            this.capability.getBreakdownTickList().put(substance, tick);
        }
    }
}