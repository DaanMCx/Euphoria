package nl.daanmc.euphoria.util;

public interface IScheduledTask {
    enum Side {CLIENT, COMMON, SERVER}
    long getTick();
    Side getSide();
    boolean isPersistent();
    void execute();
}