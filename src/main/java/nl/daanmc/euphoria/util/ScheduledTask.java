package nl.daanmc.euphoria.util;

public interface ScheduledTask {
    enum Side {CLIENT, COMMON, SERVER}
    long getTick();
    Side getSide();
    void execute();
}