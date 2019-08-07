package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.quest.advanced.Quest;
import eidolons.macro.entity.town.Town;
import main.content.enums.DungeonEnums.DUNGEON_STYLE;
import main.content.DC_TYPE;
import main.content.enums.DungeonEnums.LOCATION_TYPE;
import main.content.enums.entity.BfObjEnums.BF_OBJ_SUB_TYPES_HANGING;
import main.content.enums.meta.QuestEnums.QUEST_GROUP;
import main.content.enums.meta.QuestEnums.QUEST_TIME_LIMIT;
import main.content.enums.meta.QuestEnums.QUEST_TYPE;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.data.ability.construct.VariableManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.math.MathMaster;

import java.util.ArrayList;

/**
 * Created by JustMe on 10/5/2018.
 */
public class DungeonQuest implements Quest {

    private final ObjType objType;
    private final QuestPreparer preparer;
    protected Integer numberRequired = 0;
    protected Integer numberAchieved = 0;
    protected Integer numberPrepared = 0;
    LOCATION_TYPE locationType;
    private String title;
    private String description;
    private String progressText;
    private String progressTextTemplate;
    private Integer timeLeft;
    private QuestReward reward;
    private QUEST_GROUP group;
    private QUEST_TYPE type;
    //    private QUEST_SUBTYPE subtype;
    private Object arg;
    private boolean complete;
    private Coordinates coordinate;
    private String image;
    private DUNGEON_STYLE style;
    private float powerCoef;
    private boolean started;
    private boolean rewardTaken;
    private Town town;

    public DungeonQuest(ObjType type) {
        this.objType=type;
        initValues(type);
        preparer= new QuestPreparer(this);
        preparer.initArg();
        description = parseDescriptionVars(type, description);
        description+="\n" + getReward().toString();
        //create a trigger?
    }

    private String parseDescriptionVars(ObjType type, String description) {
        return preparer.parseVars(false, type, description, preparer.getDescriptor(),
                ""+getNumberRequired());
    }
    public void update() {
        progressText =preparer.parseVars(true, objType, progressTextTemplate,
                preparer.getDescriptor(), getNumberTooltip());
        GuiEventManager.trigger(GuiEventType.QUEST_UPDATE, this);
    }

    private String getNumberTooltip() {
        if (getNumberRequired() == 0) {
            return null;
        }
        return StringMaster.wrapInBraces(
                numberAchieved + " / " +
                        numberRequired);
    }

    private void initValues(ObjType type) {
        this.title = type.getName();
        this.image = type.getImagePath();
        this.group = new EnumMaster<QUEST_GROUP>().retrieveEnumConst(QUEST_GROUP.class,
         type.getProperty(MACRO_PROPS.QUEST_GROUP));
        this.type = new EnumMaster<QUEST_TYPE>().retrieveEnumConst(QUEST_TYPE.class,
         type.getProperty(MACRO_PROPS.QUEST_TYPE));

        reward = new QuestReward(type);

        this.description = type.getDescription();

        this.progressTextTemplate = type.getProperty(G_PROPS.TOOLTIP);
        if (type.checkProperty(MACRO_PROPS.QUEST_TIME_LIMIT)) {
            QUEST_TIME_LIMIT timing = new EnumMaster<QUEST_TIME_LIMIT>().
             retrieveEnumConst(QUEST_TIME_LIMIT.class,
              type.getProperty(MACRO_PROPS.QUEST_TIME_LIMIT));
            this.timeLeft = QuestCreator.getTimeInSeconds(this, timing);
        }
    }


    @Override
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

    public Object getArg() {
        return arg;
    }

    public void setArg(Object arg) {
        if (arg==null){
            this.arg = arg;
        }
        this.arg = arg;
        if (arg instanceof Entity){
            image = ((Entity) arg).getImagePath();
        }
        update();
    }

    public QUEST_TYPE getType() {
        return type;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getProgressText() {
        return progressText;
    }

    public Integer getTimeLeft() {
        return timeLeft;
    }

    public Integer getNumberPrepared() {
        return numberPrepared;
    }

    public void setNumberPrepared(Integer numberPrepared) {
        this.numberPrepared = numberPrepared;
    }

    @Override
    public Integer getNumberRequired() {
        if (numberRequired <= 0) {
            numberRequired = QuestCreator.getNumberRequired(this);
        }
        return numberRequired;
    }

    public void setNumberRequired(Integer numberRequired) {
        this.numberRequired = numberRequired;
    }

    @Override
    public Integer getNumberAchieved() {
        return numberAchieved;
    }

    @Override
    public QuestReward getReward() {
        return reward;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
        setStarted(!complete);
    }

    public Coordinates getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinates coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getPowerCoef() {
        return powerCoef;
    }

    public void setPowerCoef(float powerCoef) {
        this.powerCoef = powerCoef;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public void setStarted(boolean started) {
        this.started = started;
    }

    @Override
    public boolean isRewardTaken() {
        return rewardTaken;
    }

    @Override
    public void setRewardTaken(boolean rewardTaken) {
        this.rewardTaken = rewardTaken;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Town getTown() {
        return town;
    }

    public ObjType getObjType() {
        return objType;
    }
}
