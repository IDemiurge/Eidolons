package main.game.battlecraft.logic.meta.scenario.dialogue;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.battlecraft.logic.meta.scenario.ScenarioMeta;
import main.game.battlecraft.logic.meta.universal.MetaGameHandler;
import main.game.battlecraft.logic.meta.universal.MetaGameMaster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueActorMaster extends MetaGameHandler<ScenarioMeta> {
    static Map<String, DialogueActor> map = new HashMap<>();

    public DialogueActorMaster(MetaGameMaster master) {
        super(master);
    }

    private static DialogueActor createActor(String name) {
//        getMetaGame().getScenario().getProperty(PROPS.ACTORS);
        //lightweight entities?
        ObjType type = DataManager.getType(name, DC_TYPE.ACTORS);

        return new DialogueActor(type);
    }

    public static DialogueActor getActor(String name) {
        DialogueActor actor = map.get(name);
        if (actor == null) {
            actor = createActor(name);
            map.put(name, actor);
        }
        return actor;
    }

    public List<DialogueActor> getActors(String actorNames) {
        return null;
    }
}
