package eidolons.libgdx.gui.panels.dc.inventory.datasource;

import eidolons.libgdx.gui.panels.dc.inventory.InvItemActor;

import java.util.List;

public interface EquipDataSource {
    InvItemActor mainWeapon();

    InvItemActor offWeapon();

    InvItemActor armor();

    InvItemActor avatar();

    InvItemActor amulet();

    List<InvItemActor> rings();
}
