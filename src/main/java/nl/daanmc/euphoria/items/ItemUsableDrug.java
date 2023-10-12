package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.drugs.Drug;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceMsg;
import nl.daanmc.euphoria.util.PacketHandler;

public class ItemUsableDrug extends Item implements Drug {
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        for (DrugPresence presence : drugPresences) {System.out.println(presence.substance +" "+ presence.amount);}
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        stack.damageItem(1, entityLiving);
        if (worldIn.isRemote && entityLiving instanceof EntityPlayer) {
            for (DrugPresence presence : drugPresences) {
                PacketHandler.INSTANCE.sendToServer(new DrugPresenceMsg(presence));
            }
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void attachPresences(DrugPresence[] presences) {
        this.drugPresences = presences;
    }    
}
