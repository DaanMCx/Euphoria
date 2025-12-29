package nl.daanmc.euphoria.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap;
import nl.daanmc.euphoria.util.messages.MsgReqConfPDCap.Type;
import nl.daanmc.euphoria.util.messages.MsgSyncPDCap;

@Mod.EventBusSubscriber
public class EventHandler {
    public static boolean confCap = true;

    //Server
    @SubscribeEvent
    public static void onPlayerSaveToFile(SaveToFile event) {
        NetworkHandler.INSTANCE.sendTo(new MsgReqConfPDCap(Type.REQUEST), (EntityPlayerMP) event.getEntityPlayer());
    }

    //Server
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        IPlayerDrugsCap PDCap = event.player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        Euphoria.Content.DRUGS.forEach(drug -> {
            PDCap.getPlayerDrugs().putIfAbsent(drug, 0F);
            PDCap.getBreakdownTicks().putIfAbsent(drug, 0L);
            PDCap.getBreakdownAmounts().putIfAbsent(drug, 0F);
        });
    }

    //Server
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        NetworkHandler.INSTANCE.sendTo(new MsgSyncPDCap(event.player.getCapability(PlayerDrugsCap.Provider.CAP,null)), (EntityPlayerMP) event.player);
    }

    //Common
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        //Attach Drug capability to players
        if(event.getObject() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getObject();
            event.addCapability(new ResourceLocation(Euphoria.MODID, "player_drugs_cap"), new PlayerDrugsCap.Provider(player));
        }
    }
}