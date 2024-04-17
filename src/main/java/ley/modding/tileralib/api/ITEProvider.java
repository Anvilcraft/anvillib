package ley.modding.tileralib.api;

import net.minecraft.tileentity.TileEntity;

public interface ITEProvider {

    Class<? extends TileEntity> getTEClass();

}
