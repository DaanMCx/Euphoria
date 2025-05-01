package nl.daanmc.euphoria.util.messages;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.util.Drug;
import nl.daanmc.euphoria.util.DrugPresence;

import java.util.ArrayList;

public class MsgDrugPresence implements IMessage {
    public MsgDrugPresence() {}
    public ArrayList<DrugPresence> presenceList = new ArrayList<>();
    public MsgDrugPresence(ArrayList<DrugPresence> presenceList) {
        this.presenceList=presenceList;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(presenceList.size());
        presenceList.forEach(presence -> {
            byte[] stringBytes = presence.drug.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
            buf.writeInt(stringBytes.length);
            buf.writeBytes(stringBytes);
            buf.writeFloat(presence.amount);
            buf.writeInt(presence.delay);
            buf.writeInt(presence.comeUp);
        });
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int listLength = buf.readInt();
        for (int i = 0; i < listLength; i++) {
            int length = buf.readInt();
            byte[] stringData = new byte[length];
            buf.readBytes(stringData);
            Drug drug = Drug.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
            float amount = buf.readFloat();
            int incubation = buf.readInt();
            int delay = buf.readInt();
            presenceList.add(new DrugPresence(drug, amount, incubation, delay));
        }
    }
}