package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleMaster extends DungeonHandler {

    Module base;
    Module current;
    Set<Module> modules;
    private LinkedHashSet<Coordinates> full;

    public ModuleMaster(DungeonMaster master) {
        super(master);
    }

    public Set<Module> getModules() {
        return modules;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
        if (!modules.isEmpty()) {
            for (Module module : modules) {
                if (module.isStartModule()) {
                    base = module;
                    break;
                }
            }
            current = base;
        }
    }

    public Module getModule(Coordinates c) {
        if (modules == null) {
            return null;
        }
        for (Module module : modules) {
            if (module.getCoordinatesSet().contains(c)) {
                return module;
            }
        }
        return null;
    }

    public Module getBase() {
        return base;
    }

    public Module getCurrent() {
        return current;
    }

    public boolean isWithinModule(Obj obj) {
        return obj.getModule() == current;
    }

    public Set<Coordinates> getAllVoidCells() {
        if (full != null) {
            return full;
        }
        LinkedHashSet<Coordinates> set = new LinkedHashSet<>();
        full = new LinkedHashSet<>(getGame().getCoordinates());
        for (Module module : getModules()) {
            set.addAll(module.initCoordinateSet(false));
            set.removeAll(module.getVoidCells());
        }
        full.removeAll(set);
        return full;
    }
}
