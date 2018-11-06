package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.game.core.Eidolons;
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
public class DungeonQuest {

    private final ObjType objType;
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
        initArg();
        description = parseDescriptionVars(type, description);
        description+="\n" + getReward().toString();
        //create a trigger?
    }

    private void initArg() {
        int powerLevel= Eidolons.getMainHero().getIntParam(PARAMS.POWER) ;
        main.system.auxiliary.log.LogMaster.log(1,"powerLevel= " +powerLevel);
        style =  Eidolons.getGame().getDungeonMaster().getDungeonLevel().getMainStyle();
        switch (type) {
            case BOSS:
                setArg(QuestCreator.getBossType(powerLevel, this,
                 style ));
                break;
            case HUNT:
                ObjType unitType = QuestCreator.getPreyType(powerLevel, this,
                 style );
                float coef = getMobPower(powerLevel, unitType);
                setPowerCoef(coef);
                setArg(unitType);
                break;
            case OBJECTS:
                setPowerCoef(new Float(powerLevel)/40);
                setArg(DataManager.getType(BF_OBJ_SUB_TYPES_HANGING.ANCIENT_RUNE.getName(), DC_TYPE.BF_OBJ));
                break;
            case SECRETS:
                setPowerCoef(new Float(powerLevel)/180);
                setArg(DataManager.getType("Old Stone Wall", DC_TYPE.BF_OBJ));
                break;
            case COMMON_ITEMS:
                setPowerCoef(new Float(powerLevel)/70);
                setArg(QuestCreator.getItemTypeCommon(powerLevel, this,
                 style ));
                break;
            case SPECIAL_ITEM:
                setArg(QuestCreator.getItemTypeSpecial(objType, powerLevel, this,
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

    public void update() {
        progressText = parseVars(true, objType, progressTextTemplate,
         getDescriptor(), getNumberTooltip());

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

    private String getDescriptor() {
        switch (type) {
            case ESCAPE:
                return null;
        }
        return StringMaster.toStringForm(arg);
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

    private String parseDescriptionVars(ObjType type, String description) {
       return parseVars(false, type, description, getDescriptor(),
         ""+getNumberRequired());
    }

    private String parseVars(boolean inverse, ObjType type, String template,
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
        this.arg = arg;
        if (arg instanceof Entity){
            image = ((Entity) arg).getImagePath();
        }
        update();
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

    public Integer getNumberPrepared() {
        return numberPrepared;
    }

    public void setNumberPrepared(Integer numberPrepared) {
        this.numberPrepared = numberPrepared;
    }

    public Integer getNumberRequired() {
        if (numberRequired <= 0) {
            numberRequired = QuestCreator.getNumberRequired(this);
        }
        return numberRequired;
    }

    public void setNumberRequired(Integer numberRequired) {
        this.numberRequired = numberRequired;
    }

    public Integer getNumberAchieved() {
        return numberAchieved;
    }

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

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isRewardTaken() {
        return rewardTaken;
    }

    public void setRewardTaken(boolean rewardTaken) {
        this.rewardTaken = rewardTaken;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public Town getTown() {
        return town;
    }
}
