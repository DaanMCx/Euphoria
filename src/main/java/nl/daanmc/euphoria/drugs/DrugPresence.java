package nl.daanmc.euphoria.drugs;

import net.minecraft.world.World;
import nl.daanmc.euphoria.util.EventHandler;

import java.util.HashMap;

public class DrugPresence {
    public static HashMap<DrugPresence, Long> activePresences = new HashMap<>();
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
                EventHandler.clientTasks.add(new DrugPresenceTask(presenceIn.substance, presenceIn.amount / presenceIn.delay, tick + presenceIn.delay + i));
            }
            EventHandler.clientTasks.add(new DrugPresenceTask(presenceIn.substance, tick + (2L * presenceIn.delay) +1));
            activePresences.put(presenceIn, tick);
            System.out.println(presenceIn.substance.getRegistryName()+" tasks added +breakdown "+ (tick + (2 * presenceIn.delay) +1));
        }
    }

    public static void activatePresence(DrugPresence[] presencesIn, World worldIn) {
        if (worldIn.isRemote) {
            long tick = EventHandler.clientPlayerTicks;
            for (DrugPresence presenceIn : presencesIn) {
                for (int i = 0; i < presenceIn.delay; i++) {
                    EventHandler.clientTasks.add(new DrugPresenceTask(presenceIn.substance, presenceIn.amount / presenceIn.delay, tick + presenceIn.delay + i));
                }
                EventHandler.clientTasks.add(new DrugPresenceTask(presenceIn.substance, tick + (2L * presenceIn.delay) +1L));
                activePresences.put(presenceIn, tick);
                System.out.println(presenceIn.substance.getRegistryName()+" tasks added +breakdown "+tick + (2L * presenceIn.delay) +1L);
            }
        }
    }

    public void activate(long tick) {
        for (int i = 0; i < this.delay; i++) {
            EventHandler.clientTasks.add(new DrugPresenceTask(this.substance, this.amount / this.delay, tick + this.delay + i));
        }
        EventHandler.clientTasks.add(new DrugPresenceTask(this.substance, tick + (2L * this.delay) +1));
        activePresences.put(this, tick);
    }
}