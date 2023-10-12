package nl.daanmc.euphoria.drugs.presence;

import nl.daanmc.euphoria.drugs.DrugSubstance;

public class DrugPresence {
    public DrugSubstance substance;
    public float amount;
    public int delay;

    public DrugPresence(DrugSubstance substance, float amount, int delay) {
        this.substance=substance;
        this.amount=amount;
        this.delay=delay;
    }
}
