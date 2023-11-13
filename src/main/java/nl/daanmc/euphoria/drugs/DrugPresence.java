package nl.daanmc.euphoria.drugs;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import nl.daanmc.euphoria.util.capabilities.DrugCap;
import nl.daanmc.euphoria.util.capabilities.IDrugCap;

public class DrugPresence {
    public DrugSubstance substance;
    public float amount;
    public int delay;

    public DrugPresence(DrugSubstance substance, float amount, int delay) {
        this.substance=substance;
        this.amount=amount;
        this.delay=delay;
    }

    public static void activatePresence(DrugPresence presence, World worldIn) {
        if (worldIn.isRemote) {
            IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
            long tick = drugCap.getClientTick();
            for (int i = 0; i < presence.delay; i++) {
                drugCap.addClientTask(new DrugPresenceTask(presence.substance, presence.amount / presence.delay), tick + Math.floorDiv(presence.delay, 2) + i);
            }
            drugCap.addClientTask(new DrugBreakdownTask(presence.substance), tick + Math.floorDiv(3*presence.delay, 2) +1);
            drugCap.getActivePresences().put(presence, tick);
            //TODO remove
            System.out.println(presence.substance.getRegistryName()+" tasks added +breakdown "+ (tick + Math.floorDiv(3*presence.delay, 2) +1));
        }
    }

    public static void activate(IDrug fromDrug, World worldIn) {
        if (worldIn.isRemote) {
            IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
            DrugPresence[] presences = fromDrug.getDrugPresences();
            long tick = drugCap.getClientTick();
            for (DrugPresence presence : presences) {
                int incubationTime = Math.round(presence.delay * IDrug.delayModifiers.get(fromDrug.getConsumptionType()));
                for (int i = 0; i < presence.delay; i++) {
                    drugCap.addClientTask(new DrugPresenceTask(presence.substance, presence.amount / presence.delay), tick + incubationTime + i);
                }
                drugCap.addClientTask(new DrugBreakdownTask(presence.substance), tick + incubationTime + presence.delay + 1);
                drugCap.getActivePresences().put(presence, tick);
                //TODO remove
                System.out.println(presence.substance.getRegistryName()+" tasks added +breakdown "+(tick + incubationTime + presence.delay + 1));
            }
        }
    }

    public void activate(long aTick) {
        IDrugCap drugCap = Minecraft.getMinecraft().player.getCapability(DrugCap.Provider.CAP,null);
        long tick = drugCap.getClientTick();
        if (tick < aTick+Math.floorDiv(3*delay, 2)) {
            for (long i = Math.max(aTick+Math.floorDiv(delay, 2)-tick, 0); i < aTick+Math.floorDiv(3*delay, 2)-tick; i++) {
                drugCap.addClientTask(new DrugPresenceTask(substance, amount / delay), tick + i + 1);
            }
            drugCap.addClientTask(new DrugBreakdownTask(substance), aTick + Math.floorDiv(3*delay, 2) +1);
            drugCap.getActivePresences().put(this, aTick);
            //TODO remove
            System.out.println(substance.getRegistryName()+" tasks added +breakdown "+(aTick + Math.floorDiv(3*delay, 2) +1L));
        }
    }
}