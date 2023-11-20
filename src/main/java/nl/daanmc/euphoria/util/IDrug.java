package nl.daanmc.euphoria.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.messages.MsgDrugPresence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public interface IDrug {
    /**
     * DON'T OVERRIDE | Should be run in Common proxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param presence The {@code DrugPresence} to attach to the drug's default presences. You may attach multiple different presences to the default.
     */
    default void attachDrugPresence(DrugPresence presence) {
        HashMap<String, ArrayList<DrugPresence>> table = this.getPresenceTable();
        if (table.containsKey("")) {
            table.get("").add(presence);
        } else {
            table.put("", new ArrayList<>(Collections.singletonList(presence)));
        }
    }

    /**
     * DON'T OVERRIDE | Should be run in Common proxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param type A tag which you can use to specify which presence(s) you wish to attach. Use only if you want your drug to activate different presences for different situations.
     * @param presence The {@code DrugPresence} to attach to the drug's specified presences. You may attach multiple different presences per tag.
     */
    default void attachDrugPresence(String type, DrugPresence presence) {
        HashMap<String, ArrayList<DrugPresence>> table = this.getPresenceTable();
        if (table.containsKey(type)) {
            table.get(type).add(presence);
        } else {
            table.put(type, new ArrayList<>(Collections.singletonList(presence)));
        }
    }

    /**
     * DON'T OVERRIDE
     * @return An ArrayList containing the attached default {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences() {
        return this.getPresenceTable().get("");
    }

    /**
     * DON'T OVERRIDE
     * @param type A tag which you can use to specify which presence(s) you wish to get. Use only if you set up your drug to provide different presences for different situations.
     * @return An ArrayList containing the attached specified {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences(String type) {
        return this.getPresenceTable().get(type);
    }

    /**
     * DON'T OVERRIDE | Activates the drug's attached default effects
     * @param player The player on which to activate the attached default {@code DrugPresence}(s).
     * @param serverOnly Put {@code true} if method is called on the server side only, put {@code false} if method is called on both sides or client side only.
     */
    default void activateDrug(EntityPlayer player, boolean serverOnly) {
        HashMap<String, ArrayList<DrugPresence>> table = this.getPresenceTable();
        if (table.containsKey("")) {
            if (serverOnly) {
                NetworkHandler.INSTANCE.sendTo(new MsgDrugPresence(table.get("")), (EntityPlayerMP) player);
            } else {
                if (player.world.isRemote) {
                    table.get("").forEach(presence -> {
                        presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick());
                    });
                }
            }
        } else System.out.println("Drug "+this+" does not have any default DrugPresences attached.");
    }

    /**
     * DON'T OVERRIDE | Activates the drug's attached specified effects
     * @param type A tag which you can use to specify which presence(s) you wish to activate. Use only if you set up your drug to activate different presences for different situations.
     * @param player The player on which to activate the attached specified {@code DrugPresence}(s).
     * @param serverOnly Put {@code true} if method is called on the server side only, put {@code false} if method is called on both sides or client side only.
     */
    default void activateDrug(String type, EntityPlayer player, boolean serverOnly) {
        HashMap<String, ArrayList<DrugPresence>> table = this.getPresenceTable();
        if (table.containsKey(type)) {
            if (serverOnly) {
                NetworkHandler.INSTANCE.sendTo(new MsgDrugPresence(table.get(type)), (EntityPlayerMP) player);
            } else {
                if (player.world.isRemote) {
                    table.get(type).forEach(presence -> {
                        presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick());
                    });
                }
            }
        } else System.out.println("Drug "+this+" does not have any DrugPresences attached with type: '"+type+"'.");
    }

    /**
     * @return Should return the {@code private final HashMap<String, ArrayList<DrugPresence>> NAME = new HashMap<>()} that you defined in your drug's class.
     */
    HashMap<String, ArrayList<DrugPresence>> getPresenceTable();

    /**
     * @return Should return {@code true} if drug can be smoked; {@code false} if not.
     */
    boolean isSmokable();
}