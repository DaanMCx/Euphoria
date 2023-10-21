package nl.daanmc.euphoria.util.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Euphoria;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(DrugPresenceCapMsgHandler.ReqDrugPresenceCapMsgHandler.class, MsgReqDrugPresenceCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(DrugPresenceCapMsgHandler.SendDrugPresenceCapMsgHandler.class, MsgSendDrugPresenceCap.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(DrugPresenceCapMsgHandler.SendDrugPresenceCapMsgHandler.class, MsgSendDrugPresenceCap.class, 1, Side.SERVER);
        INSTANCE.registerMessage(ClientInfoMsgHandler.ConfClientInfoMsgHandler.class, MsgConfClientInfo.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(ClientInfoMsgHandler.SendClientInfoMsgHandler.class, MsgSendClientInfo.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(ClientInfoMsgHandler.SendClientInfoMsgHandler.class, MsgSendClientInfo.class, 3, Side.SERVER);
    }
}