package nl.daanmc.euphoria.drugs.presence;

import nl.daanmc.euphoria.drugs.DrugSubstance;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class DrugPresenceCap implements IDrugPresenceCap {
    private HashMap<DrugSubstance, Float> presenceList = new HashMap<DrugSubstance, Float>();

    @Override
    public HashMap<DrugSubstance, Float> getPresenceList() {
        return presenceList;
    }
}
