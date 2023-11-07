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

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientInfoMH {

    public static class SendClientInfoMH implements IMessageHandler<MsgSendClientInfo, MsgConfClientInfo> {
        @Override
        public MsgConfClientInfo onMessage(MsgSendClientInfo message, MessageContext ctx) {
            EntityPlayer player = Euphoria.proxy.getPlayerFromContext(ctx);
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
                    System.out.println("Saved "+message.scheduledTasks.size()+" tasks to player NBT");
                }
            } else {
                EventHandler.clientPlayerTicks = message.clientTicks;
                if (message.scheduledTasks != null) {
                    message.scheduledTasks.forEach((key, value) -> {
                        EventHandler.pendingTasks.add(IScheduledTask.deserialize(value));
                    });
                    System.out.println("Activated "+message.scheduledTasks.size()+" tasks from player NBT");
                }
            }
            System.out.println("MsgSendClientInfo received; updated THC: "+ oldCap.getDrugPresenceList().get(Elements.DrugSubstances.THC));
            return new MsgConfClientInfo(true);
        }
    }

    public static class ConfClientInfoMH implements IMessageHandler<MsgConfClientInfo, MsgSendClientInfo> {
        @Override
        public MsgSendClientInfo onMessage(MsgConfClientInfo message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                EventHandler.confDisconnectInfo = message.get();
                return null;
            } else {
                EntityPlayer player = Euphoria.proxy.getPlayerFromContext(ctx);
                if (message.get()) {
                    int amount = player.getEntityData().getInteger("scheduled_tasks:c");
                    if (amount > 0) {
                        for (int i = 0; i < amount; i++) {
                            player.getEntityData().removeTag("scheduled_task:c:"+i);
                        }
                        System.out.println("Removed "+amount+" task tags from player NBT");
                        player.getEntityData().setInteger("scheduled_tasks:c", 0);
                    }
                    return null;
                } else {
                    HashMap<String, byte[]> scheduledTasks = new HashMap<>();
                    AtomicInteger count = new AtomicInteger();
                    EventHandler.pendingTasks.forEach(task -> {
                        if (task.isPersistent()) {
                            scheduledTasks.put("scheduled_task:c:"+count.getAndIncrement(), task.serialize());
                        }
                    });
                    return new MsgSendClientInfo(player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null), scheduledTasks, player.getEntityData().getLong("client_ticks"));
                }
            }
        }
    }
}