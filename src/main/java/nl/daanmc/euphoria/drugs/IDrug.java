package nl.daanmc.euphoria.drugs;

import java.util.HashMap;

public interface IDrug {
    enum ConsumptionType {SMOKE, EAT, DRINK, SNORT, INJECT, SWALLOW, TAKE}
    HashMap<ConsumptionType, Float> delayModifiers = new HashMap<>(7);
    ConsumptionType getConsumptionType();
    DrugPresence[] getDrugPresences();
    void attachPresences(DrugPresence[] presencesIn);
}