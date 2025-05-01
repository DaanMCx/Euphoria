package nl.daanmc.euphoria.util.messages;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.EventHandler;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;

public class MsgReqConfPDCap implements IMessage {
    public MsgReqConfPDCap(){};

    public enum Type {REQUEST_INITIAL, REQUEST, CONFIRM}
    public Type type;

    public MsgReqConfPDCap(Type type) {
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

    public static class Handler implements IMessageHandler<MsgReqConfPDCap, MsgSyncPDCap> {
        @Override
        public MsgSyncPDCap onMessage(MsgReqConfPDCap message, MessageContext ctx) {
            IPlayerDrugsCap PDCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(PlayerDrugsCap.Provider.CAP, null);
            if (ctx.side.isClient() && !(PDCap.getClientTick() > 0L)) {
                return null;
            } else {
                if (message.type == Type.CONFIRM) {
                    EventHandler.confCap = true;
                    return null;
                } else {
                    return new MsgSyncPDCap(PDCap, message.type==Type.REQUEST_INITIAL);
                }
            }
        }
    }
}