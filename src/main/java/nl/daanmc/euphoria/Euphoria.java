package nl.daanmc.euphoria;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
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
import nl.daanmc.euphoria.block.BlockCannabisCrop;
import nl.daanmc.euphoria.block.BlockDrugPlant;
import nl.daanmc.euphoria.block.BlockDryingTable;
import nl.daanmc.euphoria.block.BlockLargeDrugPlant;
import nl.daanmc.euphoria.item.*;
import nl.daanmc.euphoria.tileentity.TileEntityCannabisStrain;
import nl.daanmc.euphoria.tileentity.TileEntityDryingTable;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.EventHandler;
import nl.daanmc.euphoria.util.NetworkHandler;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.proxy.IProxy;
import nl.daanmc.euphoria.worldgen.feature.EuphoriaSurfaceGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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
        GameRegistry.registerWorldGenerator(new EuphoriaSurfaceGenerator<>(Content.Blocks.CANNABIS_PLANT, 50, 6), 0);
        GameRegistry.registerWorldGenerator(new EuphoriaSurfaceGenerator<>(Content.Blocks.CANNABIS_PLANT_SMALL, 100, 3), 0);
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
        Content.SUBSTANCES.forEach((substance) -> DrugSubstance.REGISTRY.put(substance.getRegistryName(), substance));
        Content.Blocks.CANNABIS_PLANT.setDrops(Content.Items.CANNABIS_SEEDS);
        Content.Blocks.CANNABIS_PLANT_SMALL.setDrops(Content.Items.CANNABIS_SEEDS);
        Content.Items.COCAINE.attachDrugPresence(new DrugPresence(Content.DrugSubstances.COCAINE, 20, 100, 500));
        Content.Items.CIGARETTE.attachDrugPresence(new DrugPresence(Content.DrugSubstances.NICOTINE, 5,100,200));
        //Content.Blocks.TOBACCO_PLANT.setDrops(Content.Items.TOBACCO_SEEDS);
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
                new Item().setRegistryName("cannabis_bud").setTranslationKey("cannabis_bud").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("cannabis_leaf").setTranslationKey("cannabis_leaf").setCreativeTab(Content.Tabs.EUPHORIA),
                new ItemSeeds(Content.Blocks.CANNABIS_CROP, Blocks.FARMLAND).setRegistryName("cannabis_seeds").setTranslationKey("cannabis_seeds"),
                new Item().setRegistryName("coca_leaf").setTranslationKey("coca_leaf").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("coca_seeds").setTranslationKey("coca_seeds").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("hop_cone").setTranslationKey("hop_cone").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("hop_seeds").setTranslationKey("hop_seeds").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf").setTranslationKey("tobacco_leaf").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf_dried").setTranslationKey("tobacco_leaf_dried").setCreativeTab(Content.Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_seeds").setTranslationKey("tobacco_seeds").setCreativeTab(Content.Tabs.EUPHORIA),
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
                new ItemBlock(Content.Blocks.DRYING_TABLE).setRegistryName(Content.Blocks.DRYING_TABLE.getRegistryName()),
                new ItemBlock(Content.Blocks.CANNABIS_PLANT).setRegistryName(Content.Blocks.CANNABIS_PLANT.getRegistryName()),
                new ItemBlock(Content.Blocks.CANNABIS_PLANT_SMALL).setRegistryName(Content.Blocks.CANNABIS_PLANT_SMALL.getRegistryName()),
                new ItemBlock(Content.Blocks.CANNABIS_CROP).setRegistryName(Content.Blocks.CANNABIS_CROP.getRegistryName())
        };
        event.getRegistry().registerAll(ITEMS);
        Content.ITEMS.addAll(Arrays.asList(ITEMS));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        Block[] BLOCKS = {
                new BlockDryingTable(),
                new BlockLargeDrugPlant("cannabis_plant"),
                new BlockDrugPlant("cannabis_plant_small"),
                new BlockCannabisCrop()
        };
        event.getRegistry().registerAll(BLOCKS);
        Content.BLOCKS.addAll(Arrays.asList(BLOCKS));
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
        for (Item item : Content.ITEMS) {
            proxy.registerItemRenderer(item, 0, "inventory");
        }
        for (Block block : Content.BLOCKS) {
            proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");   
        }
    }

    public static final class Content {
        @GameRegistry.ObjectHolder(MODID)
        public static final class Items {
            public static final Item CANNABIS_BUD = null;
            public static final Item CANNABIS_LEAF = null;
            public static final ItemSeeds CANNABIS_SEEDS = null;
            public static final Item COCA_LEAF = null;
            public static final Item COCA_SEEDS = null;
            public static final Item HOP_CONE = null;
            public static final Item HOP_SEEDS = null;
            public static final Item TOBACCO_LEAF = null;
            public static final Item TOBACCO_LEAF_DRIED = null;
            public static final Item TOBACCO_SEEDS = null;
            public static final ItemDrug CANNABIS_BUD_DRIED = null;
            public static final ItemDrug TOBACCO = null;
            public static final ItemSmokingTool BONG = null;
            public static final ItemSmokingTool SMOKING_PIPE = null;
            public static final ItemDrugRollable CIGAR = null;
            public static final ItemDrugRollable CIGARETTE = null;
            public static final ItemDrugRollable JOINT = null;
            public static final ItemCocaine COCAINE = null;
            public static final ItemEdibleDrug SUSPICIOUS_MUFFIN = null;
            public static final ItemEdibleDrug DRIED_RED_MUSHROOM = null;
            public static final ItemBlock DRYING_TABLE = null;
            public static final ItemBlock CANNABIS_PLANT = null;
            public static final ItemBlock CANNABIS_PLANT_SMALL = null;
        }
        public static ArrayList<Item> ITEMS = new ArrayList<>();

        @GameRegistry.ObjectHolder(MODID)
        public static final class Blocks {
            public static final BlockDryingTable DRYING_TABLE = null;
            public static final BlockLargeDrugPlant CANNABIS_PLANT = null;
            public static final BlockDrugPlant CANNABIS_PLANT_SMALL = null;
            public static final BlockCannabisCrop CANNABIS_CROP = null;
        }
        public static ArrayList<Block> BLOCKS = new ArrayList<>();

        @GameRegistry.ObjectHolder(MODID)
        public static final class DrugSubstances {
            public static final DrugSubstance THC = null;
            public static final DrugSubstance CBD = null;
            public static final DrugSubstance ALCOHOL = null;
            public static final DrugSubstance COCAINE = null;
            public static final DrugSubstance PSILOCYBIN = null;
            public static final DrugSubstance NICOTINE = null;
            public static final DrugSubstance MESCALINE = null;
        }
        public static ArrayList<DrugSubstance> SUBSTANCES = new ArrayList<>();

        public static class Tabs {
            public static final CreativeTabs EUPHORIA = new CreativeTabs("euphoria") {
                @Override
                public ItemStack createIcon() {
                    TABS.add(this);
                    return new ItemStack(Content.Items.CANNABIS_LEAF);
                }
            };
        }
        public static ArrayList<CreativeTabs> TABS = new ArrayList<>();

        public static final int GUI_DRYING_TABLE = 2;
    }
}