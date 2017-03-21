package main.entity.obj;

import main.ability.AbilityObj;
import main.ability.PassiveAbilityObj;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.enums.entity.UnitEnums;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.tools.EntityMaster;
import main.entity.tools.ObjMaster;
import main.entity.type.ObjType;
import main.game.battlefield.Coordinates;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.datatypes.DequeImpl;
import main.system.images.ImageManager.HIGHLIGHT;
import main.system.launch.CoreEngine;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an Entity on the Battlefield
 * Placed in a map in GameState upon creation
 */
public class Obj extends Entity {
    private HIGHLIGHT highlight;
    private DequeImpl<BuffObj> buffs;
    private List<String> passiveAbils;
    private boolean infoSelected;
    private boolean activeSelected;
    private boolean targetHighlighted;
    private Boolean passable;

    public Obj(ObjType type, Player owner, Game game, Ref ref) {
        super(type
         // new ObjType(type) TODO how can I ensure that each object's Type can
         // be modified independently? Perhaps I should clone upon
         // modification...
         , owner, game, ref);
        // init();
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
        }
        else {
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

    @Override
    public void construct() {
        if (!added) {
            addToState();
        }
        super.construct();
    }


    public void activatePassives() {
        if (passives == null) {
            game.getManager().setActivatingPassives(false);
            return;
        }
        // if (!getGame().isSimulation())
        // if (isPassivesReady() || game.getManager().isActivatingPassives()) {
        // return;
        // } not necessary?
        game.getManager().setActivatingPassives(true);
        for (Active abil : passives) {
            try {
                if (abil != null) {
//                    if () TODO currently, passives are just applied each time, no continuous effects...
//                    if (abil instanceof PassiveAbilityObj){
//                        getGame().getManager().addAttachment((PassiveAbilityObj)abil, this);
//                    }
                    abil.setRef(getRef());
                    abil.activate();
                }
            } catch (Exception e) {
                // setPassivesReady(true);
                // if (game.isSimulation())
                e.printStackTrace();
            }
        }
        game.getManager().setActivatingPassives(false);
        // setPassivesReady(true);
        // addDynamicValues();
    }

    public void applyType(ObjType type) {
        setType(type);
        cloneMaps(type);
        setOBJ_TYPE_ENUM(type.getOBJ_TYPE_ENUM());
        setName(type.getName());
        toBase();
        resetObjects();
        afterEffects();
        resetPercentages();
        resetCurrentValues();

    }

    @Override
    public void cloneMaps(Entity type) {
        this.propMap = clonePropMap(type.getPropMap().getMap());
        // so the problem is that it doesn't seem to carry over c_ and perc_
        // values?
        for (PARAMETER p : type.getParamMap().getMap().keySet()) {
            if (!p.isDynamic()) {
                paramMap.remove(p);
            }
        }
        for (PARAMETER p : type.getParamMap().getMap().keySet()) {
            paramMap.put(p, type.getParamMap().getMap().get(p));
        }
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

    // @Override
    // public int getX() {
    // return x;
    // }
    //
    // @Override
    // public int getY() {
    // return y;
    // }
    //

    public boolean isFull(PARAMETER p) {
        return getIntParam(ContentManager.getCurrentParam(p)) >= getIntParam(p);
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

    public void removePassive(String abilName) {
        for (ActiveObj passive : passives) {
            if (passive.getName().equals(abilName)) {
                removePassive((PassiveAbilityObj) passive);
            }

        }

    }

    public void removeAllPassives() {
        for (ActiveObj passive : passives) {
            removePassive((PassiveAbilityObj) passive);
        }
        setProperty(G_PROPS.PASSIVES, "");
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
        // if (checkProperty(G_PROPS.BF_OBJECT_TAGS, "" +
        // BF_OBJECT_TAGS.PASSABLE))
        // return true;
        // return checkBool(STD_BOOLS.PASSABLE);
    }

    public List<String> getPassiveAbils() {
        if (passiveAbils == null) {
            passiveAbils = new LinkedList<>();
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

    public void invokeHovered() {

    }

    public void invokeRightClicked() {
        game.getManager().rightClicked(this);
        if (!isToolTipDisabled()) {
            initToolTip();
        }
        // game.getDialogManager().displayInfo(this);

    }

    public boolean isToolTipDisabled() {

        return game.isSimulation() || CoreEngine.isGraphicTestMode();
        // return false;
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

    public HIGHLIGHT getHighlight() {
        return highlight;
    }

    public void setHighlight(HIGHLIGHT highlight) {
        this.highlight = highlight;
    }

    public boolean isInfoSelected() {
        return infoSelected;
    }

    public void setInfoSelected(boolean b) {
        this.infoSelected = b;
    }

    public boolean isActiveSelected() {
        return activeSelected;
    }

    public void setActiveSelected(boolean b) {
        this.activeSelected = b;
    }

    @Override
    public void toBase() {
        super.toBase();
        construct();
    }

    public void resetObjects() {
        getResetter().resetObjects();
    }

    public boolean isObstructing() {
        return isObstructing(null);
    }

    public boolean isObstructing(Obj source) {
        return true;
    }

    public boolean isTransparent() {
        return isDead();

    }

    public int getZ() {
        return -100;
    }

    public boolean isTargetHighlighted() {
        return targetHighlighted;
    }

    public void setTargetHighlighted(boolean targetHighlighted) {
        this.targetHighlighted = targetHighlighted;
    }

    public void initToolTip() {
        // TODO Auto-generated method stub

    }

}
