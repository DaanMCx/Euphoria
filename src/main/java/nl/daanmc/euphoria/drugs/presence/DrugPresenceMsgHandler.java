package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DrugPresenceMsgHandler implements IMessageHandler<DrugPresenceMsg, IMessage> {
    public DrugPresenceMsgHandler() {}

    @Override
    public IMessage onMessage(DrugPresenceMsg message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            EntityPlayer player = (EntityPlayer) ctx.getServerHandler().player;
            long tick = player.world.getTotalWorldTime();
            for (int i = 0; i < message.delay; i++) {
                DrugPresenceTask task = new DrugPresenceTask(message.substance, message.amount / message.delay, tick + message.delay + i, player);
                task.add(1);
            }
        }
        
        if (ctx.side.isClient()) {
            System.out.println("[Client] DrugPresence Update packet received: " + message.substance.getRegistryName() + " amount " + message.amount);
            //TODO: implement DrugInfluence updates
        }
        return null;
    }
}
