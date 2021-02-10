package macro.global;

import macro.MacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.MacroObj;
import eidolons.macro.map.Region;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.data.ListMaster;

import java.util.List;

public class World extends MacroObj {
    private List<Region> regions;

    /*
     * date, what else? some global stuff about campaign... scheduled events,
     * outcomes...
     */
    public World(MacroGame game, ObjType type, MacroRef ref) {
        super(game, type, ref);
    }

    @Override
    public void setRef(Ref ref) {
        this.ref = ref;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public Region getRegion(String name) {
        return new ListMaster<Region>().findType(name, regions);
    }

}
