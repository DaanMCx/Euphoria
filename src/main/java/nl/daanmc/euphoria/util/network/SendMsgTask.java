package nl.daanmc.euphoria.util.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import nl.daanmc.euphoria.util.IScheduledTask;
//Currently unused but might become useful again in the future
public class SendMsgTask implements IScheduledTask {
    private final EntityPlayerMP player;
    private final IMessage message;
    private final long tick;

    public SendMsgTask(EntityPlayerMP toPlayer, IMessage msg, long onTick) {
        this.player = toPlayer;
        this.message = msg;
        this.tick = onTick;
        //When toPlayer is null, msg is sent to server
    }

    @Override
    public long getTick() {
        return tick;
    }

    @Override
    public boolean isPersistent() {
        return false;
    }

    @Override
    public void execute() {
        if (player == null) {
            NetworkHandler.INSTANCE.sendToServer(message);
        } else {
            NetworkHandler.INSTANCE.sendTo(message, player);
        }
    }

    @Override
    public byte[] serialize() {
        return null;
    }
}