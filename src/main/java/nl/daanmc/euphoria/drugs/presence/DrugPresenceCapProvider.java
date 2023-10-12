package nl.daanmc.euphoria.drugs.presence;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DrugPresenceCapProvider implements ICapabilitySerializable<NBTBase> {
    public EntityPlayer player;
    public DrugPresenceCapProvider(EntityPlayer player) {
        this.player = player;
    }

    @CapabilityInject(IDrugPresenceCap.class)
    public static final Capability<IDrugPresenceCap> DRUG_PRESENCE_CAP = null;
    private IDrugPresenceCap instance = DRUG_PRESENCE_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == DRUG_PRESENCE_CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == DRUG_PRESENCE_CAP ? DRUG_PRESENCE_CAP.<T> cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return DRUG_PRESENCE_CAP.getStorage().writeNBT(DRUG_PRESENCE_CAP, this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        DRUG_PRESENCE_CAP.getStorage().readNBT(DRUG_PRESENCE_CAP, this.instance, null, nbt);
    }
}
