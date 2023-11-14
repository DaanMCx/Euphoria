package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.tasks.DrugBreakdownTask;
import nl.daanmc.euphoria.util.tasks.DrugPresenceTask;

public class DrugPresence {
    public DrugSubstance substance;
    public float amount;
    public int incubation;
    public int delay;

    /**
     * Package of properties that describes how a given DrugSubstance's presence level should be increased after using a certain drug. It is automatically converted into the required drug tasks upon activation.
     * @param substance the DrugSubstance that should become active
     * @param amount the total amount of substance that should be added
     * @param incubation the number of ticks between taking the drug and the first effects coming on; e.g. you don't start to feel the effects of spacecake kick in right after eating it, but when smoking a joint you do.
     * @param delay the number of ticks it takes for the full substance amount to be added, after the incubation
     * @implNote Example: a {@code new DrugPresence(DrugSubstances.THC, 20F, 500, 2000)} will cause the following to happen upon activation:
     * t0 until t+500: incubation time;
     * t+500 until t+2500: substance level linearly rises by 20, so (20/2000=)0.01F each tick;
     * t+2500 onwards: substance level gradually decreases following S-curve with a speed defined by {@code substance.breakdownTime}
     */
    public DrugPresence(DrugSubstance substance, float amount, int incubation, int delay) {
        this.substance=substance;
        this.amount=amount;
        this.incubation=incubation;
        this.delay=delay;
    }

    void activate(long aTick) {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        long tick = drugCap.getClientTick();
        System.out.println("IDrug activating s: "+substance.getRegistryName()+"; a: "+amount+"; i: "+incubation+"; d: "+delay);
        if (tick < aTick+incubation+delay) {
            for (long i = Math.max(aTick+incubation-tick, 0); i < aTick+incubation+delay-tick; i++) {
                drugCap.addClientTask(new DrugPresenceTask(substance, amount / delay), tick + i + 1);
            }
            drugCap.addClientTask(new DrugBreakdownTask(substance), aTick + incubation + delay +1);
            drugCap.getActivePresences().put(this, aTick);
            //TODO remove
            System.out.println(substance.getRegistryName()+" tasks added +breakdown "+(aTick + incubation + delay +1));
        }
    }
}