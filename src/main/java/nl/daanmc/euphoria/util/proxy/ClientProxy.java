package nl.daanmc.euphoria.util.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import nl.daanmc.euphoria.Euphoria;
import nl.daanmc.euphoria.drugs.presence.DrugPresence;
import nl.daanmc.euphoria.drugs.presence.DrugPresenceTask;
import nl.daanmc.euphoria.util.EventHandler;

public class ClientProxy implements IProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public EntityPlayer getPlayerFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : Euphoria.proxy.getPlayerFromContext(ctx));
    }

    @Override
    public void activateDrugPresence(DrugPresence presenceIn) {
        long tick = Minecraft.getMinecraft().player.world.getTotalWorldTime();
        for (int i = 0; i < presenceIn.delay; i++) {
            EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, presenceIn.amount / presenceIn.delay, tick + presenceIn.delay + i));
        }
        EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, tick + (2L * presenceIn.delay) +1L));
        System.out.println("Tasks added");
    }

    @Override
    public void activateDrugPresence(DrugPresence[] presencesIn) {
        long tick = Minecraft.getMinecraft().player.world.getTotalWorldTime();
        for (DrugPresence drugPresenceIn : presencesIn) {
            for (int i = 0; i < drugPresenceIn.delay; i++) {
                EventHandler.pendingTasks.add(new DrugPresenceTask(drugPresenceIn.substance, drugPresenceIn.amount / drugPresenceIn.delay, tick + drugPresenceIn.delay + i));
            }
            EventHandler.pendingTasks.add(new DrugPresenceTask(drugPresenceIn.substance, tick + (2L * drugPresenceIn.delay) +1L));
        }
        System.out.println("Tasks added");
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

}