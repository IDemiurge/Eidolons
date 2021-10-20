package main.entity;

import main.ability.AbilityObj;
import main.content.ContentValsManager;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.entity.handlers.*;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.EnumMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.text.TextParser;
import main.system.threading.Weaver;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root class of every CoreEngine object Contains property and parameter maps and an array of methods for retrieving and
 * modifying them Has ID by which it can be found from GameState
 * <portrait>
 * <portrait>
 * has OBJ_TYPE that defines how it is treated by things like Targeting (Filter)
 * <portrait>
 * has ObjType that defines its [base] values  (property and parameter)
 * <portrait>
 * toBase() is called whenever we need to update them - first they are set to their base, then modified by various
 * effects
 * <portrait>
 * resetObjects, afterEffects (apply masteries/attributes), preCheck parameter buff rules (focus, stamina, …) :: Dynamic
 * values are not reset to base on toBase() :: Properties – container (; separated), variable(value), :: Parameters –
 * formula syntax: {ref_value}, e.g. {source_base strength}, event_ prefix checked first, replaced ref with event’s ref
 * if necessary :: Buffs – created with a duration or a retainCondition, add ContinuousEffect’s while in game
 */
public abstract class Entity extends DataModel implements OBJ {

    protected List<AbilityObj> passives = new ArrayList<>();
    protected List<ActiveObj> actives = new ArrayList<>();

    protected boolean dead = false;
    protected Player originalOwner;

    protected boolean added;
    protected Map<VALUE, String> valueCache = new HashMap<>(); //to cache valid tooltip values
    protected ImageIcon customIcon;
    protected ImageIcon icon;
    protected EntityMaster master;
    protected Map<PARAMETER, Integer> validParams;


    public Entity() {

    }

    public Entity(ObjType type, Player owner, Game game, Ref ref) {
        if (type == null) {
            RuntimeException e = new RuntimeException("null type!" + ref);
            throw (e);
        } else {
            preInit(game, type, owner, ref);
            init();
        }
    }

    protected void preInit(Game game, ObjType type, Player owner, Ref ref) {
        this.game = game;
        getId(); // new id if null
        this.TYPE_ENUM = type.getOBJ_TYPE_ENUM();
        this.type = (type); // no cloning by default
        type.checkBuild();
        this.owner = owner;
        this.setOriginalOwner(owner);
        setProperty(G_PROPS.NAME, type.getName());
        setOriginalName(type.getName());

        master = initMaster();
        setRef(ref); //create ref branch
    }

    public boolean checkGroupingProperty(String string) {
        return checkSingleProp(getOBJ_TYPE_ENUM().getGroupingKey(), string);
    }

    protected abstract EntityMaster initMaster();

    public void addToState() {
        if (game.isCloningMode()) {
            return;
        }

        if (added || !isMicroGameObj()) {
            return;
        }
        if (this instanceof Obj) {
            getGame().getState().addObject((Obj) this);
            added = true;
        }

    }

    protected boolean isMicroGameObj() {
        return true;
    }

    public EntityMaster getMaster() {
        return master;
    }

    public int getLastValidParamValue(PARAMETER parameter) {
        if (getValidParams().containsKey(parameter))
            return validParams.get(parameter);
        return getIntParam(parameter);
    }

    public Map<PARAMETER, Integer> getValidParams() {
        if (validParams == null) {
            validParams = new HashMap<>();
        }
        return validParams;
    }

    public void toBase() {

        setBeingReset(true);

        if (isResetViaHandler())
            if (getMaster() != null) {
                if (getResetter() != null) {
                    getResetter().toBase();
                    setBeingReset(false);
                    return;
                }
            }
        //To-Cleanup
        //         getPropCache().clear();
        //         getIntegerMap(false).clear(); // TODO [OPTIMIZED] no need to clear
        //         // type's map?
        //         if (modifierMaps != null) {
        //             modifierMaps.clear(); // remember? For interesting spells or src.main.system.log
        //         }
        //         // info...
        //         if (!type.checkProperty(G_PROPS.DISPLAYED_NAME)) {
        //             setProperty(G_PROPS.DISPLAYED_NAME, getName(), true);
        //         }
        //
        //         if (this.owner != getOriginalOwner()) {
        //             LogMaster.src.main.system.log(LogMaster.CORE_DEBUG, getName()
        //              + ": original owner restored!");
        //             this.owner = getOriginalOwner();
        //         }
        //
        //         for (PARAMETER p : getParamMap().keySet()) {
        //             if (p == null) {
        //                 continue;
        //             }
        //             if (p.isDynamic()) {
        //                 if (p.isWriteToType()) {
        //                     getType().setParam(p, getParam(p), true);
        //                 }
        //                 continue;
        //             }
        //
        //             String baseValue = getType().getParam(p);
        //             String value = getParam(p);
        //
        //             getValueCache().put(p, value);
        //
        //             if (!value.equals(baseValue)) {
        //                 if (isValidMapStored(p)){
        //                     getValidParams().put(p, NumberUtils.getIntParse(value));
        //                 }
        //                 String amount = getType().getParam(p);
        //                 putParameter(p, amount);
        //                 if (game.isStarted() && !game.isSimulation()) {
        //                     if (p.isDynamic()) {
        //                         fireParamEvent(p, amount, CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED);
        //                     }
        //                 }
        //             }
        //         }
        //         for (PROPERTY p : getPropMap().keySet()) {
        //
        //             if (p.isDynamic()) {
        //                 if (p.isWriteToType()) {
        //                     getType().setProperty(p, getProperty(p));
        //                 }
        //                 continue;
        //             }
        //             String baseValue = getType().getProperty(p);
        //             if (TextParser.isRef(baseValue)) {
        //                 baseValue = new Property(baseValue).getStr(ref);
        //                 if ((baseValue) == null) {
        //                     baseValue = getType().getProperty(p);
        //                 }
        //             }
        //             String value = getProperty(p);
        //             getValueCache().put(p, value);
        //             if (!value.equals(baseValue)) {
        //                 putProperty(p, baseValue);
        //             } else {
        //                 putProperty(p, baseValue);
        //             }
        //
        //         }
        // //        resetStatus();
        //         setDirty(false);
        //         setBeingReset(false);

    }

    protected boolean isResetViaHandler() {
        return true;
    }

    public boolean isValidMapStored(PARAMETER p) {
        return false;
    }

    public Map<VALUE, String> getValueCache() {
        return valueCache;
    }

    public String getCachedValue(VALUE value) {
        String val = valueCache.get(value);
        if (val == null) {
            return getValue(value);
        }

        return val;
    }

    public void constructConcurrently() {
        if (constructing) {
            return;
        }
        constructing = true;
        Weaver.inNewThread(new Runnable() {
            public void run() {
                construct();
            }
        });

    }

    public void construct() {
        getInitializer().construct();
    }

    public boolean isConstructAlways() {
        return false;
    }

    public void resetRef() {
        setRef(ref);
    }


    public void addStatus(STATUS value) {
        addProperty(G_PROPS.STATUS, value.toString());
    }

    public void addStatus(String value) {
        addProperty(G_PROPS.STATUS, value);
    }

    public void removeStatus(String value) {
        removeProperty(G_PROPS.STATUS, value);
    }

    public void newRound() {
    }

    public void clicked() {
    }

    public void invokeRightClicked() {
        clicked();
    }

    @Override
    public void run() {
        clicked();
    }

    public void invokeClicked() {
        clicked();
    }


    public boolean kill() {
        return kill(this, true, false);
    }

    public boolean kill(Entity killer, boolean leaveCorpse, Boolean quietly) {
        setDead(true);
        return true;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGenericGame() {
        return game;
    }

    public String getOBJ_TYPE() {
        if (TYPE_ENUM != null) {
            return TYPE_ENUM.getName();
        } else {
            TYPE_ENUM = ContentValsManager.getOBJ_TYPE(getProperty(G_PROPS.TYPE));
        }
        return getProperty(G_PROPS.TYPE);
    }

    public OBJ_TYPE getOBJ_TYPE_ENUM() {
        if (TYPE_ENUM == null) {
            TYPE_ENUM = ContentValsManager.getOBJ_TYPE(getOBJ_TYPE());
        }
        return TYPE_ENUM;
    }

    public void setOBJ_TYPE_ENUM(OBJ_TYPE TYPE_ENUM) {
        this.TYPE_ENUM = TYPE_ENUM;
    }

    @Override
    public String toString() {
        return getName() + " - " + id + " (" + getOBJ_TYPE() + ")";
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        setDirty(true);
    }

    public List<AbilityObj> getPassives() {
        return passives;
    }

    public void setPassives(List<AbilityObj> passives) {
        this.passives = passives;
    }

    public List<AbilityObj> getPassivesFiltered() {
        return null;
    }

    public List<ActiveObj> getActives() {
        return actives;
    }

    public void setActives(List<ActiveObj> list) {
        this.actives = list;
    }

    public boolean isUpgrade() {
        if (checkBool(GenericEnums.STD_BOOLS.NON_REPLACING)) {
            return false;
        }
        return !getProperty(G_PROPS.BASE_TYPE).isEmpty();
        // return DataManager.isTypeName(getProperty(G_PROPS.BASE_TYPE)); //too
        // heavy!
    }

    public Player getOriginalOwner() {
        return originalOwner;
    }

    public void setOriginalOwner(Player originalOwner) {
        this.originalOwner = originalOwner;
    }

    public boolean isNeutral() {
        return getChecker().isNeutral();
    }

    public boolean isOwnedBy(Player player) {
        if (getOwner() == player) {
            return true;
        }
        return getChecker().isOwnedBy(player);
    }

    public ASPECT getAspect() {
        return new EnumMaster<ASPECT>()
                .retrieveEnumConst(ASPECT.class, getProperty(G_PROPS.ASPECT));
    }

    public ImageIcon getDefaultIcon() {
        return ImageManager.getIcon(getProperty(G_PROPS.IMAGE, true));
    }

    public Image getImage() {
        return getIcon().getImage();
    }

    public void setImage(String image) {
        setProperty(G_PROPS.IMAGE, image, true);
    }

    public ImageIcon getIcon() {
        if (ImageManager.isValidIcon(customIcon)) {
            return customIcon;
        }
        if (!CoreEngine.isLevelEditor()) {
            if (ImageManager.isValidIcon(icon)) {
                return icon;
            }
        }
        String property = getImagePath();
        property = TextParser.parse(property, ref);
        icon = ImageManager.getIcon(property);
        if (!ImageManager.isValidIcon(icon)) {
            icon = ImageManager.getDefaultTypeIcon(this);
        }
        return icon;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isMine() {
        return getOwner().isMe();
    }

    public String getImagePath() {
        return getProperty(G_PROPS.IMAGE);
    }

    public String getLargeImagePath() {
        return ImageManager.getLargeImage(this);
    }

    public String getFullSizeImagePath() {
        if (!checkProperty(G_PROPS.FULLSIZE_IMAGE))
            return ImageManager.getFullSizeImage(this);
        return getProperty(G_PROPS.FULLSIZE_IMAGE);
    }

    public String getEmblemPath() {
        return getProperty(G_PROPS.EMBLEM);
    }

    public void resetRawValues() {
        if (isRawValuesOn())
            for (PARAMETER param : ContentValsManager.getParamsForType(getOBJ_TYPE(), false)) {
                // get values from ValueIcons?
                getRawValues().put(param, String.valueOf(getIntParam(param)));
            }

    }

    public boolean isRawValuesOn() {
        return false;
    }

    public String getGroupingKey() {
        return getProperty(getOBJ_TYPE_ENUM().getGroupingKey());
    }

    public EntityAnimator getAnimator() {
        return getMaster().getAnimator();
    }

    public EntityLogger getLogger() {
        return getMaster().getLogger();
    }

    public EntityInitializer getInitializer() {
        return getMaster().getInitializer();
    }

    public EntityCalculator getCalculator() {
        return getMaster().getCalculator();
    }

    public EntityChecker getChecker() {
        return getMaster().getChecker();
    }

    public EntityHandler getHandler() {
        return getMaster().getHandler();
    }

    public EntityResetter getResetter() {
        return getMaster().getResetter();
    }

    public void reset() {
        getResetter().reset();
    }

    public ImageIcon getCustomIcon() {
        if (customIcon == null) {
            if (game != null) {
                if (getGame().isSimulation()) {
                    if (ref != null) {
                        Map<String, ImageIcon> cache = ImageManager.getCustomIconCache().get(
                                ref.getSourceObj());
                        if (cache == null) {
                            return null;
                        }
                        return cache.get(getName()); // modified name for
                    }
                }
            }
        }

        return customIcon;
    }

    public void setCustomIcon(ImageIcon customIcon) {
        if (getGame().isSimulation()) {
            Map<String, ImageIcon> cache = ImageManager.getCustomIconCache()
                    .get(ref.getSourceObj());
            if (cache == null) {
                cache = new HashMap<>();
                ImageManager.getCustomIconCache().put(ref.getSourceObj(), cache);
            }
            cache.put(getName(), customIcon);
        }
        this.customIcon = customIcon;
    }

    public void setDisplayedName(String displayedName) {
        setProperty(G_PROPS.DISPLAYED_NAME, displayedName);
    }
}
