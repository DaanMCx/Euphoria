package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.daanmc.euphoria.Elements.DrugSubstances;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<ScheduledTask> pendingTasks = new CopyOnWriteArrayList<>();
    //Common
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (event.side.isServer()) {
                if (!pendingTasks.isEmpty()) {
                    for (ScheduledTask task : pendingTasks) {
                        if (task.getTick() <= event.world.getTotalWorldTime()) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }
            } else if (event.side.isClient()) {
                if (!pendingTasks.isEmpty()) {
                    for (ScheduledTask task : pendingTasks) {
                        if (task.getTick() <= event.world.getTotalWorldTime()) {
                            task.execute();
                            pendingTasks.remove(task);
                        }
                    }
                }

                EntityPlayer player = Minecraft.getMinecraft().player;
                player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().forEach((drugSubstance, presence) -> {
                    if (presence > 0F) {
                        player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, presence * ((1 - drugSubstance.getBreakdownSpeed()) + player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().getOrDefault(DrugSubstances.ALCOHOL, 0F) / 100 * drugSubstance.getBreakdownSpeed()));
                        if (presence < 1F && event.world.getTotalWorldTime() % 100 == 0) {
                            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, 0F);
                        }
                    }
                });

                if (event.world.getTotalWorldTime() % 100 == 0) {
                    Minecraft.getMinecraft().player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP,null).getPresenceList().forEach((sub,am)->{System.out.println("DrugPresence "+sub.getRegistryName()+" "+am);});
                }

                //TODO: Update DrugInfluences
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