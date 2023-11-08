package nl.daanmc.euphoria.util.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(ReqDrugCapMH.class, MsgReqDrugCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(ReqDrugCapMH.class, MsgReqDrugCap.class, 0, Side.SERVER);
        INSTANCE.registerMessage(SyncDrugCapMH.class, MsgSyncDrugCap.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(SyncDrugCapMH.class, MsgSyncDrugCap.class, 1, Side.SERVER);
    }

    public static class ReqDrugCapMH implements IMessageHandler<MsgReqDrugCap, MsgSyncDrugCap> {
        @Override
        public MsgSyncDrugCap onMessage(MsgReqDrugCap message, MessageContext ctx) {
            IDrugCap drugCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugCap.Provider.CAP, null);
            if (ctx.side.isClient() && !(drugCap.getClientTicks() > 0L)) {
                return null;
            } else {
                return new MsgSyncDrugCap(drugCap);
            }
        }
    }

    public static class SyncDrugCapMH implements IMessageHandler<MsgSyncDrugCap, MsgSyncDrugCap> {
        @Override
        public MsgSyncDrugCap onMessage(MsgSyncDrugCap message, MessageContext ctx) {
            if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
                IDrugCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugCap.Provider.CAP, null);
                IDrugCap newCap = message.capability;
                oldCap.setClientTicks(Math.max(newCap.getClientTicks(), 1L));
                oldCap.getActivePresences().clear();
                oldCap.getActivePresences().putAll(newCap.getActivePresences());
                oldCap.getDrugs().clear();
                oldCap.getDrugs().putAll(newCap.getDrugs());
                oldCap.getBreakdownAmounts().clear();
                oldCap.getBreakdownAmounts().putAll(newCap.getBreakdownAmounts());
                oldCap.getBreakdownTicks().clear();
                oldCap.getBreakdownTicks().putAll(newCap.getBreakdownTicks());
            }
            return null;
        }
    }
}