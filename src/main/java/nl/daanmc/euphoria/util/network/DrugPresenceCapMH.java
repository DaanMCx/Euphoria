package nl.daanmc.euphoria.util.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import nl.daanmc.euphoria.drugs.presence.IDrugPresenceCap;

public class DrugPresenceCapMH implements IMessageHandler<MsgDrugPresenceCap, MsgDrugPresenceCap> {
    @Override
    public MsgDrugPresenceCap onMessage(MsgDrugPresenceCap message, MessageContext ctx) {
        if (Euphoria.proxy.getPlayerFromContext(ctx) != null) {
            IDrugPresenceCap oldCap = Euphoria.proxy.getPlayerFromContext(ctx).getCapability(DrugPresenceCapProvider.DRUG_PRESENCE_CAP, null);
            IDrugPresenceCap newCap = message.capability;
            oldCap.getDrugPresenceList().clear();
            oldCap.getDrugPresenceList().putAll(newCap.getDrugPresenceList());
            oldCap.getBreakdownAmountList().clear();
            oldCap.getBreakdownAmountList().putAll(newCap.getBreakdownAmountList());
            oldCap.getBreakdownTickList().clear();
            oldCap.getBreakdownTickList().putAll(newCap.getBreakdownTickList());
            System.out.println("DPcap msg received; updated THC: "+ oldCap.getDrugPresenceList().get(Elements.DrugSubstances.THC));
        }
        return null;
    }
}