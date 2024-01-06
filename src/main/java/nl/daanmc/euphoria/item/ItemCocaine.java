package nl.daanmc.euphoria.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class ItemCocaine extends ItemDrug {
    public ItemCocaine(String name, int maxFullUses, int maxUseDuration) {
        super(name, maxFullUses, maxUseDuration, true);
    }

    @Override
    void playSound(EntityPlayer player) {
        player.playSound(SoundEvents.ENTITY_PLAYER_BREATH, 0.5F, 3F);
    }
}