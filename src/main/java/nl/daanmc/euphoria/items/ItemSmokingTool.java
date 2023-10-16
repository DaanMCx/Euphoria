package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;

public class ItemSmokingTool extends Item {
    private final int useDuration;
    public ItemSmokingTool(String name, int maxUses, int itemUseDuration) {
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        //TODO: RightClick action
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        //TODO: Usage
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
}