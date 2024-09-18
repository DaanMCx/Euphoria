package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;
import nl.daanmc.euphoria.util.tasks.TaskDrugBreakdown;
import nl.daanmc.euphoria.util.tasks.TaskDrugPresence;

public class DrugPresence {
    public DrugSubstance substance;
    public float amount;
    public int delay;
    public int comeUp;

    /**
     * Package of properties that describes how a given DrugSubstance's presence level should be increased after using a certain drug. It is automatically converted into the required drug tasks upon activation.
     * @param substance the DrugSubstance that should become active
     * @param amount the total amount of substance that should be added
     * @param delay the number of ticks between taking the drug and the first effects coming on; e.g. you don't start to feel the effects of spacecake kick in right after eating it, but when smoking a joint you do.
     * @param comeUp the number of ticks it takes for the full substance amount to be added, after the delay
     * @implNote Example: a {@code new DrugPresence(DrugSubstances.THC, 20F, 500, 2000)} will cause the following to happen upon activation:
     * t0 until t+500: delay time;
     * t+500 until t+2500: substance level linearly rises by 20, so (20/2000=)0.01F each tick;
     * t+2500 onwards: substance level gradually decreases following S-curve with a speed defined by {@code substance.breakdownTime}
     */
    public DrugPresence(DrugSubstance substance, float amount, int delay, int comeUp) {
        this.substance=substance;
        this.amount=amount;
        this.delay =delay;
        this.comeUp =comeUp;
    }

    void activate(long aTick) {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        long tick = drugCap.getClientTick();
        //todo remove
        System.out.println("IDrug activating s: "+substance.getRegistryName()+"; a: "+amount+"; i: "+ delay +"; d: "+ comeUp);
        //
        if (tick < aTick+ delay + comeUp) {
            for (long i = Math.max(aTick+ delay -tick, 0); i < aTick+ delay + comeUp -tick; i++) {
                drugCap.addClientTask(new TaskDrugPresence(substance, amount / comeUp), tick + i + 1);
            }
            drugCap.addClientTask(new TaskDrugBreakdown(substance), aTick + delay + comeUp +1);
            drugCap.getActivePresences().put(this, aTick);
        }
    }
}