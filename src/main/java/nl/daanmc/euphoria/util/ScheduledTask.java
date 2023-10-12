package nl.daanmc.euphoria.util;

public interface ScheduledTask {
    long getTick();
    void add(int times);
    void execute();
}
