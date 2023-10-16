package nl.daanmc.euphoria.drugs;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import nl.daanmc.euphoria.Elements;

import java.util.HashMap;

public class DrugSubstance extends IForgeRegistryEntry.Impl<DrugSubstance> {
    public static HashMap<ResourceLocation, DrugSubstance> REGISTRY = new HashMap<>();
    private final int breakdownTime;
    public DrugSubstance(int breakdownTime) {
        this.breakdownTime = breakdownTime;
        Elements.DRUG_PRESENCE_LIST.add(this);
    }

    public int getBreakdownTime() {return this.breakdownTime;}

    public static class PhantomDrugSubstance extends DrugSubstance {
        public PhantomDrugSubstance() {
            super(0);
        }
    }
}