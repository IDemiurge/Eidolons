package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.core.Eidolons;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums;
import main.content.enums.entity.BfObjEnums;
import main.content.enums.meta.QuestEnums;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;

/**
 * Created by JustMe on 10/6/2018.
 */
public class QuestPreparer {
    private final QuestEnums.QUEST_TYPE type;
    DungeonQuest quest;

    public QuestPreparer(DungeonQuest dungeonQuest) {
        this.quest = dungeonQuest;
        type = dungeonQuest.getType();
    }

    public String parseVars(boolean inverse, ObjType type, String template,
                             String name,
                             String number) {
        if (type.getProperty(G_PROPS.VARIABLES).isEmpty()) {
            if (inverse) {
                return VariableManager.substitute(template,
                        number,name);
            }
            return VariableManager.substitute(template,
                    name,
                    number);
        }
        ArrayList<Object> vars = new ArrayList<>();

        for (String var : ContainerUtils.openContainer(type.getProperty(G_PROPS.VARIABLES))) {
            if (var.equalsIgnoreCase("name")) {
                vars.add(name);
            } else if (var.equalsIgnoreCase("number")) {
                vars.add(number);
            }
        }
        return VariableManager.substitute(template,vars);
    }

    public void initArg() {
        int powerLevel= Eidolons.getMainHero().getIntParam(PARAMS.POWER) ;
        DungeonEnums.DUNGEON_STYLE style = DungeonEnums.DUNGEON_STYLE.Somber; //quest.getMaster().getStyle();
//        Eidolons.getGame().getDungeonMaster().getDungeonLevel().getMainStyle()
// TODO is it useful?
        switch (type) {
            case BOSS:
               quest. setArg(QuestCreator.getBossType(powerLevel, quest,
                        style ));
                break;
            case HUNT:
                ObjType unitType = QuestCreator.getPreyType(powerLevel, quest,
                        style );
                float coef = getMobPower(powerLevel, unitType);
                quest.  setPowerCoef(coef);
                quest. setArg(unitType);
                break;
            case OBJECTS:
                quest. setPowerCoef(new Float(powerLevel)/40);
                quest.  setArg(
                        QuestCreator.getOverlayObjType(quest));
                break;
            case SECRETS:
                quest. setPowerCoef(new Float(powerLevel)/180);
                quest.  setArg(DataManager.getType("Old Stone Wall", DC_TYPE.BF_OBJ));
                break;
            case COMMON_ITEMS:
                quest. setPowerCoef(new Float(powerLevel)/70);
                quest. setArg(QuestCreator.getItemTypeCommon(powerLevel, quest,
                        style ));
                break;
            case SPECIAL_ITEM:
                quest.  setArg(QuestCreator.getItemTypeSpecial(quest.getObjType(), powerLevel, quest,
                        style ));
                break;
            case ESCAPE:
                break;
        }
    }

    private float getMobPower(int powerLevel, ObjType unitType) {
        return MathMaster.minMax(new Float(powerLevel) / unitType.getIntParam(PARAMS.POWER)
                , 0.25f, 3);
    }


    public String getDescriptor() {
        switch (type) {
            case ESCAPE:
                return null;
        }
        return StringMaster.toStringForm(getArg());
    }

    public String getTitle() {
        return quest.getTitle();
    }

    public DungeonEnums.LOCATION_TYPE getLocationType() {
        return quest.getLocationType();
    }



    public QuestEnums.QUEST_GROUP getGroup() {
        return quest.getGroup();
    }

    public Object getArg() {
        return quest.getArg();
    }
}
