package nl.daanmc.euphoria.util.capabilities;

import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.tasks.ITask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface IPlayerDrugsCap {
    long getClientTick();
    void doClientTick();
    void setClientTick(long ticks);
    void addClientTask(ITask task, long tick);
    void executeClientTasks();
    ConcurrentHashMap<Long, ArrayList<ITask>> getClientTasks();
    HashMap<DrugSubstance, Float> getPlayerDrugs();
    HashMap<DrugSubstance, Long> getBreakdownTicks();
    HashMap<DrugSubstance, Float> getBreakdownAmounts();
    HashMap<DrugPresence, Long> getActivePresences();
}