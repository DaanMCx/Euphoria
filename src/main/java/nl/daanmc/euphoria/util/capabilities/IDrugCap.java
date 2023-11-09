package nl.daanmc.euphoria.util.capabilities;

import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.drugs.DrugPresence;
import nl.daanmc.euphoria.util.IScheduledTask;

import java.util.ArrayList;
import java.util.HashMap;

public interface IDrugCap {
    long getClientTicks();
    void setClientTicks(long ticks);
    ArrayList<IScheduledTask> getClientTasks();
    HashMap<DrugSubstance, Float> getDrugs();
    HashMap<DrugSubstance, Long> getBreakdownTicks();
    HashMap<DrugSubstance, Float> getBreakdownAmounts();
    HashMap<DrugPresence, Long> getActivePresences();
}