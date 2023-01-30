package tauri.dev.jsg.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import tauri.dev.jsg.power.zpm.IEnergyStorageZPM;
import tauri.dev.jsg.power.zpm.ZPMEnergyStorage;

public class CapabilityEnergyZPM {
    @CapabilityInject(IEnergyStorageZPM.class)
    public static Capability<IEnergyStorageZPM> ENERGY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IEnergyStorageZPM.class, new Capability.IStorage<IEnergyStorageZPM>() {
                    @Override
                    public NBTBase writeNBT(Capability<IEnergyStorageZPM> capability, IEnergyStorageZPM instance, EnumFacing side) {
                        return new NBTTagLong(instance.getEnergyStored());
                    }

                    @Override
                    public void readNBT(Capability<IEnergyStorageZPM> capability, IEnergyStorageZPM instance, EnumFacing side, NBTBase nbt) {
                        if (!(instance instanceof ZPMEnergyStorage))
                            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                        ((ZPMEnergyStorage) instance).setEnergyStored(((NBTTagLong) nbt).getLong());
                    }
                },
                () -> new ZPMEnergyStorage(1000, 1000));
    }
}
