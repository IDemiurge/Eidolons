package main.libgdx.gui.panels.dc.unitinfo.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

public interface OffWeaponDataSource {
    ValueContainer getOffWeapon();

    List<ValueContainer> getOffWeaponDetailInfo();

    ValueContainer getNaturalOffWeapon();

    List<ValueContainer> getNaturalOffWeaponDetailInfo();
}
