package nl.daanmc.euphoria.util.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;

public class DrugPresenceCapMsgHandler {
    public static class ReqDrugPresenceCapMsgHandler implements IMessageHandler<MsgReqDrugPresenceCap, MsgSendDrugPresenceCap> {
        @Override
        public MsgSendDrugPresenceCap onMessage(MsgReqDrugPresenceCap message, MessageContext ctx) {
            return new MsgSendDrugPresenceCap(Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null));
        }
    }

    public static class SendDrugPresenceCapMsgHandler implements IMessageHandler<MsgSendDrugPresenceCap, MsgSendDrugPresenceCap> {
        @Override
        public MsgSendDrugPresenceCap onMessage(MsgSendDrugPresenceCap message, MessageContext ctx) {
            if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
                IDrugPresenceCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null);
                IDrugPresenceCap newCap = message.capability;
                oldCap.getDrugPresenceList().clear();
                oldCap.getDrugPresenceList().putAll(newCap.getDrugPresenceList());
                oldCap.getBreakdownAmountList().clear();
                oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
                oldCap.getBreakdownTickList().clear();
                oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());
                System.out.println("Send msg received; updated THC: "+ oldCap.getDrugPresenceList().get(Elements.DrugSubstances.THC));
            } else {
                NetworkHandler.INSTANCE.sendToServer(new MsgReqDrugPresenceCap());
            }
            return null;
        }
    }
}