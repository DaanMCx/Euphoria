package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgReqDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgSendClientInfo;
import nl.daanmc.euphoria.util.network.MsgSendDrugPresenceCap;
import nl.daanmc.euphoria.util.network.NetworkHandler;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<IScheduledTask> pendingTasks = new CopyOnWriteArrayList<>();
    public static long clientPlayerTicks = 0L;
    public static boolean confDisconnectInfo = false;

    //Client
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            if (event.phase == TickEvent.Phase.END) {
                if (!pendingTasks.isEmpty()) {
                    for (IScheduledTask task : pendingTasks) {
                        if (task.getTick() <= clientPlayerTicks) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }
                //Calculating the breakdown S-curve
                if (!Minecraft.getMinecraft().isGamePaused()) {
                    IDrugPresenceCap dpCap = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null);
                    dpCap.getBreakdownTickList().forEach((drugSubstance, tick) -> {
                        if (tick > 0L && tick <= clientPlayerTicks) {
                            float oldAmount = dpCap.getDrugPresenceList().get(drugSubstance);
                            float A = dpCap.getBreakdownAmountList().get(drugSubstance);
                            int L = Math.round(drugSubstance.getBreakdownTime() * (dpCap.getBreakdownAmountList().get(drugSubstance)/100));
                            long X = clientPlayerTicks - tick;
                            dpCap.getDrugPresenceList().put(drugSubstance, (oldAmount>1 ? (float)((-A/(1+Math.exp((((Math.log((-A/(1-A))-1)-7)*X)/L)+7)))+A) : 0F));
                            if (dpCap.getDrugPresenceList().get(drugSubstance) == 0F) {
                                dpCap.getBreakdownTickList().put(drugSubstance, 0L);
                            }
                            if (X == 0) {
                                NetworkHandler.INSTANCE.sendToServer(new MsgSendDrugPresenceCap(dpCap));
                            }
                            if (clientPlayerTicks % 40 == 0) {
                                System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+dpCap.getDrugPresenceList().get(drugSubstance));
                            }
                        }
                    });
                    clientPlayerTicks++;
                    if (clientPlayerTicks%20==0) {
                        System.out.println("Client tick "+clientPlayerTicks);
                    }
                }

                //TODO: Update DrugInfluences
            }
        }
    }

    //Server
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
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
    public static void onPlayerSaveToFile(SaveToFile event) {
        NetworkHandler.INSTANCE.sendTo(new MsgReqDrugPresenceCap(), (EntityPlayerMP) event.getEntityPlayer());
        //TODO: Save player's pendingTasks to NBT

    }

    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IDrugPresenceCap dpCap = event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null);
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            dpCap.getDrugPresenceList().putIfAbsent(drugSubstance, 0F);
            dpCap.getBreakdownTickList().putIfAbsent(drugSubstance, 0L);
            dpCap.getBreakdownAmountList().putIfAbsent(drugSubstance, 0F);
        });
        HashMap<String, byte[]> scheduledTasks = new HashMap<>();
        for (int i = 0; i < event.player.getEntityData().getInteger("scheduled_tasks:c"); i++) {
            String key = "scheduled_task:c:"+i;
            scheduledTasks.put(key, event.player.getEntityData().getByteArray(key));
        }
        long clientTicks = event.player.getEntityData().getLong("client_ticks");
        NetworkHandler.INSTANCE.sendTo(new MsgSendClientInfo(dpCap, scheduledTasks, clientTicks), (EntityPlayerMP) event.player);
        //TODO: Read player's pendingTasks from NBT and send to player
    }

    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSendDrugPresenceCap(event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null)), (EntityPlayerMP) event.player);
    }

    //Client
    @SubscribeEvent
    public static void onClientSaveAndQuit(GuiScreenEvent.ActionPerformedEvent event) throws InterruptedException {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == 1) {
            HashMap<String, byte[]> scheduledTasks = new HashMap<>();
            AtomicInteger count = new AtomicInteger();
            pendingTasks.forEach(task -> {
                if (task.isPersistent()) {
                    scheduledTasks.put("scheduled_task:c:"+count.getAndIncrement(), task.serialize());
                }
            });
            NetworkHandler.INSTANCE.sendToServer(new MsgSendClientInfo(Minecraft.getMinecraft().player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null), scheduledTasks, clientPlayerTicks));
            AtomicInteger timeoutCount = new AtomicInteger(0);
            while (!confDisconnectInfo && timeoutCount.getAndIncrement() < 500) {
                Thread.sleep(1L);
            }
            confDisconnectInfo = false;
        }
    }
}