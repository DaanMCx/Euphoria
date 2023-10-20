package nl.daanmc.euphoria.drugs.presence;

import nl.daanmc.euphoria.drugs.DrugSubstance;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class DrugPresenceCap implements IDrugPresenceCap {
    private HashMap<DrugSubstance, Float> presenceList = new HashMap<>();
    private HashMap<DrugSubstance, Long> breakdownTickList = new HashMap<>();
    private HashMap<DrugSubstance, Float> breakdownAmountList = new HashMap<>();

    @Override
    public HashMap<DrugSubstance, Float> getDrugPresenceList() {
        return this.presenceList;
    }

    @Override
    public HashMap<DrugSubstance, Long> getBreakdownTickList() {
        return this.breakdownTickList;
    }

    @Override
    public HashMap<DrugSubstance, Float> getBreakdownAmountList() {
        return this.breakdownAmountList;
    }
}