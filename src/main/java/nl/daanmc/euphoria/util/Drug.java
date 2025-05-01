package nl.daanmc.euphoria.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import nl.daanmc.euphoria.Euphoria;

import java.util.HashMap;

public class Drug extends IForgeRegistryEntry.Impl<Drug> {
    public static HashMap<ResourceLocation, Drug> REGISTRY = new HashMap<>();
    private final int breakdownTime;
    public Drug(int breakdownTime) {
        this.breakdownTime = breakdownTime;
        Euphoria.Content.DRUGS.add(this);
    }

    public int getBreakdownTime() {return this.breakdownTime;}

    public static class PhantomDrug extends Drug {
        public PhantomDrug() {
            super(0);
        }
    }
}