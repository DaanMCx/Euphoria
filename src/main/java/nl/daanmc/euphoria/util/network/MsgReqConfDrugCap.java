package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgReqConfDrugCap implements IMessage {
    public MsgReqConfDrugCap(){};

    public enum Type {REQUEST_INITIAL, REQUEST, CONFIRM}
    public Type type;

    public MsgReqConfDrugCap(Type type) {
        this.type = type;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        switch (type) {
            case REQUEST_INITIAL: buf.writeByte(0);
            case REQUEST: buf.writeByte(1);
            case CONFIRM: buf.writeByte(2);
        }
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        switch (buf.readByte()) {
            case 0: type = Type.REQUEST_INITIAL;
            case 1: type = Type.REQUEST;
            case 2: type = Type.CONFIRM;
        }
    }
}