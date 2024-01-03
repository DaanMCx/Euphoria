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
     * DON'T USE
     */
    HashMap<IDrug, HashMap<String, ArrayList<DrugPresence>>> drugPresenceTable = new HashMap<>();

    /**
     * DON'T OVERRIDE | Chainable | Should be run in Common proxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param presence The {@code DrugPresence} to attach to the drug's default presences. You may attach multiple different presences to the default.
     */
    default IDrug attachDrugPresence(DrugPresence presence) {
        if (!drugPresenceTable.containsKey(this)) {
            drugPresenceTable.put(this, new HashMap<>());
        }
        HashMap<String, ArrayList<DrugPresence>> table = drugPresenceTable.get(this);
        if (table.containsKey("")) {
            table.get("").add(presence);
        } else {
            table.put("", new ArrayList<>(Collections.singletonList(presence)));
        } return this;
    }

    /**
     * DON'T OVERRIDE | Chainable | Should be run in Common proxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param type A tag which you can use to specify which presence(s) you wish to attach. Use only if you want your drug to activate different presences for different situations.
     * @param presence The {@code DrugPresence} to attach to the drug's specified presences. You may attach multiple different presences per tag.
     */
    default IDrug attachDrugPresence(String type, DrugPresence presence) {
        if (!drugPresenceTable.containsKey(this)) {
            drugPresenceTable.put(this, new HashMap<>());
        }
        HashMap<String, ArrayList<DrugPresence>> table = drugPresenceTable.get(this);
        if (table.containsKey(type)) {
            table.get(type).add(presence);
        } else {
            table.put(type, new ArrayList<>(Collections.singletonList(presence)));
        } return this;
    }

    /**
     * DON'T OVERRIDE
     * @return An ArrayList containing the attached default {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences() {
        if (!drugPresenceTable.containsKey(this)) {
            System.out.println("Drug "+this+" does not have any DrugPresences attached.");
            return new ArrayList<>();
        } else if (!drugPresenceTable.get(this).containsKey("")) {
            System.out.println("Drug "+this+" doesn't have any default DrugPresences attached.");
            return new ArrayList<>();
        } else {
            return drugPresenceTable.get(this).get("");
        }
    }

    /**
     * DON'T OVERRIDE
     * @param type A tag which you can use to specify which presence(s) you wish to get. Use only if you set up your drug to provide different presences for different situations.
     * @return An ArrayList containing the attached specified {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences(String type) {
        if (type.isEmpty()) return this.getDrugPresences();
        if (!drugPresenceTable.containsKey(this)) {
            System.out.println("Drug "+this+" does not have any DrugPresences attached.");
            return new ArrayList<>();
        } else if (!drugPresenceTable.get(this).containsKey(type)) {
            System.out.println("Drug "+this+" doesn't have any DrugPresences attached with type: '"+type+"'.");
            return new ArrayList<>();
        } else {
            return drugPresenceTable.get(this).get(type);
        }
    }

    /**
     * DON'T OVERRIDE | Activates the drug's attached default effects
     * @param player The player on which to activate the attached default {@code DrugPresence}(s).
     * @param serverOnly Put {@code true} if method is called on the server side only, put {@code false} if method is called on both sides or client side only.
     */
    default void activateDrug(EntityPlayer player, float multiplier, boolean serverOnly) {
        if (!drugPresenceTable.containsKey(this)) {
            System.out.println("Drug "+this+" doesn't have any DrugPresences attached.");
            return;
        }
        HashMap<String, ArrayList<DrugPresence>> table = drugPresenceTable.get(this);
        if (table.containsKey("")) {
            ArrayList<DrugPresence> presences = new ArrayList<>(table.get("").size());
            table.get("").forEach(presence -> presences.add(new DrugPresence(presence.substance, presence.amount*multiplier, presence.incubation, Math.round(presence.delay*multiplier))));
            if (serverOnly) {
                NetworkHandler.INSTANCE.sendTo(new MsgDrugPresence(presences), (EntityPlayerMP) player);
            } else {
                if (player.world.isRemote) {
                    presences.forEach(presence -> {
                        presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick());
                    });
                }
            }
        } else System.out.println("Drug "+this+" doesn't have any default DrugPresences attached.");
    }

    /**
     * DON'T OVERRIDE | Activates the drug's attached specified effects
     * @param type A tag which you can use to specify which presence(s) you wish to activate. Use only if you set up your drug to activate different presences for different situations.
     * @param player The player on which to activate the attached specified {@code DrugPresence}(s).
     * @param serverOnly Put {@code true} if method is called on the server side only, put {@code false} if method is called on both sides or client side only.
     */
    default void activateDrug(String type, EntityPlayer player, float multiplier, boolean serverOnly) {
        if (!drugPresenceTable.containsKey(this)) {
            System.out.println("Drug "+this+" doesn't have any DrugPresences attached.");
            return;
        }
        HashMap<String, ArrayList<DrugPresence>> table = drugPresenceTable.get(this);
        if (table.containsKey(type)) {
            ArrayList<DrugPresence> presences = new ArrayList<>(table.get(type).size());
            table.get(type).forEach(presence -> presences.add(new DrugPresence(presence.substance, presence.amount*multiplier, presence.incubation, Math.round(presence.delay*multiplier))));
            if (serverOnly) {
                NetworkHandler.INSTANCE.sendTo(new MsgDrugPresence(presences), (EntityPlayerMP) player);
            } else {
                if (player.world.isRemote) {
                    presences.forEach(presence -> {
                        presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick());
                    });
                }
            }
        } else System.out.println("Drug "+this+" doesn't have any DrugPresences attached with type: '"+type+"'.");
    }

    /**
     * @return Should return {@code true} if drug can be put in and smoked using ItemSmokingTools; {@code false} if not.
     */
    boolean isSmokableInTool();
}