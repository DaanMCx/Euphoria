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

    private void setType(Type typeIn) {
        type = typeIn;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        switch (type) {
            case REQUEST_INITIAL:
                buf.writeByte(0);
                break;
            case REQUEST:
                buf.writeByte(1);
                break;
            case CONFIRM:
                buf.writeByte(2);
                break;
        }
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        switch (buf.readByte()) {
            case 0:
                setType(Type.REQUEST_INITIAL);
                break;
            case 1:
                setType(Type.REQUEST);
                break;
            case 2:
                setType(Type.CONFIRM);
                break;
        }
    }
}