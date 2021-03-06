package eidolons.game.battlecraft.logic.meta.scenario.dialogue;

import eidolons.content.consts.Images;
import eidolons.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.StringMap;
import main.entity.type.ObjType;

import java.util.Map;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueActorMaster extends MetaGameHandler<ScenarioMeta> {
    static Map<String, DialogueActor> map = new StringMap<>();

    public DialogueActorMaster(MetaGameMaster master) {
        super(master);
    }

    private static DialogueActor createActor(String name) {
//        getMetaGame().getScenario().getProperty(PROPS.ACTORS);
        //lightweight entities?
        ObjType type = DataManager.getType(name, DC_TYPE.ACTORS);
        if (type == null) {
            ObjType unitType = DataManager.getType(name, C_OBJ_TYPE.UNITS_CHARS);
            if (unitType != null) {
                type = new ObjType(name, DC_TYPE.ACTORS);
                type.setImage(unitType.getImagePath());
            } else {
                type = new ObjType("", DC_TYPE.ACTORS);
                type.setImage(Images.DEMIURGE);
            }
        }
        return new DialogueActor(type);
    }

    public static DialogueActor getActor(String name) {
        DialogueActor actor = map.get(name);
        if (actor == null) {
//            if (name.contains(""))
            if (!DataManager.isTypeName(name)) {
                return null;
            }
            actor = createActor(name);
            map.put(name, actor);
        }
        return actor;
    }


}
