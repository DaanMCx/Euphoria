package nl.daanmc.euphoria.util.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MsgReqDrugPresenceCap implements IMessage {
    public MsgReqDrugPresenceCap() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}
}