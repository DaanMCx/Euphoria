package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.Mod;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.drugs.DrugSubstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    //Capability provider
    public static class Provider implements ICapabilitySerializable<NBTBase> {
        public EntityPlayer player;
        public Provider(EntityPlayer player) {
            this.player = player;
        }

        @CapabilityInject(IDrugPresenceCap.class)
        public static final Capability<IDrugPresenceCap> CAP = null;
        private IDrugPresenceCap instance = CAP.getDefaultInstance();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CAP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CAP ? CAP.<T> cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT() {
            return CAP.getStorage().writeNBT(CAP, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            CAP.getStorage().readNBT(CAP, this.instance, null, nbt);
        }
    }

    //Capability storage
    public static class Storage implements Capability.IStorage<IDrugPresenceCap> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IDrugPresenceCap> capability, IDrugPresenceCap instance, EnumFacing side) {
            final NBTTagCompound tagCompound = new NBTTagCompound();
            Elements.DRUG_PRESENCE_LIST.forEach(drugSubstance -> {
                tagCompound.setFloat("dpcap:dp:"+drugSubstance.getRegistryName().toString(), instance.getDrugPresenceList().getOrDefault(drugSubstance, 0F));
                tagCompound.setFloat("dpcap:ba:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownAmountList().getOrDefault(drugSubstance, 0F));
                tagCompound.setFloat("dpcap:bt:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownTickList().getOrDefault(drugSubstance, 0L));
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
}