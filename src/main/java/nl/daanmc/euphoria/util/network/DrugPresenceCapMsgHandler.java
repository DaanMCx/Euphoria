package nl.daanmc.euphoria.util.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;

public class DrugPresenceCapMsgHandler {
    public static class ReqDrugPresenceCapMsgHandler implements IMessageHandler<MsgReqDrugPresenceCap, MsgSendDrugPresenceCap> {
        @Override
        public MsgSendDrugPresenceCap onMessage(MsgReqDrugPresenceCap message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                return new MsgSendDrugPresenceCap(Minecraft.getMinecraft().player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null));
            } else {
                return null;
            }
        }
    }

    public static class SendDrugPresenceCapMsgHandler implements IMessageHandler<MsgSendDrugPresenceCap, MsgSendDrugPresenceCap> {
        @Override
        public MsgSendDrugPresenceCap onMessage(MsgSendDrugPresenceCap message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                message.capability.getPresenceList().forEach((drugSubstance, amount) -> {
                    ctx.getServerHandler().player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, amount);
                });
            } else {
                message.capability.getPresenceList().forEach((drugSubstance, amount) -> {
                    Minecraft.getMinecraft().player.getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null).getPresenceList().put(drugSubstance, amount);
                });
            }
            return null;
        }
    }
}