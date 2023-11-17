package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.messages.MsgDrugPresence;
import nl.daanmc.euphoria.util.messages.MsgReqConfDrugCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfDrugCap.Type;
import nl.daanmc.euphoria.util.messages.MsgSyncDrugCap;

import java.util.concurrent.atomic.AtomicInteger;

public final class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(ReqConfDrugCapMH.class, MsgReqConfDrugCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(ReqConfDrugCapMH.class, MsgReqConfDrugCap.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SyncDrugCapMH.class, MsgSyncDrugCap.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(SyncDrugCapMH.class, MsgSyncDrugCap.class, 1, Side.SERVER);
        INSTANCE.registerMessage(DrugPresenceMH.class, MsgDrugPresence.class, 2, Side.CLIENT);
    }

    public static class ReqConfDrugCapMH implements IMessageHandler<MsgReqConfDrugCap, MsgSyncDrugCap> {
        @Override
        public MsgSyncDrugCap onMessage(MsgReqConfDrugCap message, MessageContext ctx) {
            IDrugCap drugCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugCap.Provider.CAP, null);
            if (ctx.side.isClient() && !(drugCap.getClientTick() > 0L)) {
                return null;
            } else {
                if (message.type == Type.CONFIRM) {
                    EventHandler.confCap = true;
                    return null;
                } else {
                    return new MsgSyncDrugCap(drugCap, message.type==Type.REQUEST_INITIAL);
                }
            }
        }
    }

    public static class SyncDrugCapMH implements IMessageHandler<MsgSyncDrugCap, MsgReqConfDrugCap> {
        @Override
        public MsgReqConfDrugCap onMessage(MsgSyncDrugCap message, MessageContext ctx) {
            if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
                IDrugCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugCap.Provider.CAP, null);
                IDrugCap newCap = message.capability;
                oldCap.setClientTick(Math.max(newCap.getClientTick(), 1L));
                oldCap.getDrugs().clear();
                oldCap.getDrugs().putAll(newCap.getDrugs());
                oldCap.getBreakdownAmounts().clear();
                oldCap.getBreakdownAmounts().putAll(newCap.getBreakdownAmounts());
                oldCap.getBreakdownTicks().clear();
                oldCap.getBreakdownTicks().putAll(newCap.getBreakdownTicks());
                if (ctx.side.isClient() && message.isInitialSync) {
                    newCap.getActivePresences().forEach(DrugPresence::activate);
                } else {
                    oldCap.getActivePresences().clear();
                    oldCap.getActivePresences().putAll(newCap.getActivePresences());
                }
            }
            return ctx.side.isServer() ? new MsgReqConfDrugCap(Type.CONFIRM) : null;
        }
    }

    public static class DrugPresenceMH implements IMessageHandler<MsgDrugPresence,MsgDrugPresence> {
        @Override
        public MsgDrugPresence onMessage(MsgDrugPresence message, MessageContext ctx) {
            AtomicInteger presencesFound = new AtomicInteger();
            Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null).getActivePresences().forEach((presence, tick) -> {
                if (tick == message.tick) {
                    message.presenceList.forEach(newPresence -> {
                        if (newPresence.equals(presence)) {
                            presencesFound.incrementAndGet();
                        }
                    });
                }
            });
            if (presencesFound.get() < message.presenceList.size()) {
                message.presenceList.forEach(drugPresence -> drugPresence.activate(message.tick));
            }
            return null;
        }
    }
}