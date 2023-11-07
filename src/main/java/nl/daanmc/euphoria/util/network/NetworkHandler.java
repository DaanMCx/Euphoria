package nl.daanmc.euphoria.util.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import nl.daanmc.euphoria.Euphoria;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Euphoria.MODID);

    public static void init() {
        INSTANCE.registerMessage(DrugPresenceCapMH.class, MsgDrugPresenceCap.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(DrugPresenceCapMH.class, MsgDrugPresenceCap.class, 0, Side.SERVER);
        INSTANCE.registerMessage(ClientInfoMH.SendClientInfoMH.class, MsgSendClientInfo.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(ClientInfoMH.SendClientInfoMH.class, MsgSendClientInfo.class, 2, Side.SERVER);
        INSTANCE.registerMessage(ClientInfoMH.ConfClientInfoMH.class, MsgConfClientInfo.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(ClientInfoMH.ConfClientInfoMH.class, MsgConfClientInfo.class, 1, Side.SERVER);
    }
}