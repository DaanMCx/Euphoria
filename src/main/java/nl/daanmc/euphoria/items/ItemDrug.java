package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.IDrug;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemDrug extends Item implements IDrug {
    private final HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();
    private final boolean isSmokable;
    private final int maxUseDuration;
    public ItemDrug(String name, int maxFullUses, int maxUseDuration, boolean isSmokable) {
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Tabs.EUPHORIA);
        setMaxDamage(maxFullUses * maxUseDuration);
        setMaxStackSize(1);
        setHasSubtypes(false);
        this.maxUseDuration = maxUseDuration>0? maxUseDuration : 1;
        this.isSmokable = isSmokable;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return this.maxUseDuration;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (isSmokable) {
            playerIn.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 0.5F, 4F);
        } else playerIn.playSound(SoundEvents.ENTITY_PLAYER_BREATH, 0.5F, 3F);
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        stack.damageItem(1, player);
        super.onUsingTick(stack, player, count);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            activateDrug((EntityPlayer) entityLiving,1F,false);
            System.out.println("ItemUseFinish");
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer) {
            float multiplier = (float) (maxUseDuration-timeLeft)/maxUseDuration;
            activateDrug((EntityPlayer) entityLiving,multiplier,false);
            System.out.println("ItemStoppedUsing tl: "+timeLeft+", mp: "+multiplier);
        }
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    @Override
    public HashMap<String, ArrayList<DrugPresence>> getPresenceTable() {
        return presenceTable;
    }

    @Override
    public boolean isSmokable() {
        return isSmokable;
    }
}