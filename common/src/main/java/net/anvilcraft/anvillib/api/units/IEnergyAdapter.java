package net.anvilcraft.anvillib.api.units;

import net.anvilcraft.anvillib.api.types.IDirection;

public interface IEnergyAdapter {
    
    IEnergyUnit getDefaultUnit();

    Class<?>[] getRelatedInterfaces();

    boolean canHandle(Class<?> type);

    boolean canHandle(Object obj);

    int CAN_INPUT = 1;
    int CAN_OUTPUT = 2;

    boolean canConnect(Object obj, IDirection from, Object source, int eneryFlowFlags);

    double receiveEnergy(Object obj, IEnergyUnit unit, IDirection from, double receive, boolean doReceive);

	double extractEnergy(Object obj, IEnergyUnit unit, IDirection from, double extract, boolean doExtract);

    boolean setEnergy(Object obj, IEnergyUnit unit, IDirection from, double energy);

	double getEnergy(Object obj, IEnergyUnit unit, IDirection from);

	double getEnergyCapacity(Object obj, IEnergyUnit unit, IDirection from);

    <T> T createWrapper(Object source, Class<T> iface, IEnergyAdapter sourceAdapter);

}
