package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import nl.daanmc.euphoria.Elements;

import javax.annotation.Nullable;

public class DrugPresenceCapStorage implements Capability.IStorage<IDrugPresenceCap> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IDrugPresenceCap> capability, IDrugPresenceCap instance, EnumFacing side) {
        final NBTTagCompound tagCompound = new NBTTagCompound();
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            tagCompound.setFloat("dpcap:dp:"+drugSubstance.getRegistryName().toString(), instance.getDrugPresenceList().getOrDefault(drugSubstance, 0F));
            tagCompound.setFloat("dpcap:ba:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownAmountList().getOrDefault(drugSubstance, 0F));
            tagCompound.setFloat("dpcap:bt:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownTickList().get(drugSubstance));
        });
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IDrugPresenceCap> capability, IDrugPresenceCap instance, EnumFacing side, NBTBase nbt) {
        final NBTTagCompound tagCompound = (NBTTagCompound) nbt;
        Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
            instance.getDrugPresenceList().put(drugSubstance, tagCompound.getFloat("dpcap:dp:"+drugSubstance.getRegistryName().toString()));
            instance.getBreakdownAmountList().put(drugSubstance, tagCompound.getFloat("dpcap:ba:"+drugSubstance.getRegistryName().toString()));
            instance.getBreakdownTickList().put(drugSubstance, tagCompound.getLong("dpcap:bt:"+drugSubstance.getRegistryName().toString()));
        });
    }
}