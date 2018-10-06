package eidolons.game.module.dungeoncrawl.quest;

import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.meta.QuestEnums.QUEST_GROUP;
import main.content.enums.meta.QuestEnums.QUEST_TIME_LIMIT;
import main.content.enums.meta.QuestEnums.QUEST_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.ability.construct.VariableManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.EnumMaster;

/**
 * Created by JustMe on 10/5/2018.
 */
public class DungeonQuest {

    private String title;
    private String description;
    private String progressText;
    private String progressTextTemplate;

    private  Integer timeLeft;
    protected  Integer numberRequired;
    protected   Integer numberAchieved;

    private QuestReward reward;
    private QUEST_GROUP group;
    private QUEST_TYPE type;
    private Object arg;
//    private QUEST_SUBTYPE subtype;

    LOCATION_TYPE locationType;
    private boolean complete;
    private Coordinates coordinate;

    public DungeonQuest(ObjType type) {
        initValues(type);
        reward = new QuestReward(type, this);

        //create a trigger?
    }

    public void update() {
        progressText = VariableManager.substitute(progressTextTemplate,
         numberAchieved,
         numberRequired,
         timeLeft
        );
        GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, this);
    }


    private void initValues(ObjType type) {
        this.title = type.getName();
        this.description = type.getDescription();
        this. progressTextTemplate = type.getProperty(G_PROPS.TOOLTIP);
        if (type.checkProperty(MACRO_PROPS.QUEST_TIME_LIMIT)){
            QUEST_TIME_LIMIT timing = new EnumMaster<QUEST_TIME_LIMIT>().
             retrieveEnumConst(QUEST_TIME_LIMIT.class,
             type.getProperty(MACRO_PROPS.QUEST_TIME_LIMIT));
            this.timeLeft =QuestCreator.getTimeInSeconds(this, timing);
        }
        this.group = new EnumMaster<QUEST_GROUP>().retrieveEnumConst(QUEST_GROUP.class,
         type.getProperty(MACRO_PROPS.QUEST_GROUP));
        this.type = new EnumMaster<QUEST_TYPE>().retrieveEnumConst(QUEST_TYPE.class,
         type.getProperty(MACRO_PROPS.QUEST_TYPE));
        update();
    }

    public String getTitle() {
        return title;
    }

    public LOCATION_TYPE getLocationType() {
        return locationType;
    }

    public String getProgressTextTemplate() {
        return progressTextTemplate;
    }

    public QUEST_GROUP getGroup() {
        return group;
    }

    public void setArg(Object arg) {
        this.arg = arg;
    }

    public Object getArg() {
        return arg;
    }

    public QUEST_TYPE getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getProgressText() {
        return progressText;
    }

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public Integer getNumberRequired() {
        return numberRequired;
    }

    public Integer getNumberAchieved() {
        return numberAchieved;
    }

    public QuestReward getReward() {
        return reward;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean isComplete() {
        return complete;
    }

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }
}
