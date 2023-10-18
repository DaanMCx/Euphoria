package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.util.ScheduledTask.Side;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<ScheduledTask> pendingTasks = new CopyOnWriteArrayList<>();
    //Client
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().world != null) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (event.phase == TickEvent.Phase.END) {
                if (!pendingTasks.isEmpty()) {
                    for (ScheduledTask task : pendingTasks) {
                        if (task.getTick() <= player.world.getTotalWorldTime() && (task.getSide()==Side.CLIENT || task.getSide()==Side.COMMON)) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }

                player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().forEach((drugSubstance, tick) -> {
                    if (tick > 0L && tick <= player.world.getTotalWorldTime()) {
                        float oldAmount = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance);
                        float A = player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownAmountList().get(drugSubstance);
                        int L = drugSubstance.getBreakdownTime() * (int)(100/player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownAmountList().get(drugSubstance));
                        long X = player.world.getTotalWorldTime() - tick;
                        //Calculate the breakdown S-curve with values above
                        player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, (oldAmount>1 ? (float)((-A/(1+Math.pow(Math.E,(((Math.exp((-A/(1-A))-1)-7)*X)/L)+7)))+A) : 0F));
                        if (player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance) == 0F) {
                            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getBreakdownTickList().put(drugSubstance, 0L);
                        }
                        System.out.println("S-curve: "+drugSubstance.getRegistryName()+" "+player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance));
                    }
                });

                //TODO: Update DrugInfluences
            }
        }
    }
    //Server
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!pendingTasks.isEmpty()) {
                for (ScheduledTask task : pendingTasks) {
                    if (task.getTick() <= event.world.getTotalWorldTime() && (task.getSide()==Side.SERVER || task.getSide()==Side.COMMON)) {
                        task.execute();
                        pendingTasks.remove(task);
                    }
                }
            }

        }
    }
    //Server
    @SubscribeEvent
    public static void onPlayerSaveToFile(net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile event) {
        //TODO: Request player's DrugPresenceCap for saving
        //TODO: Save player's pendingTasks to NBT
    }
    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        //TODO: Send Player's DrugPresenceCap to player
        //TODO: Read player's pendingTasks from NBT and send to player
    }
    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        //TODO: Send player's DrugPresenceCap to player
    }
}