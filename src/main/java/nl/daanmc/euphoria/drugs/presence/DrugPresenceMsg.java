package nl.daanmc.euphoria.drugs.presence;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.drugs.DrugSubstance;

public class DrugPresenceMsg implements IMessage {
    public DrugPresenceMsg() {}
    
    public DrugSubstance substance;
    public float amount;
    public int delay;

    public DrugPresenceMsg(DrugSubstance substance, Float amount) {
        this.substance = substance;
        this.amount = amount;
    }

    public DrugPresenceMsg(DrugPresence drugPresencee) {

        this.substance = drugPresencee.substance;
        this.amount = drugPresencee.amount;
        this.delay = drugPresencee.delay;
    }

    @Override
    public void toBytes(ByteBuf buf) {
    
        buf.writeInt(delay);
        buf.writeFloat(amount);
        byte[] stringBytes = substance.getRegistryName().toString().getBytes(CharsetUtil.UTF_8);
        buf.writeInt(stringBytes.length);
        buf.writeBytes(stringBytes);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        delay = buf.readInt();
        amount = buf.readFloat();
        int stringLength = buf.readInt();
        byte[] stringData = new byte[stringLength];
        buf.readBytes(stringData);
        substance = DrugSubstance.REGISTRY.get(new ResourceLocation(new String(stringData, CharsetUtil.UTF_8)));
    }
}
