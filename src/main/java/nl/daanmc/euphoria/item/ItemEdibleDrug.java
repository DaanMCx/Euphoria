package nl.daanmc.euphoria.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.util.IDrug;

public class ItemEdibleDrug extends ItemFood implements IDrug {
    public ItemEdibleDrug(String name, int amount, float saturation) {
        super(amount, saturation, false);
        this.setCreativeTab(Tabs.EUPHORIA);
        this.setRegistryName(name);
        this.setTranslationKey(name);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            activateDrug((EntityPlayer) entityLiving,1F,false);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public boolean isSmokableInTool() {
        return false;
    }
}