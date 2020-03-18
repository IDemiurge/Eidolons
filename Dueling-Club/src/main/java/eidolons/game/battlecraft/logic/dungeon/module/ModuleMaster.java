package eidolons.game.battlecraft.logic.dungeon.module;

import eidolons.game.battlecraft.logic.battlefield.CoordinatesMaster;
import eidolons.game.battlecraft.logic.meta.igg.xml.XmlLevelTools;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.particles.ambi.AmbienceDataSource;
import main.data.ability.construct.VariableManager;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.PathUtils;
import org.junit.Test;
import org.w3c.dom.Node;

import java.util.LinkedHashSet;
import java.util.Set;

public class ModuleMaster extends MetaGameHandler {

    Module current;
    Set<Module> modules;
    MODULE_LEVEL scheme = MODULE_LEVEL.ASHEN_PATH;

    public ModuleMaster(MetaGameMaster master) {
        super(master);
        initModules();
        current = getInitialModule();

    }

    public Set<Module> getModules() {
        return modules;
    }

    private Module getInitialModule() {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(scheme.initialModule)){
                return module;
            }
        }
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
        modules = new LinkedHashSet<>();
        for (String s : scheme.modules) {
            String path = VariableManager.removeVarPart(s);
            String name = PathUtils.getLastPathSegment(path);
            Coordinates c = Coordinates.get(VariableManager.getVars(s));
            int w = 15;
            int h = 15;
            Module module = new Module(c, w, h, name );
            modules.add(module);
        }
    }

    @Test
    public void packModule(MODULE_LEVEL moduleLevel) {
        for (String module : moduleLevel.modules) {
            String name = VariableManager.removeVarPart(module) + ".xml";
            Coordinates c = Coordinates.get(VariableManager.getVars(module));
            XmlLevelTools.insertModule(  moduleLevel.path + ".xml", name, c.x, c.y);

        }

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


    public enum MODULE_LEVEL {
        ASHEN_PATH("sublevels/ashen path modular", false, "sublevels/main(0-0)", "sublevels/maze module(0-0);"),
        ;
        public String initialModule;

        MODULE_LEVEL(String path, boolean wtf, String... modules) {
            this(VariableManager.removeVarPart(PathUtils.getLastPathSegment(modules[0])), path, wtf, modules);
        }
        MODULE_LEVEL(String initialModule, String path, boolean wtf, String... modules) {
            this.path = path;
            this.initialModule = initialModule;
            this.modules = modules;
        }

        String path;
        String[] modules;
    }

    public void moduleEntered(Module module) {
        AmbienceDataSource.AMBIENCE_TEMPLATE template = module.getVfx();
        GuiEventManager.trigger(GuiEventType.UPDATE_AMBIENCE, template);

//        AmbientMaster.override(module.height)


/**
 * >>> Can we return ?
 *
 * camera control
 * visibility
 * custom render
 * vfx?
 *
 */
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
}