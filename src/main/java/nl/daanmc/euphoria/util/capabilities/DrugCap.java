package nl.daanmc.euphoria.util.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.common.Mod;
import nl.daanmc.euphoria.Elements;
import nl.daanmc.euphoria.util.DrugSubstance;
import nl.daanmc.euphoria.util.DrugPresence;
import nl.daanmc.euphoria.util.tasks.ITask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber
public class DrugCap implements IDrugCap {
    private long clientTick = 0L;
    private final ConcurrentHashMap<Long, ArrayList<ITask>> clientTasks = new ConcurrentHashMap<>();
    private final HashMap<DrugSubstance, Float> drugList = new HashMap<>();
    private final HashMap<DrugSubstance, Long> breakdownTickList = new HashMap<>();
    private final HashMap<DrugSubstance, Float> breakdownAmountList = new HashMap<>();
    private final HashMap<DrugPresence, Long> activePresenceList = new HashMap<>();

    @Override
    public long getClientTick() {
        return clientTick;
    }

    @Override
    public void doClientTick() {
        clientTick++;
    }

    @Override
    public void setClientTick(long ticks) {
        clientTick = ticks;
    }

    @Override
    public void addClientTask(ITask task, long tick) {
        long realTick = Math.max(tick, clientTick+1);
        if (clientTasks.containsKey(realTick)) {
            clientTasks.get(realTick).add(task);
        } else {
            clientTasks.put(realTick, new ArrayList<>(Collections.singletonList(task)));
        }
    }

    @Override
    public void executeClientTasks() {
        if (clientTasks.containsKey(clientTick)) {
            clientTasks.get(clientTick).forEach(ITask::execute);
            clientTasks.remove(clientTick);
        }
    }

    @Override
    public ConcurrentHashMap<Long, ArrayList<ITask>> getClientTasks() {
        return clientTasks;
    }

    @Override
    public HashMap<DrugSubstance, Float> getDrugs() {
        return drugList;
    }

    @Override
    public HashMap<DrugSubstance, Long> getBreakdownTicks() {
        return breakdownTickList;
    }

    @Override
    public HashMap<DrugSubstance, Float> getBreakdownAmounts() {
        return breakdownAmountList;
    }

    @Override
    public HashMap<DrugPresence, Long> getActivePresences() {
        return activePresenceList;
    }

    //Capability provider
    public static class Provider implements ICapabilitySerializable<NBTBase> {
        public EntityPlayer player;
        public Provider(EntityPlayer player) {
            this.player = player;
        }

        @CapabilityInject(IDrugCap.class)
        public static final Capability<IDrugCap> CAP = null;
        private IDrugCap instance = CAP.getDefaultInstance();

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
    public static class Storage implements Capability.IStorage<IDrugCap> {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IDrugCap> capability, IDrugCap instance, EnumFacing side) {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("dpcap:ct", instance.getClientTick());
            AtomicInteger count = new AtomicInteger();
            instance.getActivePresences().forEach((presence, tick) -> {
                tag.setString("dpcap:ap:"+count.incrementAndGet()+":s", presence.substance.getRegistryName().toString());
                tag.setFloat("dpcap:ap:"+count.get()+":a", presence.amount);
                tag.setInteger("dpcap:ap:"+count.get()+":i", presence.incubation);
                tag.setInteger("dpcap:ap:"+count.get()+":d", presence.delay);
                tag.setLong("dpcap:ap:"+count.get()+":t", tick);
            });
            tag.setInteger("dpcap:ap", count.get());
            Elements.SUBSTANCES.forEach(drugSubstance -> {
                tag.setFloat("dpcap:dp:"+drugSubstance.getRegistryName().toString(), instance.getDrugs().getOrDefault(drugSubstance, 0F));
                tag.setFloat("dpcap:ba:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownAmounts().getOrDefault(drugSubstance, 0F));
                tag.setFloat("dpcap:bt:"+drugSubstance.getRegistryName().toString(), instance.getBreakdownTicks().getOrDefault(drugSubstance, 0L));
            });
            return tag;
        }

        @Override
        public void readNBT(Capability<IDrugCap> capability, IDrugCap instance, EnumFacing side, NBTBase nbt) {
            final NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setClientTick(tag.getLong("dpcap:ct"));
            if (tag.hasKey("dpcap:ap")) {
                for (int i = 1; i <= tag.getInteger("dpcap:ap"); i++) {
                    DrugSubstance substance = DrugSubstance.REGISTRY.get(new ResourceLocation(tag.getString("dpcap:ap:"+i+":s")));
                    float amount = tag.getFloat("dpcap:ap:"+i+":a");
                    int incubation = tag.getInteger("dpcap:ap:"+i+":i");
                    int delay = tag.getInteger("dpcap:ap:"+i+":d");
                    long tick = tag.getLong("dpcap:ap:"+i+":t");
                    instance.getActivePresences().put(new DrugPresence(substance, amount, incubation, delay), tick);
                }
            }
            Elements.SUBSTANCES.forEach(drugSubstance -> {
                instance.getDrugs().put(drugSubstance, tag.getFloat("dpcap:dp:"+drugSubstance.getRegistryName().toString()));
                instance.getBreakdownAmounts().put(drugSubstance, tag.getFloat("dpcap:ba:"+drugSubstance.getRegistryName().toString()));
                instance.getBreakdownTicks().put(drugSubstance, tag.getLong("dpcap:bt:"+drugSubstance.getRegistryName().toString()));
            });
        }
    }
}