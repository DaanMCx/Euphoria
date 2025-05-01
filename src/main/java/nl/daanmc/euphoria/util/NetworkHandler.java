package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.messages.MsgDrugPresence;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap.Type;
import nl.daanmc.euphoria.util.messages.MsgSyncPDCap;

public final class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(MsgReqConfPDCap.Handler.class, MsgReqConfPDCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(MsgReqConfPDCap.Handler.class, MsgReqConfPDCap.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SyncPDCapMH.class, MsgSyncPDCap.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(SyncPDCapMH.class, MsgSyncPDCap.class, 1, Side.SERVER);
        INSTANCE.registerMessage(DrugPresenceMH.class, MsgDrugPresence.class, 2, Side.CLIENT);
    }

    public static class SyncPDCapMH implements IMessageHandler<MsgSyncPDCap, MsgReqConfPDCap> {
        @Override
        public MsgReqConfPDCap onMessage(MsgSyncPDCap message, MessageContext ctx) {
            if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
                IPlayerDrugsCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(PlayerDrugsCap.Provider.CAP, null);
                IPlayerDrugsCap newCap = message.capability;
                oldCap.setClientTick(Math.max(newCap.getClientTick(), 1L));
                oldCap.getPlayerDrugs().clear();
                oldCap.getPlayerDrugs().putAll(newCap.getPlayerDrugs());
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
            return ctx.side.isServer() ? new MsgReqConfPDCap(Type.CONFIRM) : null;
        }
    }

    public static class DrugPresenceMH implements IMessageHandler<MsgDrugPresence,MsgDrugPresence> {
        @Override
        public MsgDrugPresence onMessage(MsgDrugPresence message, MessageContext ctx) {
            message.presenceList.forEach(drugPresence -> drugPresence.activate(Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null).getClientTick()));
            return null;
        }
    }
}