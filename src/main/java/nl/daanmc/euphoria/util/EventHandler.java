package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCap;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgConfClientInfo;
import nl.daanmc.euphoria.util.network.MsgDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgSendClientInfo;
import nl.daanmc.euphoria.util.network.NetworkHandler;

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
        if (event.phase == TickEvent.Phase.END && player != null && !Minecraft.getMinecraft().isGamePaused()) {
            //Request client info if this is initial player tick
            if (clientPlayerTicks == 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgConfClientInfo(false));
            }
            //Execute tasks
            if (!pendingTasks.isEmpty()) {
                for (IScheduledTask task : pendingTasks) {
                    if (task.getTick() <= clientPlayerTicks) {
                        task.execute();
                        pendingTasks.remove(task);
                    }
                }
            }
            //Filter activePresences for relevancy
            DrugPresence.activePresences.forEach((drugPresence, tick) -> {
                if (tick + 2 * drugPresence.delay + 1 < clientPlayerTicks) {
                    DrugPresence.activePresences.remove(drugPresence);
                }
            });
            //Calculate the breakdown S-curve
            IDrugPresenceCap dpCap = player.getCapability(DrugPresenceCap.Provider.CAP,null);
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
                        NetworkHandler.INSTANCE.sendToServer(new MsgDrugPresenceCap(dpCap));
                    }
                    if (clientPlayerTicks % 40 == 0) {
                        System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+dpCap.getDrugPresenceList().get(drugSubstance));
                    }
                }
            });
            //Active sync DrugPresenceCap to server each 5 seconds
            if (clientPlayerTicks%100==0 && clientPlayerTicks > 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgDrugPresenceCap(dpCap));
                System.out.println("Client tick "+clientPlayerTicks);
            }
            clientPlayerTicks++;
            //TODO: Update DrugInfluences
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
        NetworkHandler.INSTANCE.sendTo(new MsgConfClientInfo(false), (EntityPlayerMP) event.getEntityPlayer());
    }

    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IDrugPresenceCap dpCap = event.player.getCapability(DrugPresenceCap.Provider.CAP,null);
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            dpCap.getDrugPresenceList().putIfAbsent(drugSubstance, 0F);
            dpCap.getBreakdownTickList().putIfAbsent(drugSubstance, 0L);
            dpCap.getBreakdownAmountList().putIfAbsent(drugSubstance, 0F);
        });
    }

    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgDrugPresenceCap(event.player.getCapability(DrugPresenceCap.Provider.CAP,null)), (EntityPlayerMP) event.player);
    }

    //Client
    @SubscribeEvent
    public static void onClientSaveAndQuit(GuiScreenEvent.ActionPerformedEvent event) throws InterruptedException {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == 1) {
            //Send client info to server and wait until confirmed
            confDisconnectInfo = false;
            NetworkHandler.INSTANCE.sendToServer(new MsgSendClientInfo(Minecraft.getMinecraft().player.getCapability(DrugPresenceCap.Provider.CAP,null), DrugPresence.activePresences, clientPlayerTicks));
            AtomicInteger timeoutCount = new AtomicInteger(0);
            while (!confDisconnectInfo && timeoutCount.getAndIncrement() < 500) {
                Thread.sleep(1L);
                System.out.println("SLEEPING 1MS");
            }
            confDisconnectInfo = false;
            clientPlayerTicks = 0L;
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getObject();
        event.addCapability(new ResourceLocation(Euphoria.MODID, "drug_presence"), new DrugPresenceCap.Provider(player));
    }
}