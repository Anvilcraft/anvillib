package net.anvilcraft.anvillib.ae2;

import appeng.api.AEApi;
import cpw.mods.fml.common.Optional.Method;
import net.anvilcraft.anvillib.ae2.impl.AEEnergyUnit;
import net.anvilcraft.anvillib.ae2.impl.EnergyStorageChannel;
import net.anvilcraft.anvillib.ae2.impl.ItemResearchStorageChannel;
import net.anvilcraft.anvillib.ae2.impl.ManaStorageChannel;
import net.anvilcraft.anvillib.ae2.impl.MatterStorageChannel;
import net.anvilcraft.anvillib.api.ae2.channel.IEnergyStorageChannel;
import net.anvilcraft.anvillib.api.ae2.channel.IItemResearchStorageChannel;
import net.anvilcraft.anvillib.api.ae2.channel.IManaStorageChannel;
import net.anvilcraft.anvillib.api.ae2.channel.IMatterStorageChannel;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;
import net.anvilcraft.anvillib.registries.UnitRegistry;

public class AEIntegration {

    public static IEnergyUnit ENERGY_UNIT;

    @Method(modid = "appliedenergistics2")
    public static void load() {
        AEApi.instance().storage().registerStorageChannel(IEnergyStorageChannel.class, new EnergyStorageChannel());
        AEApi.instance().storage().registerStorageChannel(IMatterStorageChannel.class, new MatterStorageChannel());
        AEApi.instance().storage().registerStorageChannel(IManaStorageChannel.class, new ManaStorageChannel());
        AEApi.instance().storage().registerStorageChannel(IItemResearchStorageChannel.class, new ItemResearchStorageChannel());
        ENERGY_UNIT = new AEEnergyUnit();
        UnitRegistry.INSTANCE.register(ENERGY_UNIT);
    }
    
}
