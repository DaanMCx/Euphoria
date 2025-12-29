package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap.Type;
import nl.daanmc.euphoria.util.messages.MsgSyncPDCap;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (event.phase == TickEvent.Phase.END && player != null && !Minecraft.getMinecraft().isGamePaused()) {
            IPlayerDrugsCap PDCap = player.getCapability(PlayerDrugsCap.Provider.CAP, null);
            //Request DrugCap if this is initial player tick
            if (PDCap.getClientTick() == 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgReqConfPDCap(Type.REQUEST_INITIAL));
            }
            //Execute tasks
            PDCap.executeClientTasks();
            //Filter activePresences for relevancy
            ArrayList<DrugPresence> oldPresences = new ArrayList<>();
            PDCap.getActivePresences().forEach((drugPresence, tick) -> {
                if (tick + 2 * drugPresence.comeUp + 1 < PDCap.getClientTick()) {
                    oldPresences.add(drugPresence);
                }
            });
            oldPresences.forEach(drugPresence -> PDCap.getActivePresences().remove(drugPresence));
            //Calculate the breakdown S-curve
            PDCap.getBreakdownTicks().forEach((drug, tick) -> {
                if (tick > 0L && tick <= PDCap.getClientTick()) {
                    float oldAmount = PDCap.getPlayerDrugs().get(drug);
                    float A = PDCap.getBreakdownAmounts().get(drug);
                    int L = Math.round(drug.getBreakdownTime() * (PDCap.getBreakdownAmounts().get(drug)/100));
                    long X = PDCap.getClientTick() - tick;
                    PDCap.getPlayerDrugs().put(drug, (oldAmount > 1 ? (float) ((-A / (1 + Math.exp((((Math.log((-A / (1 - A)) -1) -7) * X) / L) +7))) +A) : 0F));
                    if (PDCap.getPlayerDrugs().get(drug) == 0F) {
                        PDCap.getBreakdownTicks().put(drug, 0L);
                    }
                    //TODO remove
                    if (PDCap.getClientTick() % 40 == 0) {
                        System.out.println("S-curve: "+drug.getRegistryName()+" "+PDCap.getPlayerDrugs().get(drug));
                    }
                }
            });
            //Active sync DrugCap to server each 5 seconds
            if (PDCap.getClientTick()%200 == 0 && PDCap.getClientTick() > 0L) {
                NetworkHandler.INSTANCE.sendToServer(new MsgSyncPDCap(PDCap));
            }
            //TODO remove
            if (PDCap.getClientTick()%100 == 0) {
                System.out.println("Client tick " + PDCap.getClientTick());
            }
            PDCap.doClientTick();
            //TODO: Update DrugInfluences
        }
    }

    @SubscribeEvent
    public static void onClientSaveAndQuit(GuiScreenEvent.ActionPerformedEvent event) throws InterruptedException {
        if (event.getGui() instanceof GuiIngameMenu && event.getButton().id == 1) {
            NetworkHandler.INSTANCE.sendToServer(new MsgSyncPDCap(Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP, null)));
            //Send DrugCap to server and wait until confirmed
            EventHandler.confCap = false;
            AtomicInteger timeoutCount = new AtomicInteger(0);
            while (!EventHandler.confCap && timeoutCount.getAndIncrement() < 500) {
                Thread.sleep(1L);
                System.out.println("SLEEPING 1MS");
            }
        }
    }
}
