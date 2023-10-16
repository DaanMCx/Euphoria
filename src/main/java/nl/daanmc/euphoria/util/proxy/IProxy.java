package nl.daanmc.euphoria.util.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;

public interface IProxy {
    void preInit(FMLPreInitializationEvent event);
    void init(FMLInitializationEvent event);
    void postInit(FMLPostInitializationEvent event);
    void registerItemRenderer(Item item, int meta, String id);
    EntityPlayer getPlayerFromContext(MessageContext ctx);
    void activateDrugPresence(DrugPresence presenceIn);
    void activateDrugPresence(DrugPresence[] presencesIn);
}