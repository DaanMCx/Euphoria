package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgConfClientInfo implements IMessage {
    public MsgConfClientInfo(){};
    private boolean conf;

    public MsgConfClientInfo(boolean confirm) {
        this.conf = confirm;
    }

    public boolean get() {
        return conf;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(conf ? 1 : 0);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        conf = buf.readByte() == 1;
    }
}