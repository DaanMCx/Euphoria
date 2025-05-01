package nl.daanmc.euphoria.util;

import net.minecraft.client.Minecraft;
import nl.daanmc.euphoria.util.capabilities.PlayerDrugsCap;
import nl.daanmc.euphoria.util.capabilities.IPlayerDrugsCap;
import nl.daanmc.euphoria.util.tasks.TaskDrugBreakdown;
import nl.daanmc.euphoria.util.tasks.TaskDrugPresence;

public class DrugPresence {
    public Drug drug;
    public float amount;
    public int delay;
    public int comeUp;

    /**
     * Package of properties that describes how a given Drug's presence level should be increased after using a certain drug. It is automatically converted into the required drug tasks upon activation.
     * @param drug the Drug that should become active
     * @param amount the total amount of drug that should be added
     * @param delay the number of ticks between taking the drug and the first effects coming on; e.g. you don't start to feel the effects of spacecake kick in right after eating it, but when smoking a joint you do.
     * @param comeUp the number of ticks it takes for the full drug amount to be added, after the delay
     * @implNote Example: a {@code new DrugPresence(Drugs.THC, 20F, 500, 2000)} will cause the following to happen upon activation:
     * t0 until t+500: delay time;
     * t+500 until t+2500: drug level linearly rises by 20, so (20/2000=)0.01F each tick;
     * t+2500 onwards: drug level gradually decreases following S-curve with a speed defined by {@code drug.breakdownTime}
     */
    public DrugPresence(Drug drug, float amount, int delay, int comeUp) {
        this.drug = drug;
        this.amount=amount;
        this.delay =delay;
        this.comeUp =comeUp;
    }

    void activate(long aTick) {
        IPlayerDrugsCap PDCap = Minecraft.getMinecraft().player.getCapability(PlayerDrugsCap.Provider.CAP,null);
        long tick = PDCap.getClientTick();
        //todo remove
        System.out.println("IDrug activating drug: "+ drug.getRegistryName()+"; a: "+amount+"; i: "+ delay +"; d: "+ comeUp);
        //
        if (tick < aTick+ delay + comeUp) {
            for (long i = Math.max(aTick+ delay -tick, 0); i < aTick+ delay + comeUp -tick; i++) {
                PDCap.addClientTask(new TaskDrugPresence(drug, amount / comeUp), tick + i + 1);
            }
            PDCap.addClientTask(new TaskDrugBreakdown(drug), aTick + delay + comeUp +1);
            PDCap.getActivePresences().put(this, aTick);
        }
    }
}