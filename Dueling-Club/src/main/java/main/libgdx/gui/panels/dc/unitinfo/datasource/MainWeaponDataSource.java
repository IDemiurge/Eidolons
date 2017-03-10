package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface MainWeaponDataSource {
    ValueContainer getMainWeapon();

    List<ValueContainer> getMainWeaponDetailInfo();

    ValueContainer getNaturalMainWeapon();

    List<ValueContainer> getNaturalMainWeaponDetailInfo();
}
