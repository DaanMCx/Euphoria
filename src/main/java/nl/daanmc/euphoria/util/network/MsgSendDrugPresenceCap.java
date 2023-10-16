package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCap;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;

import java.util.HashMap;

public class MsgSendDrugPresenceCap implements IMessage {
    public MsgSendDrugPresenceCap() {}

    public IDrugPresenceCap capability = new DrugPresenceCap();

    public MsgSendDrugPresenceCap(IDrugPresenceCap cap) {
        this.capability = cap;
    }

    //TODO: Fix serialization
    @Override
    public void toBytes(ByteBuf buf) {
        HashMap<DrugSubstance, Float> presenceList = this.capability.getPresenceList();
        buf.writeInt(presenceList.size());
        presenceList.forEach((drugSubstance, amount) -> {
            byte[] stringBytes = drugSubstance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(amount);
        });
    }

    //TODO: Fix deserialization
    @Override
    public void fromBytes(ByteBuf buf) {
        int mapSize = buf.readInt();
        for (int i = 0; i < mapSize; i++) {
            int stringLength = buf.readInt();
            byte[] stringData = new byte[stringLength];
            buf.readBytes(stringData);
            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            this.capability.getPresenceList().put(substance, amount);
        }
    }
}