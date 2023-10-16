package nl.daanmc.euphoria.util.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;

public class ServerProxy implements IProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {}

    @Override
    public EntityPlayer getPlayerFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }

    @Override
    public void activateDrugPresence(DrugPresence presenceIn) {}

    @Override
    public void activateDrugPresence(DrugPresence[] presencesIn) {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}