package nl.daanmc.euphoria;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.items.ItemEdibleDrug;
import nl.daanmc.euphoria.items.ItemSmokingTool;
import nl.daanmc.euphoria.items.ItemUsableDrug;

import java.util.ArrayList;

public final class Elements {
    @ObjectHolder(Euphoria.MODID)
    public static class Items {
        @ObjectHolder("cannabis_bud")
        public static final Item CANNABIS_BUD = null;
        @ObjectHolder("cannabis_bud_dried")
        public static final Item CANNABIS_BUD_DRIED = null;
        @ObjectHolder("cannabis_leaf")
        public static final Item CANNABIS_LEAF = null;
        @ObjectHolder("cannabis_seeds")
        public static final Item CANNABIS_SEEDS = null;
        @ObjectHolder("coca_leaf")
        public static final Item COCA_LEAF = null;
        @ObjectHolder("coca_seeds")
        public static final Item COCA_SEEDS = null;
        @ObjectHolder("hop_cone")
        public static final Item HOP_CONE = null;
        @ObjectHolder("hop_seeds")
        public static final Item HOP_SEEDS = null;
        @ObjectHolder("tobacco")
        public static final Item TOBACCO = null;
        @ObjectHolder("tobacco_leaf")
        public static final Item TOBACCO_LEAF = null;
        @ObjectHolder("tobacco_leaf_dried")
        public static final Item TOBACCO_LEAF_DRIED = null;
        @ObjectHolder("tobacco_seeds")
        public static final Item TOBACCO_SEEDS = null;
        @ObjectHolder("bong")
        public static final ItemSmokingTool BONG = null;
        @ObjectHolder("smoking_pipe")
        public static final ItemSmokingTool SMOKING_PIPE = null;
        @ObjectHolder("cigar")
        public static final ItemUsableDrug CIGAR = null;
        @ObjectHolder("cigarette")
        public static final ItemUsableDrug CIGARETTE = null;
        @ObjectHolder("joint")
        public static final ItemUsableDrug JOINT = null;
        @ObjectHolder("cocaine")
        public static final ItemUsableDrug COCAINE = null;
        @ObjectHolder("suspicious_muffin")
        public static final ItemEdibleDrug SUSPICIOUS_MUFFIN = null;
        @ObjectHolder("dried_red_mushroom")
        public static final ItemEdibleDrug DRIED_RED_MUSHROOM = null;
        @ObjectHolder("drying_table")
        public static final ItemBlock DRYING_TABLE = null;
    }

    public static Item[] ITEM_LIST = {
        Items.CANNABIS_BUD,
        Items.CANNABIS_BUD_DRIED,
        Items.CANNABIS_LEAF,
        Items.CANNABIS_SEEDS,
        Items.COCA_LEAF,
        Items.COCA_SEEDS,
        Items.HOP_CONE,
        Items.HOP_SEEDS,
        Items.TOBACCO,
        Items.TOBACCO_LEAF,
        Items.TOBACCO_LEAF_DRIED,
        Items.TOBACCO_SEEDS,
        Items.DRYING_TABLE,
        Items.BONG,
        Items.CIGAR,
        Items.CIGARETTE,
        Items.COCAINE,
        Items.DRIED_RED_MUSHROOM,
        Items.JOINT,
        Items.SMOKING_PIPE,
        Items.SUSPICIOUS_MUFFIN
    };

    @ObjectHolder(Euphoria.MODID)
    public static class Blocks {
        @ObjectHolder("drying_table")
        public static final Block DRYING_TABLE = null;
    }

    public static Block[] BLOCK_LIST = {
        Blocks.DRYING_TABLE
    };

    @ObjectHolder(Euphoria.MODID)
    public static class DrugSubstances {
        @ObjectHolder("thc")
        public static final DrugSubstance THC = null;
        @ObjectHolder("cbd")
        public static final DrugSubstance CBD = null;
        @ObjectHolder("alcohol")
        public static final DrugSubstance ALCOHOL = null;
        @ObjectHolder("cocaine")
        public static final DrugSubstance COCAINE = null;
        @ObjectHolder("psilocybin")
        public static final DrugSubstance PSILOCYBIN = null;
        @ObjectHolder("nicotine")
        public static final DrugSubstance NICOTINE = null;
        @ObjectHolder("mescaline")
        public static final DrugSubstance MESCALINE = null; 
    }

    public static ArrayList<DrugSubstance> DRUG_SUBSTANCE_LIST = new ArrayList<>();

    public static class Tabs {
        public static final CreativeTabs EUPHORIA = new CreativeTabs("euphoria") {
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.CANNABIS_LEAF);
            }
        };
    }

    public static CreativeTabs[] TABS_LIST = {
        Tabs.EUPHORIA
    };
}