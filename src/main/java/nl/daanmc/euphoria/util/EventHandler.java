package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgReqDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgSendDrugPresenceCap;
import nl.daanmc.euphoria.util.network.NetworkHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<IScheduledTask> pendingTasks = new CopyOnWriteArrayList<>();
    public static long clientTicks = 0L;
    public static long serverTicks = 0L;
    //Client
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().world != null) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (event.phase == TickEvent.Phase.END) {
                clientTicks++;
                if (!pendingTasks.isEmpty()) {
                    for (IScheduledTask task : pendingTasks) {
                        if (task.getTick() <= player.world.getTotalWorldTime()) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }
                //Calculating the breakdown S-curve
                if (!Minecraft.getMinecraft().isGamePaused()) {
                    IDrugPresenceCap dpCap = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null);
                    dpCap.getBreakdownTickList().forEach((drugSubstance, tick) -> {
                        if (tick != null && tick <= player.world.getTotalWorldTime()) {
                            float oldAmount = dpCap.getDrugPresenceList().get(drugSubstance);
                            float A = dpCap.getBreakdownAmountList().get(drugSubstance);
                            int L = Math.round(drugSubstance.getBreakdownTime() * (dpCap.getBreakdownAmountList().get(drugSubstance)/100));
                            long X = player.world.getTotalWorldTime() - tick;
                            dpCap.getDrugPresenceList().put(drugSubstance, (oldAmount>1 ? (float)((-A/(1+Math.exp((((Math.log((-A/(1-A))-1)-7)*X)/L)+7)))+A) : 0F));
                            if (dpCap.getDrugPresenceList().get(drugSubstance) == 0F) {
                                dpCap.getBreakdownTickList().put(drugSubstance, null);
                            }
                            System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+dpCap.getDrugPresenceList().get(drugSubstance));
                        }
                    });
                }

                //TODO: Update DrugInfluences
            }
        }
    }
    //Server
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            serverTicks++;
            if (!pendingTasks.isEmpty()) {
                for (IScheduledTask task : pendingTasks) {
                    if (task.getTick() <= event.world.getTotalWorldTime()) {
                        task.execute();
                        pendingTasks.remove(task);
                    }
                }
            }
            //more
        }
    }
    //Server
    @SubscribeEvent
    public static void onPlayerSaveToFile(net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile event) {
        NetworkHandler.INSTANCE.sendTo(new MsgReqDrugPresenceCap(), (EntityPlayerMP) event.getEntityPlayer());
        //TODO: Save player's pendingTasks to NBT
        AtomicInteger count = new AtomicInteger(0);
        pendingTasks.forEach(task -> {
            if (task.isPersistent()) {
                event.getEntityPlayer().getEntityData().setByteArray("scheduled_task:s:"+count, task.serialize());
                count.incrementAndGet();
            }
        });
        event.getEntityPlayer().getEntityData().setInteger("scheduled_tasks:s", count.get());
        //that was server tasks, now to request the client tasks and let them be saved in the messagehandler

    }
    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IDrugPresenceCap dpCap = event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null);
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            dpCap.getDrugPresenceList().putIfAbsent(drugSubstance, 0F);
            dpCap.getBreakdownTickList().putIfAbsent(drugSubstance, null);
            dpCap.getBreakdownAmountList().putIfAbsent(drugSubstance, 0F);
        });
        NetworkHandler.INSTANCE.sendTo(new MsgSendDrugPresenceCap(dpCap), (EntityPlayerMP) event.player);
        //TODO: Read player's pendingTasks from NBT and send to player
        for (int i=0; i<=event.player.getEntityData().getInteger("scheduled_tasks:s"); i++) {
            pendingTasks.add(IScheduledTask.deserialize(event.player.getEntityData().getByteArray("scheduled_task:s:"+i)));
        }
        //that was server tasks, now to read and send client tasks to client
    }
    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSendDrugPresenceCap(event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null)), (EntityPlayerMP) event.player);
    }
}