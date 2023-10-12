package nl.daanmc.euphoria.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import nl.daanmc.euphoria.Elements.DrugSubstances;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceMsg;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber
public class EventHandler {
    public static List<ScheduledTask> pendingTasks = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            if (!pendingTasks.isEmpty()) {
                for (ScheduledTask task : pendingTasks) {
                    if (task.getTick() <= event.world.getTotalWorldTime()) {
                        task.execute();
                        pendingTasks.remove(task);
                    }
                }
            }
            
            for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
                player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().forEach((drugSubstance, presence) -> {
                    if (presence > 0) {
                        player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, presence * ((1 - drugSubstance.getBreakdownSpeed()) + player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().getOrDefault(DrugSubstances.ALCOHOL, 0F) / 100 * drugSubstance.getBreakdownSpeed()));
                        if (presence < 0.1 && event.world.getTotalWorldTime() % 100 == 0) {
                            player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, 0F);
                        }
                        PacketHandler.INSTANCE.sendTo(new DrugPresenceMsg(drugSubstance, player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().get(drugSubstance)), player);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onServerDisconnectionFromClient(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        //TODO: Implement writing player's pendingTasks to disk
    }
    
    @SubscribeEvent
    public static void onServerConnectionFromClient(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        //TODO: Implement reading player's pendingTasks from disk
    }

}
