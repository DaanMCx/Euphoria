package nl.daanmc.euphoria.items;

import net.minecraft.item.ItemPotion;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.IDrug;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemDrinkableDrug extends ItemPotion implements IDrug {
    private final HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();
    public ItemDrinkableDrug() {
    }

    @Override
    public HashMap<String, ArrayList<DrugPresence>> getPresenceTable() {
        return presenceTable;
    }

    @Override
    public boolean isSmokableInTool() {
        return false;
    }
}