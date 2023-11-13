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
import nl.daanmc.euphoria.drugs.IDrug;
import nl.daanmc.euphoria.drugs.IDrug.ConsumptionType;

public class ClientProxy implements IProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public EntityPlayer getPlayerFromContext(MessageContext ctx) {
        return ctx.side.isClient() ? Minecraft.getMinecraft().player : ctx.getServerHandler().player;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {
        IDrug.delayModifiers.put(ConsumptionType.EAT, 0.8F);
        IDrug.delayModifiers.put(ConsumptionType.DRINK, 0.5F);
        IDrug.delayModifiers.put(ConsumptionType.SMOKE, 0.15F);
        IDrug.delayModifiers.put(ConsumptionType.SNORT, 0.05F);
        IDrug.delayModifiers.put(ConsumptionType.INJECT, 0.1F);
        IDrug.delayModifiers.put(ConsumptionType.SWALLOW, 0.7F);
        IDrug.delayModifiers.put(ConsumptionType.TAKE, 0F);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}