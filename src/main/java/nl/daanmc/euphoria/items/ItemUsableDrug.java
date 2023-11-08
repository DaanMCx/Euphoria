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
import nl.daanmc.euphoria.drugs.IDrug;
import nl.daanmc.euphoria.drugs.DrugPresence;

public class ItemUsableDrug extends Item implements IDrug {
    private DrugPresence[] drugPresences = null;
    private final int useDuration;
    public ItemUsableDrug(String name, int maxUses, int itemUseDuration) {
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
            DrugPresence.activatePresence(this.drugPresences, worldIn);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void attachPresences(DrugPresence[] presences) {
        this.drugPresences = presences;
    }    
}