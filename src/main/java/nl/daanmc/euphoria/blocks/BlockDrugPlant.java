package nl.daanmc.euphoria.blocks;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.util.IStringSerializable;

public class BlockDrugPlant extends BlockDoublePlant {
    public BlockDrugPlant() {
        super();
        this.setTranslationKey("drugPlant");
    }

    public enum EnumPlantType implements IStringSerializable {
        CANNABIS(0, "cannabis"),
        COCA(1, "coca"),
        TOBACCO(2, "tobacco");

        private static final EnumPlantType[] META_LOOKUP = new EnumPlantType[values().length];
        private final int meta;
        private final String name;

        EnumPlantType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static EnumPlantType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getTranslationKey() {
            return this.name;
        }

        static
        {
            for (EnumPlantType type : values())
            {
                META_LOOKUP[type.getMeta()] = type;
            }
        }


    }
}