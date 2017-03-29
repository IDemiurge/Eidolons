package main.client.cc.gui.lists.dc;

import main.client.cc.gui.lists.HeroListPanel;
import main.client.cc.gui.lists.dc.DC_InventoryManager.OPERATIONS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;

/**
 * Created by JustMe on 3/29/2017.
 */
public interface InventoryManager {
    boolean hasOperations(int n);

    boolean hasOperations();

    Integer getNumberOfOperations();

    void setNumberOfOperations(Integer numberOfOperations);

    boolean addType(ObjType type, HeroListPanel hlp, boolean alt);

    boolean operationDone(OPERATIONS operation, String string);

    boolean operationDone(int n, OPERATIONS operation, String string);

    void removeType(Entity type, HeroListPanel hlp, PROPERTY p);

    Unit getHero();
}
