package nl.daanmc.euphoria.util;

import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceCapProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {
    public static final ResourceLocation DRUG_PRESENCE_CAP = new ResourceLocation(Euphoria.MODID, "drug_presence");

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getObject();
        event.addCapability(DRUG_PRESENCE_CAP, new DrugPresenceCapProvider(player));
    }
}
