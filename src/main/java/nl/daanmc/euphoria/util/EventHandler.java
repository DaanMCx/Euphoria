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
import nl.daanmc.euphoria.util.IScheduledTask.Side;
import nl.daanmc.euphoria.util.network.MsgReqDrugPresenceCap;
import nl.daanmc.euphoria.util.network.MsgSendDrugPresenceCap;
import nl.daanmc.euphoria.util.network.NetworkHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
                        if (task.getTick() <= player.world.getTotalWorldTime() && (task.getSide()==Side.CLIENT || task.getSide()==Side.COMMON)) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }

                if (!Minecraft.getMinecraft().isGamePaused()) {
                    player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().forEach((drugSubstance, tick) -> {
                        if (tick > 0L && tick <= player.world.getTotalWorldTime()) {
                            float oldAmount = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance);
                            float A = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownAmountList().get(drugSubstance);
                            int L = Math.round(drugSubstance.getBreakdownTime() * (player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownAmountList().get(drugSubstance)/100));
                            long X = player.world.getTotalWorldTime() - tick;
                            //Calculate the breakdown S-curve with values above
                            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, (oldAmount>1 ? (float)((-A/(1+Math.exp((((Math.log((-A/(1-A))-1)-7)*X)/L)+7)))+A) : 0F));
                            if (player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance) == 0F) {
                                player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().put(drugSubstance, 0L);
                            }
                            System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance));
                        }
                    });
                }

                //TODO: Update DrugInfluences
            }
        }
    }
    //Server
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            serverTicks++;
            if (!pendingTasks.isEmpty()) {
                for (IScheduledTask task : pendingTasks) {
                    if (task.getTick() <= serverTicks && (task.getSide()==Side.SERVER || task.getSide()==Side.COMMON)) {
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

    }
    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IDrugPresenceCap dpCap = event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null);
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            dpCap.getPresenceList().putIfAbsent(drugSubstance, 0F);
            dpCap.getBreakdownTickList().putIfAbsent(drugSubstance, 0L);
            dpCap.getBreakdownAmountList().putIfAbsent(drugSubstance, 0F);
        });
        NetworkHandler.INSTANCE.sendTo(new MsgSendDrugPresenceCap(dpCap), (EntityPlayerMP) event.player);
        //TODO: Read player's pendingTasks from NBT and send to player
    }
    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSendDrugPresenceCap(event.player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null)), (EntityPlayerMP) event.player);
    }
}