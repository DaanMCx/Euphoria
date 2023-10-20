package nl.daanmc.euphoria.drugs;

import nl.daanmc.euphoria.drugs.presence.DrugPresence;

public interface IDrug {
    final DrugPresence[] drugPresences = null;
    void attachPresences(DrugPresence[] presencesIn);
}