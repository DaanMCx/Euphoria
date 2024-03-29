package nl.daanmc.euphoria;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import nl.daanmc.euphoria.Elements.DrugSubstances;
import nl.daanmc.euphoria.block.BlockCannabisCrop;
import nl.daanmc.euphoria.block.BlockDrugPlant;
import nl.daanmc.euphoria.block.BlockDryingTable;
import nl.daanmc.euphoria.tileentity.TileEntityCannabisStrain;
import nl.daanmc.euphoria.tileentity.TileEntityDryingTable;
import nl.daanmc.euphoria.item.*;
import nl.daanmc.euphoria.util.*;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.proxy.IProxy;
import nl.daanmc.euphoria.worldgen.feature.WorldGenCannabisPlant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

@Mod(modid = Euphoria.MODID, name = Euphoria.NAME, version = Euphoria.VERSION, acceptedMinecraftVersions = Euphoria.MCVERSION)
@EventBusSubscriber
public final class Euphoria {
    public static final String MODID = "euphoria";
    public static final String NAME = "Euphoria";
    public static final String VERSION = "1.0";
    public static final String MCVERSION = "[1.12,1.12.2]";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.Instance
    public static Euphoria instance;

    @SidedProxy(modId = MODID, clientSide = "nl.daanmc.euphoria.util.proxy.ClientProxy", serverSide = "nl.daanmc.euphoria.util.proxy.ServerProxy")
    public static IProxy proxy;

    //CommonProxy methods here
    @Mod.EventHandler
    void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        CapabilityManager.INSTANCE.register(IDrugCap.class, new DrugCap.Storage(), DrugCap::new);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkHandler.init();
        GameRegistry.registerWorldGenerator(new WorldGenCannabisPlant(), 0);
        GameRegistry.registerTileEntity(TileEntityDryingTable.class, new ResourceLocation(MODID, "drying_table"));
        GameRegistry.registerTileEntity(TileEntityCannabisStrain.class, new ResourceLocation(MODID, "cannabis_strain"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        Elements.SUBSTANCES.forEach((substance) -> DrugSubstance.REGISTRY.put(substance.getRegistryName(), substance));
        Elements.Items.COCAINE.attachDrugPresence(new DrugPresence(DrugSubstances.COCAINE, 20, 100, 500));
        Elements.Items.CIGARETTE.attachDrugPresence(new DrugPresence(DrugSubstances.NICOTINE, 5,100,200));
        Elements.Blocks.CANNABIS_PLANT.setDrops(Elements.Items.CANNABIS_SEEDS);
    }
    
    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        RegistryBuilder<DrugSubstance> builder = new RegistryBuilder<>();
        ResourceLocation key = new ResourceLocation(MODID, "drug_substance");
        builder.setType(DrugSubstance.class)
                .setName(key)
                .setDefaultKey(key)
                .set(key1 -> new DrugSubstance.PhantomDrugSubstance().setRegistryName(key1))
                .set((key12, isNetwork) -> new DrugSubstance.PhantomDrugSubstance().setRegistryName(key12))
                .create();
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        Item[] ITEMS = {
                new Item().setRegistryName("cannabis_bud").setTranslationKey("cannabis_bud").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("cannabis_leaf").setTranslationKey("cannabis_leaf").setCreativeTab(Elements.Tabs.EUPHORIA),
                new ItemSeeds(Elements.Blocks.CANNABIS_CROP, Blocks.FARMLAND).setRegistryName("cannabis_seeds").setTranslationKey("cannabis_seeds"),
                new Item().setRegistryName("coca_leaf").setTranslationKey("coca_leaf").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("coca_seeds").setTranslationKey("coca_seeds").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("hop_cone").setTranslationKey("hop_cone").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("hop_seeds").setTranslationKey("hop_seeds").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf").setTranslationKey("tobacco_leaf").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf_dried").setTranslationKey("tobacco_leaf_dried").setCreativeTab(Elements.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_seeds").setTranslationKey("tobacco_seeds").setCreativeTab(Elements.Tabs.EUPHORIA),
                new ItemDrug("cannabis_bud_dried", 2, 30, true),
                new ItemDrug("tobacco", 4,30, true),
                new ItemSmokingTool("bong", 2, 64),
                new ItemSmokingTool("smoking_pipe", 8, 24),
                new ItemDrugRollable("cigar", 20, 18),
                new ItemDrugRollable("cigarette", 10, 18),
                new ItemDrugRollable("joint", 10, 24),
                new ItemCocaine("cocaine", 5, 30),
                new ItemEdibleDrug("suspicious_muffin", 4, 5F),
                new ItemEdibleDrug("dried_red_mushroom", 2, 3F),
                new ItemBlock(Elements.Blocks.DRYING_TABLE).setRegistryName(Elements.Blocks.DRYING_TABLE.getRegistryName()),
                new ItemBlock(Elements.Blocks.CANNABIS_PLANT).setRegistryName(Elements.Blocks.CANNABIS_PLANT.getRegistryName()),
                new ItemBlock(Elements.Blocks.CANNABIS_CROP).setRegistryName(Elements.Blocks.CANNABIS_CROP.getRegistryName())
        };
        event.getRegistry().registerAll(ITEMS);
        Elements.ITEMS.addAll(Arrays.asList(ITEMS));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        Block[] BLOCKS = {
                new BlockDryingTable(),
                new BlockDrugPlant("cannabis_plant"),
                new BlockCannabisCrop()
        };
        event.getRegistry().registerAll(BLOCKS);
        Elements.BLOCKS.addAll(Arrays.asList(BLOCKS));
    }

    @SubscribeEvent
    public static void onSubstanceRegister(RegistryEvent.Register<DrugSubstance> event) {
        event.getRegistry().registerAll(
                new DrugSubstance(6000).setRegistryName("thc"),
                new DrugSubstance(7200).setRegistryName("cbd"),
                new DrugSubstance(12000).setRegistryName("alcohol"),
                new DrugSubstance(3600).setRegistryName("cocaine"),
                new DrugSubstance(12000).setRegistryName("psilocybin"),
                new DrugSubstance(1200).setRegistryName("nicotine"),
                new DrugSubstance(7200).setRegistryName("mescaline")
        );
    }
    
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : Elements.ITEMS) {
            proxy.registerItemRenderer(item, 0, "inventory");
        }
        for (Block block : Elements.BLOCKS) {
            proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");   
        }
    }
}