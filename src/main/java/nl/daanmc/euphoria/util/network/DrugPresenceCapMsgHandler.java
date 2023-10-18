package nl.daanmc.euphoria.util.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
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
            IDrugPresenceCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null);
            IDrugPresenceCap newCap = message.capability;
            oldCap.getPresenceList().clear();
            oldCap.getPresenceList().putAll(newCap.getPresenceList());
            oldCap.getBreakdownAmountList().clear();
            oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
            oldCap.getBreakdownTickList().clear();
            oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());
            return null;
        }
    }
}