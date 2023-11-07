package nl.daanmc.euphoria.util.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCap;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;
import nl.daanmc.euphoria.util.EventHandler;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(DrugPresenceCapMH.class, MsgDrugPresenceCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(DrugPresenceCapMH.class, MsgDrugPresenceCap.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SendClientInfoMH.class, MsgSendClientInfo.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(SendClientInfoMH.class, MsgSendClientInfo.class, 2, Side.SERVER);
        INSTANCE.registerMessage(ConfClientInfoMH.class, MsgConfClientInfo.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(ConfClientInfoMH.class, MsgConfClientInfo.class, 1, Side.SERVER);
    }

    public static class DrugPresenceCapMH implements IMessageHandler<MsgDrugPresenceCap, MsgDrugPresenceCap> {
        @Override
        public MsgDrugPresenceCap onMessage(MsgDrugPresenceCap message, MessageContext ctx) {
            if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
                IDrugPresenceCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugPresenceCap.Provider.CAP, null);
                IDrugPresenceCap newCap = message.capability;
                oldCap.getDrugPresenceList().clear();
                oldCap.getDrugPresenceList().putAll(newCap.getDrugPresenceList());
                oldCap.getBreakdownAmountList().clear();
                oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
                oldCap.getBreakdownTickList().clear();
                oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());
            }
            return null;
        }
    }

    public static class SendClientInfoMH implements IMessageHandler<MsgSendClientInfo, MsgConfClientInfo> {
        @Override
        public MsgConfClientInfo onMessage(MsgSendClientInfo message, MessageContext ctx) {
            EntityPlayer player = Euphoria.proxy.getPlayerFromContext(ctx);
            IDrugPresenceCap oldCap = player.getCapability(DrugPresenceCap.Provider.CAP, null);
            IDrugPresenceCap newCap = message.capability;
            oldCap.getDrugPresenceList().clear();
            oldCap.getDrugPresenceList().putAll(newCap.getDrugPresenceList());
            oldCap.getBreakdownAmountList().clear();
            oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
            oldCap.getBreakdownTickList().clear();
            oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());

            if (ctx.side.isServer()) { //Server
                player.getEntityData().setLong("client_ticks", message.clientTicks);
                if (player.getEntityData().hasKey("active_presences")) {
                    for (int i = 0; i < player.getEntityData().getInteger("active_presences"); i++) {
                        player.getEntityData().removeTag("active_presence:"+i+":s");
                        player.getEntityData().removeTag("active_presence:"+i+":a");
                        player.getEntityData().removeTag("active_presence:"+i+":d");
                        player.getEntityData().removeTag("active_presence:"+i+":t");
                    }
                }
                if (message.activePresences == null) {
                    player.getEntityData().setInteger("active_presences", 0);
                } else {
                    AtomicInteger count = new AtomicInteger();
                    message.activePresences.forEach(((presence, tick) -> {
                        player.getEntityData().setString("active_presence:"+count.incrementAndGet()+":s", presence.substance.getRegistryName().toString());
                        player.getEntityData().setFloat("active_presence:"+count.get()+":a", presence.amount);
                        player.getEntityData().setInteger("active_presence:"+count.get()+":d", presence.delay);
                        player.getEntityData().setLong("active_presence:"+count.get()+":t", tick);
                    }));
                    player.getEntityData().setInteger("active_presences", count.get());
                }
            } else { //Client
                EventHandler.clientPlayerTicks = message.clientTicks > 0 ? message.clientTicks : 1;
                if (!message.activePresences.isEmpty()) {
                    message.activePresences.forEach((DrugPresence::activate));
                }
            }
            System.out.println("MsgSendClientInfo received; updated THC: "+ oldCap.getDrugPresenceList().get(Elements.DrugSubstances.THC));
            return new MsgConfClientInfo(true);
        }
    }

    public static class ConfClientInfoMH implements IMessageHandler<MsgConfClientInfo, MsgSendClientInfo> {
        @Override
        public MsgSendClientInfo onMessage(MsgConfClientInfo message, MessageContext ctx) {
            EntityPlayer player = Euphoria.proxy.getPlayerFromContext(ctx);
            if (message.get()) { //Common True
                if (ctx.side.isClient()) { //Client True
                    EventHandler.confDisconnectInfo = true;
                }
                return null;
            } else { //Common False
                HashMap<DrugPresence, Long> activePresences = new HashMap<>();
                long clientTicks;
                if (ctx.side.isClient()) { //Client False
                    if (EventHandler.clientPlayerTicks == 0L) {
                        return null;
                    } else {
                        activePresences = DrugPresence.activePresences;
                        clientTicks = EventHandler.clientPlayerTicks;
                    }
                } else { //Server False
                    if (player.getEntityData().hasKey("active_presences")) {
                        for (int i = 0; i < player.getEntityData().getInteger("active_presences"); i++) {
                            DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(player.getEntityData().getString("active_presence:"+i+":s")));
                            float amount = player.getEntityData().getFloat("active_presence:"+i+":a");
                            int delay = player.getEntityData().getInteger("active_presence:"+i+":d");
                            long tick = player.getEntityData().getLong("active_presence:"+i+":t");
                            activePresences.put(new DrugPresence(substance, amount, delay), tick);
                        }
                    } else {
                        player.getEntityData().setInteger("active_presences", 0);
                    }
                    clientTicks = player.getEntityData().getLong("client_ticks");
                }
                return new MsgSendClientInfo(player.getCapability(DrugPresenceCap.Provider.CAP, null), activePresences, clientTicks);
            }
        }
    }
}