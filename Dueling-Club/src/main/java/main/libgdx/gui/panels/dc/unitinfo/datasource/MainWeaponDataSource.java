package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface MainWeaponDataSource<T extends ValueContainer> {
    T getMainWeapon();

    List<T> getMainWeaponDetailInfo();

    T getNaturalMainWeapon();

    List<T> getNaturalMainWeaponDetailInfo();
}
