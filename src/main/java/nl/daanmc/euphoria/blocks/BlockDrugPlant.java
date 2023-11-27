package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockDoublePlant;

public class BlockDrugPlant extends BlockDoublePlant {
    public BlockDrugPlant(String name) {
        super();
        this.setRegistryName(name);
        this.setTranslationKey(name);
    }
}