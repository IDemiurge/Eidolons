package eidolons.libgdx.gui.panels.dc.unitinfo.datasource;

import eidolons.libgdx.gui.generic.ValueContainer;

import java.util.List;

public interface MainWeaponDataSource<T extends ValueContainer> {
    T getMainWeapon();

    List<T> getMainWeaponDetailInfo();

    T getNaturalMainWeapon();

    List<T> getNaturalMainWeaponDetailInfo();
}
