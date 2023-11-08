package nl.daanmc.euphoria.util.capabilities;

import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.DrugPresence;

import java.util.HashMap;

public interface IDrugCap {
    long getClientTicks();
    void setClientTicks(long ticks);
    void tickClient();
    HashMap<DrugSubstance, Float> getDrugs();
    HashMap<DrugSubstance, Long> getBreakdownTicks();
    HashMap<DrugSubstance, Float> getBreakdownAmounts();
    HashMap<DrugPresence, Long> getActivePresences();
}