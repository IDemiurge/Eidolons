package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.Eidolons;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import org.w3c.dom.Node;

import java.util.Set;

public class ModuleMaster extends DungeonHandler  {

    Module base;
    Module current;
    Set<Module> modules;

    public ModuleMaster(DungeonMaster master) {
        super(master);
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
        if (!modules.isEmpty()) {
            base = getInitialModule();
            current = getInitialModule();
        }
    }

    private Module getInitialModule() {
        return getModules().iterator().next();
    }

    private Module getModuleByPosition() {
        Coordinates c = Eidolons.getMainHero().getCoordinates();
        for (Module module : modules) {
            if (module.getCoordinatesSet().contains(c)) {
                return module;
            }
        }
        return null;
    }

    public boolean isModuleInitOn() {
        return true;
    }

    public boolean isZoneInitRequired(Node zoneNode) {
        return zoneNode.getNodeName().equalsIgnoreCase(current.getName());
    }

    public Module getModule(Coordinates c) {
        for (Module module : modules) {
            if (module.getCoordinatesSet().contains(c)) {
                return module;
            }
//            if (CoordinatesMaster.isWithinBounds(c, module.getX(), module.getX2(),
//                    module.getY(), module.getY2())) {
//                return module;
//            }
        }
        return null;
    }

    public boolean isWithinModule(Coordinates c) {
        return CoordinatesMaster.isWithinBounds(c, current.getX(), current.getY(), current.getX() +
                current.getWidth(), current.getY() + current.getHeight());

    }

    public Module getBase() {
        if (base == null) {
            base = getInitialModule();
        }
        return base;
    }

    public Module getCurrent() {
        return current;
    }

    public boolean isWithinModule(Obj obj) {
        return obj.getModule() == current;
    }
}
