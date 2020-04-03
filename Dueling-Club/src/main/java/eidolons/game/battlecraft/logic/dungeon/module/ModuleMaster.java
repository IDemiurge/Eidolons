package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleMaster extends MetaGameHandler {

    Module base;
    Module current;
    Set<Module> modules;

    public ModuleMaster(MetaGameMaster master) {
        super(master);
    }

    public Set<Module> getModules() {
        if (modules == null) {
            initModules();
            base = getInitialModule();
            current = getInitialModule();
        }
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
        return modules.iterator().next();
    }

    private Module getModuleByPosition() {
        Coordinates c = Eidolons.getMainHero().getCoordinates();
        for (Module module : modules) {
            int x1 = module.getOrigin().x;
            int x2 = module.getOrigin().x + module.getWidth();
            int y1 = module.getOrigin().y;
            int y2 = module.getOrigin().y + module.getHeight();
            if (CoordinatesMaster.isWithinBounds(c, x1, x2, y1, y2))
                return module;

        }
        return null;
    }

    private void initModules() {
        setModules(new LinkedHashSet<>());
        Module module = createDefaultModule();
        modules.add(module);
    }

    private Module createDefaultModule() {
        Module module = new Module(Coordinates.get(0, 0), 25, 25, "Main");
        module.setZones(master.getDungeonMaster().getDungeonLevel().getZones());
        return module;
    }

    public boolean isModuleInitOn() {
        return true;
    }

    public boolean isZoneInitRequired(Node zoneNode) {
        return zoneNode.getNodeName().equalsIgnoreCase(current.getName());
    }

    public Module getModule(Coordinates c) {
        for (Module module : modules) {
            if (CoordinatesMaster.isWithinBounds(c, module.getX(), module.getX2(),
                    module.getY(), module.getY2())) {
                return module;
            }
        }
        return null;
    }


    public void moduleEntered(Module module) {
        AmbienceDataSource.AMBIENCE_TEMPLATE template = module.getVfx();
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, template);

//        AmbientMaster.override(module.height)


    }

    public boolean isWithinModule(Coordinates c) {
        return false;
//        return CoordinatesMaster.isWithinBounds(c, current.getX(), current.getY(), current.getX() +
//                current.getWidth(), current.getY() + current.getHeight());

    }

    public boolean isWithinCameraBounds(float x, float y) {
//module.getCameraMargin();
        return false;
    }

    public Module getBase() {
        if (base == null) {
            base = getInitialModule();
        }
        return base;
    }

    public Module getCurrent() {
        if (current == null) {
            current = getInitialModule();
        }
        return current;
    }

}
