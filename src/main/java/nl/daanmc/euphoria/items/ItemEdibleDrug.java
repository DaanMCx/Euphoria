package nl.daanmc.euphoria.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.drugs.Drug;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceMsg;
import nl.daanmc.euphoria.util.PacketHandler;

public class ItemEdibleDrug extends ItemFood implements Drug{
    private DrugPresence[] drugPresences;
    public ItemEdibleDrug(String name, int amount, float saturation) {
        super(amount, saturation, false);
        this.setCreativeTab(Tabs.EUPHORIA);
        this.setRegistryName(name);
        this.setTranslationKey(name);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (worldIn.isRemote && entityLiving instanceof EntityPlayer) {
            for (DrugPresence presence : drugPresences) {
                PacketHandler.INSTANCE.sendToServer(new DrugPresenceMsg(presence));
            }
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    @Override
    public void attachPresences(DrugPresence[] presencesIn) {
        this.drugPresences = presencesIn;
    }
}
