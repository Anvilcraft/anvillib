package net.anvilcraft.anvillib.api.units;

import net.minecraftforge.common.util.ForgeDirection;

public interface IEnergyAdapter {
    
    IEnergyUnit getDefaultUnit();

    Class<?>[] getRelatedInterfaces();

    boolean canHandle(Class<?> type);

    boolean canHandle(Object obj);

    int CAN_INPUT = 1;
    int CAN_OUTPUT = 2;

    boolean canConnect(Object obj, ForgeDirection from, Object source, int eneryFlowFlags);

    double receiveEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double receive, boolean doReceive);

	double extractEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double extract, boolean doExtract);

    boolean setEnergy(Object obj, IEnergyUnit unit, ForgeDirection from, double energy);

	double getEnergy(Object obj, IEnergyUnit unit, ForgeDirection from);

	double getEnergyCapacity(Object obj, IEnergyUnit unit, ForgeDirection from);

    <T> T createWrapper(Object source, Class<T> iface, IEnergyAdapter sourceAdapter);

}
