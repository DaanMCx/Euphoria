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
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.network.MsgReqDrugCap;
import nl.daanmc.euphoria.util.network.MsgSyncDrugCap;
import nl.daanmc.euphoria.util.network.NetworkHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<IScheduledTask> clientTasks = new CopyOnWriteArrayList<>();
    public static List<IScheduledTask> serverTasks = new CopyOnWriteArrayList<>();

    //Client
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase == TickEvent.Phase.END && player != null && !Minecraft.getMinecraft().isGamePaused()) {
            IDrugCap drugCap = player.getCapability(DrugCap.Provider.CAP, null);
            //Request client info if this is initial player tick
            if (drugCap.getClientTicks() == 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgReqDrugCap());
            }
            //Execute tasks
            if (!clientTasks.isEmpty()) {
                for (IScheduledTask task : clientTasks) {
                    if (task.getTick() <= drugCap.getClientTicks()) {
                        task.execute();
                        clientTasks.remove(task);
                    }
                }
            }
            //Filter activePresences for relevancy
            drugCap.getActivePresences().forEach((drugPresence, tick) -> {
                if (tick + 2 * drugPresence.delay + 1 < drugCap.getClientTicks()) {
                    drugCap.getActivePresences().remove(drugPresence);
                }
            });
            //Calculate the breakdown S-curve
            drugCap.getBreakdownTicks().forEach((drugSubstance, tick) -> {
                if (tick > 0L && tick <= drugCap.getClientTicks()) {
                    float oldAmount = drugCap.getDrugs().get(drugSubstance);
                    float A = drugCap.getBreakdownAmounts().get(drugSubstance);
                    int L = Math.round(drugSubstance.getBreakdownTime() * (drugCap.getBreakdownAmounts().get(drugSubstance)/100));
                    long X = drugCap.getClientTicks() - tick;
                    drugCap.getDrugs().put(drugSubstance, (oldAmount > 1 ? (float) ((-A / (1 + Math.exp((((Math.log((-A / (1 - A)) -1) -7) * X) / L) +7))) +A) : 0F));
                    if (drugCap.getDrugs().get(drugSubstance) == 0F) {
                        drugCap.getBreakdownTicks().put(drugSubstance, 0L);
                    }
//                    if (X == 0) {
//                        NetworkHandler.INSTANCE.sendToServer(new MsgSyncDrugCap(drugCap));
//                    }
                    if (drugCap.getClientTicks() % 40 == 0) {
                        System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+drugCap.getDrugs().get(drugSubstance));
                    }
                }
            });
            //Active sync DrugCap to server each 5 seconds
            if (drugCap.getClientTicks()%100 == 0 && drugCap.getClientTicks() > 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgSyncDrugCap(drugCap));
                System.out.println("Client tick "+drugCap.getClientTicks());
            }
            drugCap.setClientTicks(drugCap.getClientTicks() + 1);
            //TODO: Update DrugInfluences
        }
    }

    //Server
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!serverTasks.isEmpty()) {
                for (IScheduledTask task : serverTasks) {
                    if (task.getTick() <= event.world.getTotalWorldTime()) {
                        task.execute();
                        serverTasks.remove(task);
                    }
                }
            }
            //more
        }
    }

    //Server
    @SubscribeEvent
    public static void onPlayerSaveToFile(SaveToFile event) {
        NetworkHandler.INSTANCE.sendTo(new MsgReqDrugCap(), (EntityPlayerMP) event.getEntityPlayer());
    }

    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IDrugCap drugCap = event.player.getCapability(DrugCap.Provider.CAP,null);
        Elements.SUBSTANCES.forEach(drugSubstance -> {
            drugCap.getDrugs().putIfAbsent(drugSubstance, 0F);
            drugCap.getBreakdownTicks().putIfAbsent(drugSubstance, 0L);
            drugCap.getBreakdownAmounts().putIfAbsent(drugSubstance, 0F);
        });
    }

    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSyncDrugCap(event.player.getCapability(DrugCap.Provider.CAP,null)), (EntityPlayerMP) event.player);
    }

    //Client
    @SubscribeEvent
    public static void onClientSaveAndQuit(GuiScreenEvent.ActionPerformedEvent event) throws InterruptedException {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == 1) {
            //Send client info to server and wait until confirmed
            NetworkHandler.INSTANCE.sendToServer(new MsgSyncDrugCap(Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP, null)));
        }
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getObject();
        event.addCapability(new ResourceLocation(Euphoria.MODID, "drug_cap"), new DrugCap.Provider(player));
    }
}