package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.IDrug;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemEdibleDrug extends ItemFood implements IDrug {
    private final HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();
    public ItemEdibleDrug(String name, int amount, float saturation) {
        super(amount, saturation, false);
        this.setCreativeTab(Tabs.EUPHORIA);
        this.setRegistryName(name);
        this.setTranslationKey(name);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            this.activateDrugPresences((EntityPlayer) entityLiving);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public HashMap<String, ArrayList<DrugPresence>> getPresenceTable() {
        return presenceTable;
    }

    @Override
    public boolean isSmokable() {
        return false;
    }
}