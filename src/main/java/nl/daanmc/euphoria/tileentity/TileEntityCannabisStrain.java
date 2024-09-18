package nl.daanmc.euphoria.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCannabisStrain extends TileEntity {
    public TileEntityCannabisStrain() {}

    private float sativa;
    private float indica;
    private boolean autoflower;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.sativa = compound.getFloat("sativa");
        this.indica = compound.getFloat("indica");
        this.autoflower = compound.getBoolean("autoflower");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat("sativa", this.sativa);
        compound.setFloat("indica", this.indica);
        compound.setBoolean("autoflower", this.autoflower);
        return compound;
    }

    public float getSativa() {
        return this.sativa;
    }

    public void setSativa(float sativa) {
        this.sativa = sativa;
        markDirty();
    }

    public float getIndica() {
        return indica;
    }

    public void setIndica(float indica) {
        this.indica = indica;
        markDirty();
    }

    public boolean isAutoflower() {
        return autoflower;
    }

    public void setAutoflower(boolean autoflower) {
        this.autoflower = autoflower;
        markDirty();
    }
}