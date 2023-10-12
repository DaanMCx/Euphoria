package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import javax.annotation.Nullable;

public class DrugPresenceCapStorage implements Capability.IStorage<IDrugPresenceCap> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IDrugPresenceCap> capability, IDrugPresenceCap instance, EnumFacing side) {
        final NBTTagCompound tagCompound = new NBTTagCompound();
        instance.getPresenceList().forEach((drugSubstance, amount) -> tagCompound.setFloat(drugSubstance.getRegistryName().toString(), amount));
        return tagCompound;
    }

    @Override
    public void readNBT(Capability<IDrugPresenceCap> capability, IDrugPresenceCap instance, EnumFacing side, NBTBase nbt) {
        final NBTTagCompound tagCompound = (NBTTagCompound) nbt;
        instance.getPresenceList().forEach((drugSubstance, amount) -> instance.getPresenceList().put(drugSubstance, tagCompound.getFloat(drugSubstance.getRegistryName().toString())));
    }
}
