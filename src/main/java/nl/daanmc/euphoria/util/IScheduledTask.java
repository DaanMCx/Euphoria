package nl.daanmc.euphoria.util;

public interface IScheduledTask {
    long getTick();
    void execute();
}