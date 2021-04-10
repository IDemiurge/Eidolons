package eidolons.game.battlecraft.logic.dungeon.location.layer;

import eidolons.system.libgdx.GdxStatic;
import eidolons.system.libgdx.wrapper.VectorGdx;
import eidolons.game.battlecraft.logic.dungeon.location.LocationBuilder;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import main.content.enums.GenericEnums;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;
import java.util.Map;

public class LayerInitializer extends DungeonHandler {

    public static final String VFX_NODE = "VFX";
    public static final String ID_NODE = "OBJS";
    public static final String SCRIPT_NODE = "SCRIPTS";

    Map<Coordinates, List<ObjType>> activeMap;
    Map<Coordinates, List<ObjType>> cacheMap;

    public LayerInitializer(DungeonMaster master) {
        super(master);

    }

    @Override
    public LocationBuilder getBuilder() {
        return (LocationBuilder) super.getBuilder();
    }

    public void toggleLayer(Layer layer, boolean on){
        for (Integer id : layer.getIds()) {
            //remove them?
        }
        for (Coordinates coordinates : layer.getScripts().keySet()) {
            if (on) {
            getMetaMaster().getMissionMaster().getScriptManager().parseScripts(layer.getScripts().get(coordinates));
                            }
        }
        for (Coordinates coordinates : layer.getVfxMap().keySet()) {

            for (GenericEnums.VFX vfx : layer.getVfxMap().get(coordinates)) {
                VectorGdx vector = GdxStatic.getCenteredPos(coordinates);

                if (on) {
                    GuiEventManager.triggerWithParams(GuiEventType.ADD_AMBI_VFX, vfx, vector);
                } else {
//                    GuiEventManager.triggerWithParams(GuiEventType.REMOVE_AMBI_VFX, vfx, vector);
                }
            }
        }
    }
}
