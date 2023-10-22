package nl.daanmc.euphoria.util.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;
import nl.daanmc.euphoria.util.EventHandler;
import nl.daanmc.euphoria.util.IScheduledTask;

public class ClientInfoMsgHandler {

    public static class SendClientInfoMsgHandler implements IMessageHandler<MsgSendClientInfo, MsgConfClientInfo> {
        @Override
        public MsgConfClientInfo onMessage(MsgSendClientInfo message, MessageContext ctx) {
            EntityPlayer player = Euphoria.proxy.getPlayerFromContext(ctx);
            if (player != null) {
                IDrugPresenceCap oldCap = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null);
                IDrugPresenceCap newCap = message.capability;
                oldCap.getDrugPresenceList().clear();
                oldCap.getDrugPresenceList().putAll(newCap.getDrugPresenceList());
                oldCap.getBreakdownAmountList().clear();
                oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
                oldCap.getBreakdownTickList().clear();
                oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());
                if (ctx.side.isServer()) {
                    player.getEntityData().setLong("client_ticks", message.clientTicks);
                    if (message.scheduledTasks != null) {
                        message.scheduledTasks.forEach((key, value) -> {
                            player.getEntityData().setByteArray(key, value);
                        });
                        player.getEntityData().setInteger("scheduled_tasks:c", message.scheduledTasks.size());
                    }
                } else {
                    EventHandler.clientPlayerTicks = message.clientTicks;
                    if (message.scheduledTasks != null) {
                        message.scheduledTasks.forEach((key, value) -> {
                            EventHandler.pendingTasks.add(IScheduledTask.deserialize(value));
                        });
                    }
                    System.out.println(message.capability.getBreakdownTickList().get(Elements.DrugSubstances.THC));
                }
                System.out.println("MsgSendClientInfo received; updated THC: "+ oldCap.getDrugPresenceList().get(Elements.DrugSubstances.THC));
            } else {
                System.out.println("MsgSendClientInfo received, alas no player anymore");
            }
            return ctx.side.isServer() ? new MsgConfClientInfo() : null;
        }
    }

    public static class ConfClientInfoMsgHandler implements IMessageHandler<MsgConfClientInfo, MsgConfClientInfo> {
        @Override
        public MsgConfClientInfo onMessage(MsgConfClientInfo message, MessageContext ctx) {
            EventHandler.confDisconnectInfo = true;
            return null;
        }
    }
}