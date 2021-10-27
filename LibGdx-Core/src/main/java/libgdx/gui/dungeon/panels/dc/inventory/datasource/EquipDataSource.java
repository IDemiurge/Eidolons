package libgdx.gui.dungeon.panels.dc.inventory.datasource;

import libgdx.gui.dungeon.panels.dc.inventory.InvItemActor;

import java.util.List;

public interface EquipDataSource {
    InvItemActor mainWeapon();

    InvItemActor offWeapon();

    InvItemActor armor();

    InvItemActor avatar();

    InvItemActor amulet();

    List<InvItemActor> rings();

    InvItemActor mainWeaponReserve();

    InvItemActor offWeaponReserve();
}
