package nl.daanmc.euphoria;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import nl.daanmc.euphoria.blocks.BlockCannabisCrop;
import nl.daanmc.euphoria.blocks.BlockDrugPlant;
import nl.daanmc.euphoria.blocks.BlockDryingTable;
import nl.daanmc.euphoria.items.*;
import nl.daanmc.euphoria.util.DrugSubstance;

import java.util.ArrayList;

public final class Elements {
    @ObjectHolder(Euphoria.MODID)
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
    }
    public static ArrayList<Item> ITEMS = new ArrayList<>();

    @ObjectHolder(Euphoria.MODID)
    public static final class Blocks {
        public static final BlockDryingTable DRYING_TABLE = null;
        public static final BlockDrugPlant CANNABIS_PLANT = null;
        public static final BlockCannabisCrop CANNABIS_CROP = null;
    }
    public static ArrayList<Block> BLOCKS = new ArrayList<>();

    @ObjectHolder(Euphoria.MODID)
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
                return new ItemStack(Items.CANNABIS_LEAF);
            }
        };
    }
    public static ArrayList<CreativeTabs> TABS = new ArrayList<>();
}