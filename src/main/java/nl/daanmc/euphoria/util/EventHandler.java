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
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap.Type;
import nl.daanmc.euphoria.util.messages.MsgSyncPDCap;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber
public class EventHandler {
    public static boolean confCap = true;

    //Client
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase == TickEvent.Phase.END && player != null && !Minecraft.getMinecraft().isGamePaused()) {
            IPlayerDrugsCap drugCap = player.getCapability(PlayerDrugsCap.Provider.CAP, null);
            //Request DrugCap if this is initial player tick
            if (drugCap.getClientTick() == 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgReqConfPDCap(Type.REQUEST_INITIAL));
            }
            //Execute tasks
            drugCap.executeClientTasks();
            //Filter activePresences for relevancy
            ArrayList<DrugPresence> oldPresences = new ArrayList<>();
            drugCap.getActivePresences().forEach((drugPresence, tick) -> {
                if (tick + 2 * drugPresence.comeUp + 1 < drugCap.getClientTick()) {
                    oldPresences.add(drugPresence);
                }
            });
            oldPresences.forEach(drugPresence -> drugCap.getActivePresences().remove(drugPresence));
            //Calculate the breakdown S-curve
            drugCap.getBreakdownTicks().forEach((drugSubstance, tick) -> {
                if (tick > 0L && tick <= drugCap.getClientTick()) {
                    float oldAmount = drugCap.getPlayerDrugs().get(drugSubstance);
                    float A = drugCap.getBreakdownAmounts().get(drugSubstance);
                    int L = Math.round(drugSubstance.getBreakdownTime() * (drugCap.getBreakdownAmounts().get(drugSubstance)/100));
                    long X = drugCap.getClientTick() - tick;
                    drugCap.getPlayerDrugs().put(drugSubstance, (oldAmount > 1 ? (float) ((-A / (1 + Math.exp((((Math.log((-A / (1 - A)) -1) -7) * X) / L) +7))) +A) : 0F));
                    if (drugCap.getPlayerDrugs().get(drugSubstance) == 0F) {
                        drugCap.getBreakdownTicks().put(drugSubstance, 0L);
                    }
                    //TODO remove
                    if (drugCap.getClientTick() % 40 == 0) {
                        System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+drugCap.getPlayerDrugs().get(drugSubstance));
                    }
                }
            });
            //Active sync DrugCap to server each 5 seconds
            if (drugCap.getClientTick()%200 == 0 && drugCap.getClientTick() > 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgSyncPDCap(drugCap));
            }
            //TODO remove
            if (drugCap.getClientTick()%100 == 0) {
                System.out.println("Client tick " + drugCap.getClientTick());
            }
            drugCap.doClientTick();
            //TODO: Update DrugInfluences
        }
    }

    //Server
    @SubscribeEvent
    public static void onPlayerSaveToFile(SaveToFile event) {
        NetworkHandler.INSTANCE.sendTo(new MsgReqConfPDCap(Type.REQUEST), (EntityPlayerMP) event.getEntityPlayer());
    }

    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IPlayerDrugsCap drugCap = event.player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        Euphoria.Content.SUBSTANCES.forEach(drugSubstance -> {
            drugCap.getPlayerDrugs().putIfAbsent(drugSubstance, 0F);
            drugCap.getBreakdownTicks().putIfAbsent(drugSubstance, 0L);
            drugCap.getBreakdownAmounts().putIfAbsent(drugSubstance, 0F);
        });
    }

    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSyncPDCap(event.player.getCapability(PlayerDrugsCap.Provider.CAP,null)), (EntityPlayerMP) event.player);
    }

    //Client
    @SubscribeEvent
    public static void onClientSaveAndQuit(GuiScreenEvent.ActionPerformedEvent event) throws InterruptedException {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == 1) {
            NetworkHandler.INSTANCE.sendToServer(new MsgSyncPDCap(Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP, null)));
            //Send DrugCap to server and wait until confirmed
            confCap = false;
            AtomicInteger timeoutCount = new AtomicInteger(0);
            while (!confCap && timeoutCount.getAndIncrement() < 500) {
                Thread.sleep(1L);
                System.out.println("SLEEPING 1MS");
            }
        }
    }

    //Common
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        //Attach Drug capability to players
        if(event.getObject() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getObject();
            event.addCapability(new ResourceLocation(Euphoria.MODID, "drug_cap"), new PlayerDrugsCap.Provider(player));
        }
    }
}