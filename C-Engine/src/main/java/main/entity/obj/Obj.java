package main.entity.obj;

import main.ability.AbilityObj;
import main.ability.PassiveAbilityObj;
import main.content.ContentValsManager;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.parameters.ParamMap;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PropMap;
import main.data.ability.construct.AbilityConstructor;
import main.entity.DataModel;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.handlers.EntityMaster;
import main.entity.handlers.ObjMaster;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;

import java.util.ArrayList;
import java.util.List;

public class Obj extends Entity {
    protected DequeImpl<BuffObj> buffs;
    protected List<String> passiveAbils; //To-Cleanup
    protected Boolean passable;
    protected boolean annihilated;
    protected boolean customType;

    public Obj(ObjType type, Player owner, Game game, Ref ref) {
        super(type, owner, game, ref);
    }

    protected void cloneType() {
        type = new ObjType(type);
        game.initType(type);
        customType = true;
    }

    @Override
    protected EntityMaster initMaster() {
        return new ObjMaster(this);
    }

    public String getNameAndCoordinate() {
        return getName() + StringMaster.wrapInParenthesis(getCoordinates().toString());
    }

    public void init() {
        if (getInitializer() == null) {
            addToState();
        } else {
            getInitializer().init();
        }

    }

    @Override
    public DC_TYPE getOBJ_TYPE_ENUM() {
        if (super.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
            return (DC_TYPE) super.getOBJ_TYPE_ENUM();
        }
        return null;

    }

    public boolean isOutsideCombat() {
        return false;
    }

    @Override
    public void construct() {
        if (!added) {
            addToState();
        }
        super.construct();
    }


    public void activatePassives() {
        if (passives == null) {
            return;
        }
        for (Active abil : passives) {
            try {
                if (abil != null) {
                    abil.setRef(getRef());
                    abil.activate();
                }
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        }
    }

    public void applyType(ObjType type) {
        setType(new ObjType(type));
        cloneMaps(type);
        setOBJ_TYPE_ENUM(type.getOBJ_TYPE_ENUM());
        setName(type.getName());
        resetCurrentValues();
        reset();
        resetCurrentValues();

    }

    @Override
    public void cloneMaps(DataModel type) {
        cloneMaps(type.getPropMap(), type.getParamMap());
    }

    public void cloneMaps(PropMap propMap, ParamMap paramMap) {
        this.setPropMap(clonePropMap(propMap.getMap()));

        this.setParamMap(cloneParamMap(paramMap.getMap()));

        setDirty(true);
    }

    public void setDead(boolean dead) {
        this.dead = dead;
        if (dead) {
            addStatus(UnitEnums.STATUS.DEAD.toString());
        } else {
            removeStatus(UnitEnums.STATUS.DEAD.toString());
        }
    }

    public void removeStatus(String value) {
        removeProperty(G_PROPS.STATUS, value);

    }

    protected void addDynamicValues() {
    }

    @Override
    public boolean setParam(PARAMETER param, String value, boolean quiety) {
        boolean result = super.setParam(param, value, quiety);
        if (GuiEventManager.isBarParam(param.getName())) quiety = false;
        if (!quiety && game.isStarted()) {
            fireParamEvent(param, value, CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED);

            if (param.isDynamic()) {
                PARAMETER base_param = ContentValsManager.getBaseParameterFromCurrent(param);
                if (base_param != null && base_param != param) {
                    resetPercentage(base_param);

                }
            }

        }
        return result;
    }


    public List<Attachment> getAttachments() {
        return game.getState().getAttachmentsMap().get(this);
    }

    public void addBuff(BuffObj buff) {
        if (getBuffs() == null) {
            setBuffs(new DequeImpl<>());
        }
        this.getBuffs().add(buff);

    }

    public DequeImpl<BuffObj> getBuffs() {
        if (buffs == null) {
            buffs = new DequeImpl<>();
        }
        return buffs;
    }

    public void setBuffs(DequeImpl<BuffObj> buffs) {
        this.buffs = buffs;
    }

    public void removePassive(PassiveAbilityObj passive) {
        passive.kill();
        removeProperty(G_PROPS.PASSIVES, passive.getName());
        removeProperty(G_PROPS.STANDARD_PASSIVES, passive.getName());
        this.getPassiveAbils().remove(passive.getName());
        // add continuous PASSIVES' modifying effect?!
    }

    public void addPassive(String abilName) {
        if (getPassiveAbils().contains(abilName)) {
            return;
        }
        // getPassives().add(e);
        ActiveObj abil = AbilityConstructor.newAbility(abilName, this, true);
        this.getPassiveAbils().add(abilName);
        this.passives.add((AbilityObj) abil);
        addProperty(G_PROPS.PASSIVES, abilName);
        addProperty(G_PROPS.STANDARD_PASSIVES, abilName);
        abil.activatedOn(ref);
        game.getManager().addAttachment((PassiveAbilityObj) abil, this);
        // add continuous PASSIVES' modifying effect?!
    }

    public Boolean isPassable() {
        if (passable == null) {
            passable = getIntParam("GIRTH") <= 0;
        }
        return passable;
    }

    public List<String> getPassiveAbils() {
        if (passiveAbils == null) {
            passiveAbils = new ArrayList<>();
        }
        return passiveAbils;
    }

    public void setPassiveAbils(List<String> passiveAbils) {

        this.passiveAbils = passiveAbils;
    }

    public void resetPercentages() {
    }

    public void afterEffects() {
    }

    public BuffObj getBuff(String buffName) {
        return getBuff(buffName, true);
    }

    public BuffObj getBuff(String buffName, boolean strict) {
        if (buffs == null || buffName == null) {
            LogMaster.log(LogMaster.CORE_DEBUG_1, buffName
                    + " buff was searched");
            return null;
        }

        for (BuffObj buff : buffs) {
            if (StringMaster.compareByChar(buffName, buff.getName(), strict)) {
                return buff;
            }
        }
        if (!strict) {
            for (BuffObj buff : buffs) {
                if (StringMaster.contains(buff.getName(), buffName)) {
                    return buff;
                }
            }
        }
        LogMaster.log(LogMaster.CORE_DEBUG_1, buffName
                + " buff not found for " + buffs);

        return null;
    }

    public boolean hasBuff(String buffName) {
        return getBuff(buffName) != null;
    }

    public void removeBuff(String buffName) {
        BuffObj buff = getBuff(buffName);
        if (buff != null) {
            getGame().getManager().buffRemoved(buff);
        }
    }

    public Coordinates getCoordinates() {
        return null;
    }

    public void setCoordinates(Coordinates coordinates) {
    }

    public int getX() {
        return 0;
    }

    public void setX(int x) {
    }

    public int getY() {
        return 0;
    }

    public void setY(int y) {
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
        setProperty(G_PROPS.ID, id + "");
    }

    @Override
    public void toBase() {
        super.toBase();
        if (!game.isSimulation())
            if (isConstructOnToBase())
                construct();
    }

    protected boolean isConstructOnToBase() {
        return true;
    }

    public void resetObjects() {
        getResetter().resetObjects();
    }

    public boolean isObstructing() {
        return false;
    }

    public boolean isObstructing(Obj source) {
        return true;
    }

    public boolean isTransparent() {
        return isDead();

    }

    public int getZ() {
        return -100; //To-Cleanup
    }

    public boolean isAnnihilated() {
        return annihilated;
    }

    public void setAnnihilated(boolean annihilated) {
        this.annihilated = annihilated;
    }

    public Object getModule() {
        return null;
    }
}
