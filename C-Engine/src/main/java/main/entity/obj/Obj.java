package main.entity.obj;

import main.ability.AbilityObj;
import main.ability.PassiveAbilityObj;
import main.content.CONTENT_CONSTS.STATUS;
import main.content.ContentManager;
import main.content.OBJ_TYPES;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.data.ability.construct.AbilityConstructor;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.Game;
import main.game.battlefield.Coordinates;
import main.game.player.Player;
import main.system.auxiliary.LogMaster;
import main.system.auxiliary.StringMaster;
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
    private boolean added;
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

    public String getNameAndCoordinate() {
        return getName() + StringMaster.wrapInParenthesis(getCoordinates().toString());
    }

    public void init() {
        add();

    }

    @Override
    public OBJ_TYPES getOBJ_TYPE_ENUM() {
        if (super.getOBJ_TYPE_ENUM() instanceof OBJ_TYPES) {
            return (OBJ_TYPES) super.getOBJ_TYPE_ENUM();
        }
        return null;

    }

    @Override
    public void construct() {
        if (!added) {
            add();
        }
        super.construct();
    }

    protected void add() {
        if (added || !isMicroGameObj()) {
            return;
        }
        getGame().getState().addObject(get());
        added = true;
        // if (!game.isOffline())
        // construct();
        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        //
        // if (get() == null)
        // WaitMaster.WAIT(100);
        // if (get() != null) {
        // getGame().getState().addObject(get());
        // added = true;
        // if (!game.isOffline())
        // construct();
        // }
        // }
        //
        // }, "add obj " + toString()).start();

    }

    protected boolean isMicroGameObj() {
        return true;
    }

    private Obj get() {
        return this;
    }

    protected void activatePassives() {
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
            addStatus(STATUS.DEAD.toString());
        } else {
            removeStatus(STATUS.DEAD.toString());
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
            setBuffs(new DequeImpl<BuffObj>());
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
        abil.activate(ref);
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
            passiveAbils = new LinkedList<String>();
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
            main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG_1, buffName
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
        main.system.auxiliary.LogMaster.log(LogMaster.CORE_DEBUG_1, buffName
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
