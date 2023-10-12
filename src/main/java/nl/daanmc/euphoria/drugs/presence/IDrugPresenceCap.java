package nl.daanmc.euphoria.drugs.presence;

import nl.daanmc.euphoria.drugs.DrugSubstance;

import java.util.HashMap;

public interface IDrugPresenceCap {
    HashMap<DrugSubstance, Float> getPresenceList();
}
