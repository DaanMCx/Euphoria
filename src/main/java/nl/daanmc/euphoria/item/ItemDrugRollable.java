package nl.daanmc.euphoria.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class ItemDrugRollable extends ItemDrug {
    public ItemDrugRollable(String name, int maxFullUses, int maxUseDuration) {
        super(name, maxFullUses, maxUseDuration, false);
    }

    @Override
    void playSound(EntityPlayer player) {
        player.playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 0.5F, 4F);
    }
}