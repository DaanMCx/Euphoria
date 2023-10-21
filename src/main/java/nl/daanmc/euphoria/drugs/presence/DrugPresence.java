package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.world.World;
import nl.daanmc.euphoria.drugs.DrugSubstance;
import nl.daanmc.euphoria.util.EventHandler;

public class DrugPresence {
    public DrugSubstance substance;
    public float amount;
    public int delay;

    public DrugPresence(DrugSubstance substance, float amount, int delay) {
        this.substance=substance;
        this.amount=amount;
        this.delay=delay;
    }

    public static void activatePresence(DrugPresence presenceIn, World worldIn) {
        if (worldIn.isRemote) {
            long tick = EventHandler.clientPlayerTicks;
            for (int i = 0; i < presenceIn.delay; i++) {
                EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, presenceIn.amount / presenceIn.delay, tick + presenceIn.delay + i));
            }
            EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, tick + (2L * presenceIn.delay) +1L));
            System.out.println("Tasks added");
        }
    }

    public static void activatePresence(DrugPresence[] presencesIn, World worldIn) {
        if (worldIn.isRemote) {
            long tick = EventHandler.clientPlayerTicks;
            for (DrugPresence presenceIn : presencesIn) {
                for (int i = 0; i < presenceIn.delay; i++) {
                    EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, presenceIn.amount / presenceIn.delay, tick + presenceIn.delay + i));
                }
                EventHandler.pendingTasks.add(new DrugPresenceTask(presenceIn.substance, tick + (2L * presenceIn.delay) +1L));
            }
            System.out.println("Tasks added");
        }
    }
}