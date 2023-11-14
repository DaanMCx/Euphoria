package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.util.IDrug;

public class ItemDrug extends Item implements IDrug {
    private final int useDuration;
    public ItemDrug(String name, int maxUses, int itemUseDuration) {
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Tabs.EUPHORIA);
        setMaxDamage(maxUses);
        setMaxStackSize(1);
        setHasSubtypes(false);
        this.useDuration = itemUseDuration;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return this.useDuration;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        stack.damageItem(1, entityLiving);
        if (entityLiving instanceof EntityPlayer) {
            this.activateDrugPresences((EntityPlayer) entityLiving);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public boolean isSmokable() {
        return false;
    }
}