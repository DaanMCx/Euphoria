package nl.daanmc.euphoria.drugs;

import java.util.HashMap;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import nl.daanmc.euphoria.Elements;

public class DrugSubstance extends IForgeRegistryEntry.Impl<DrugSubstance> {
    public static HashMap<ResourceLocation, DrugSubstance> REGISTRY = new HashMap<ResourceLocation, DrugSubstance>();
    private float breakdownSpeed;
    public DrugSubstance(Float breakdownSpeed) {
        this.breakdownSpeed = breakdownSpeed;
        Elements.DRUG_PRESENCE_LIST.add(this);
    }

    public float getBreakdownSpeed() {return this.breakdownSpeed;}

    public static class PhantomDrugSubstance extends DrugSubstance {
        public PhantomDrugSubstance() {
            super(0F);
        }
    }
}
