package nl.daanmc.euphoria;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
import net.minecraftforge.registries.RegistryBuilder;
import nl.daanmc.euphoria.Elements.Blocks;
import nl.daanmc.euphoria.Elements.DrugSubstances;
import nl.daanmc.euphoria.Elements.Items;
import nl.daanmc.euphoria.Elements.Tabs;
import nl.daanmc.euphoria.blocks.BlockDryingTable;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.drugs.DrugPresence;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.items.ItemEdibleDrug;
import nl.daanmc.euphoria.items.ItemSmokingTool;
import nl.daanmc.euphoria.items.ItemUsableDrug;
import nl.daanmc.euphoria.util.EventHandler;
import nl.daanmc.euphoria.util.network.NetworkHandler;
import nl.daanmc.euphoria.util.proxy.IProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = Euphoria.MODID, name = Euphoria.NAME, version = Euphoria.VERSION, acceptedMinecraftVersions = Euphoria.MCVERSION)
@EventBusSubscriber
public class Euphoria {
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
        CapabilityManager.INSTANCE.register(IDrugCap.class, new DrugCap.Storage(), DrugCap::new);
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        NetworkHandler.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        Elements.DRUG_SUBSTANCE_LIST.forEach((substance) -> DrugSubstance.REGISTRY.put(substance.getRegistryName(), substance));
        Items.DRIED_RED_MUSHROOM.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.PSILOCYBIN, 20F, 2000)
        });
        Items.SUSPICIOUS_MUFFIN.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.THC, 30F, 2000),
            new DrugPresence(DrugSubstances.CBD, 30F, 2000)
        });
        Items.COCAINE.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.COCAINE, 10F, 100)
        });
        Items.CIGAR.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.NICOTINE, 10F, 100)
        });
        Items.CIGARETTE.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.NICOTINE, 10F, 100)
        });
        Items.JOINT.attachPresences(new DrugPresence[] {
            new DrugPresence(DrugSubstances.THC, 10F, 200),
            new DrugPresence(DrugSubstances.CBD, 10F, 200)
        });
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
        event.getRegistry().registerAll(
                new Item().setRegistryName("cannabis_bud").setTranslationKey("cannabis_bud").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("cannabis_bud_dried").setTranslationKey("cannabis_bud_dried").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("cannabis_leaf").setTranslationKey("cannabis_leaf").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("cannabis_seeds").setTranslationKey("cannabis_seeds").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("coca_leaf").setTranslationKey("coca_leaf").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("coca_seeds").setTranslationKey("coca_seeds").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("hop_cone").setTranslationKey("hop_cone").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("hop_seeds").setTranslationKey("hop_seeds").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco").setTranslationKey("tobacco").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf").setTranslationKey("tobacco_leaf").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_leaf_dried").setTranslationKey("tobacco_leaf_dried").setCreativeTab(Tabs.EUPHORIA),
                new Item().setRegistryName("tobacco_seeds").setTranslationKey("tobacco_seeds").setCreativeTab(Tabs.EUPHORIA),
                new ItemSmokingTool("bong", 2, 64),
                new ItemSmokingTool("smoking_pipe", 8, 24),
                new ItemUsableDrug("cigar", 20, 18),
                new ItemUsableDrug("cigarette", 10, 18),
                new ItemUsableDrug("joint", 10, 24),
                new ItemUsableDrug("cocaine", 5, 5),
                new ItemEdibleDrug("suspicious_muffin", 4, 5F),
                new ItemEdibleDrug("dried_red_mushroom", 2, 3F),
                new ItemBlock(Blocks.DRYING_TABLE).setRegistryName(Blocks.DRYING_TABLE.getRegistryName())
        );
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new BlockDryingTable()
        );
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
        for (Item item : Elements.ITEM_LIST) {
            proxy.registerItemRenderer(item, 0, "inventory");
        }
        for (Block block : Elements.BLOCK_LIST) {
            proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");   
        }
    }
}