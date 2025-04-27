package nl.daanmc.euphoria.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import nl.daanmc.euphoria.Euphoria;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;

public class TileEntityDryingTable extends TileEntity implements ITickable {

    private ItemStackHandler inputSlot;
    private ItemStackHandler outputSlot;
    private String customName;

    private int dryingProgress, totalDried;
    private float sunLevel;

    public static HashMap<Item, Item> DRYABLE_ITEMS = new HashMap<>();

    public TileEntityDryingTable() {
        DRYABLE_ITEMS.put(Euphoria.Content.Items.CANNABIS_BUD, Euphoria.Content.Items.CANNABIS_BUD_DRIED);
//        this.sunLevel = this.getSunLevel();
        this.inputSlot = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack);
            }
        };
        this.outputSlot = new ItemStackHandler(1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack);
            }
        };
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    public String getName() {
        return this.hasCustomName() ? this.customName : "container.drying_table";
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            markDirty();
            if(world != null && world.getBlockState(pos).getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
                return (T) new CombinedInvWrapper(inputSlot, outputSlot);
            }
            if(facing == null) {
                return (T) new CombinedInvWrapper(inputSlot, outputSlot);
            }
            if(Arrays.asList(EnumFacing.HORIZONTALS).contains(facing)) {
                return (T) inputSlot;
            }
            if(facing == EnumFacing.DOWN) {
                return (T) outputSlot;
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(inputSlot == null) {
            inputSlot = new ItemStackHandler();
        }
        if(outputSlot == null) {
            outputSlot = new ItemStackHandler();
        }
        if(compound.hasKey("inputSlot")) {
            inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("inputSlot"));
        }
        if(compound.hasKey("outputSlot")) {
            outputSlot.deserializeNBT((NBTTagCompound) compound.getTag("outputSlot"));
        }
        this.dryingProgress = compound.getInteger("progress");
        this.totalDried = compound.getInteger("totalDried");
        if (compound.hasKey("customName", 8)) {
            this.customName = compound.getString("customName");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("progress", this.dryingProgress);
        compound.setInteger("totalDried", this.totalDried);
        compound.setTag("inputSlot", this.inputSlot.serializeNBT());
        compound.setTag("outputSlot", this.outputSlot.serializeNBT());
        if (this.hasCustomName()) {
            compound.setString("customName", this.customName);
        }
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return true;
    }

    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    private boolean canRainHere() {
        return world.getPrecipitationHeight(pos).getY() <= pos.getY();
    }

    public float getSunLevel() { //Float value 0.0 - 1.0
        if (this.world.canBlockSeeSky(this.pos)) {
            if (world.isRaining()) {
                return canRainHere() ? 0.0F : 0.4F;
            } else return (float) Math.max(0.5F, (Math.sin((Math.PI * this.world.getWorldTime()) / 12000) + 1) / 2);
        } else return 0.5F;
    }

    public int getDryingProgress() {
        return dryingProgress;
    }

    public void setDryingProgress(int dryingProgress) {
        this.dryingProgress = dryingProgress;
        markDirty();
    }

    public int getTotalDried() {
        return totalDried;
    }

    public void setTotalDried(int totalDried) {
        this.totalDried = totalDried;
        markDirty();
    }

    public int getCurrentDryingTime() {
        Item currentItem = this.inputSlot.getStackInSlot(0).getItem();
        //TODO: get item drying time from recipe
        return 200;
    }

    @Override
    public void update() {
        //todo
    }
}