package nl.daanmc.euphoria.items;

import net.minecraft.item.ItemPotion;
import nl.daanmc.euphoria.util.IDrug;

public class ItemDrinkableDrug extends ItemPotion implements IDrug {
    public ItemDrinkableDrug() {}

    @Override
    public boolean isSmokableInTool() {
        return false;
    }
}