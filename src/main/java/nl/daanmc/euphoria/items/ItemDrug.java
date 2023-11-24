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
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.IDrug;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemDrug extends Item implements IDrug {
    private final HashMap<String, ArrayList<DrugPresence>> presenceTable = new HashMap<>();
    private final int maxUseDuration;
    private final boolean isSmokableInTool;
    public ItemDrug(String name, int maxFullUses, int maxUseDuration, boolean isSmokableInTool) {
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Tabs.EUPHORIA);
        setMaxDamage(maxFullUses * maxUseDuration);
        setMaxStackSize(1);
        setHasSubtypes(false);
        this.maxUseDuration = maxUseDuration>0? maxUseDuration : 1;
        this.isSmokableInTool = isSmokableInTool;
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
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
        if (player instanceof EntityPlayer) {
            int start = maxUseDuration-10;
            if (count==start) {
                playSound((EntityPlayer) player);
            } else if (count<start) {
                stack.damageItem(1, player);
            }
        }
        super.onUsingTick(stack, player, count);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            activateDrug((EntityPlayer) entityLiving,1F,false);
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (entityLiving instanceof EntityPlayer && timeLeft<(maxUseDuration-10)) {
            float multiplier = (float) (maxUseDuration-timeLeft)/maxUseDuration;
            activateDrug((EntityPlayer) entityLiving,multiplier,false);
        }
        super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
    }

    @Override
    public HashMap<String, ArrayList<DrugPresence>> getPresenceTable() {
        return presenceTable;
    }

    @Override
    public boolean isSmokableInTool() {
        return isSmokableInTool;
    }

    void playSound(EntityPlayer player) {}
}