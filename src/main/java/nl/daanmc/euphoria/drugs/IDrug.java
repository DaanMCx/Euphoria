package nl.daanmc.euphoria.drugs;

import net.minecraft.entity.player.EntityPlayer;
import nl.daanmc.euphoria.util.capabilities.DrugCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public interface IDrug {
    HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();

    /**
     * DON'T OVERRIDE | Should be run in ClientProxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param presence The {@code DrugPresence} to attach to the drug's default presences. You may attach multiple different presences to the default.
     */
    default void attachDrugPresence(DrugPresence presence) {
        if (presenceTable.containsKey("")) {
            presenceTable.get("").add(presence);
        } else {
            presenceTable.put("", new ArrayList<>(Collections.singletonList(presence)));
        }
    }

    /**
     * DON'T OVERRIDE | Should be run in ClientProxy {@code postInit()} for each presence you wish to attach to each drug.
     * @param type A tag which you can use to specify which presence(s) you wish to attach. Use only if you want your drug to activate different presences for different situations.
     * @param presence The {@code DrugPresence} to attach to the drug's specified presences. You may attach multiple different presences per tag.
     */
    default void attachDrugPresence(String type, DrugPresence presence) {
        if (presenceTable.containsKey(type)) {
            presenceTable.get(type).add(presence);
        } else {
            presenceTable.put(type, new ArrayList<>(Collections.singletonList(presence)));
        }
    }

    /**
     * DON'T OVERRIDE
     * @return An ArrayList containing the attached default {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences() {
        return presenceTable.get("");
    }

    /**
     * DON'T OVERRIDE
     * @param type A tag which you can use to specify which presence(s) you wish to get. Use only if you set up your drug to provide different presences for different situations.
     * @return An ArrayList containing the attached specified {@code DrugPresence}(s).
     */
    default ArrayList<DrugPresence> getDrugPresences(String type) {
        return presenceTable.get(type);
    }

    /**
     * DON'T OVERRIDE
     * @param player The player on which to activate the attached default {@code DrugPresence}(s).
     */
    default void activateDrugPresences(EntityPlayer player) {
        if (player.world.isRemote && presenceTable.containsKey("")) {
            presenceTable.get("").forEach(presence -> presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick()));
        }
    }

    /**
     * DON'T OVERRIDE
     * @param type A tag which you can use to specify which presence(s) you wish to activate. Use only if you set up your drug to activate different presences for different situations.
     * @param player The player on which to activate the attached specified {@code DrugPresence}(s).
     */
    default void activateDrugPresences(String type, EntityPlayer player) {
        if (player.world.isRemote && presenceTable.containsKey(type)) {
            presenceTable.get(type).forEach(presence -> presence.activate(player.getCapability(DrugCap.Provider.CAP,null).getClientTick()));
        }
    }

    /**
     * @return {@code true} if drug can be smoked; {@code false} if not.
     */
    boolean isSmokable();
}