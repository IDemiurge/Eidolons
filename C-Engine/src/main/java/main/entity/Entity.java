package main.entity;

import main.ability.AbilityObj;
import main.content.ContentManager;
import main.content.OBJ_TYPE;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.ASPECT;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.obj.ActiveObj;
import main.entity.obj.Obj;
import main.entity.tools.*;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.game.logic.battle.player.Player;
import main.game.logic.event.EventType.CONSTRUCTED_EVENT_TYPE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.images.ImageManager;
import main.system.launch.CoreEngine;
import main.system.math.Property;
import main.system.text.TextParser;
import main.system.threading.Weaver;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Root class of every CoreEngine object
 * Contains property and parameter maps and an array of methods for retrieving and modifying them
 * Has ID by which it can be found from GameState
 * <p>
 * <p>
 * has OBJ_TYPE that defines how it is treated by things like Targeting (Filter)
 * <p>
 * has ObjType that defines its [base] values  (property and parameter)
 * <p>
 * toBase() is called whenever we need to update them - first they are set to their base,
 * then modified by various effects
 * <p>
 * esetObjects, afterEffects (apply masteries/attributes), preCheck parameter buff rules (focus, stamina, …)
 * :: Dynamic values are not reset to base on toBase()
 * :: Properties – container (; separated), variable(value),
 * :: Parameters – formula syntax: {ref_value}, e.g. {source_base strength}, event_ prefix checked first, replaced ref with event’s ref if necessary
 * :: Buffs – created with a duration or a retainCondition, add ContinuousEffect’s while in game
 */
public abstract class Entity extends DataModel implements OBJ {

    protected List<AbilityObj> passives = new LinkedList<>();
    protected List<ActiveObj> actives = new LinkedList<>();

    protected boolean dead = false;
    protected Player originalOwner;

    protected boolean added;
    EntityMaster master;


    public Entity() {

    }

    public Entity(ObjType type, Player owner, Game game, Ref ref) {
        // initial party?
        if (type == null) {
            LogMaster.log(1, "null type!" + ref);
//            if (!CoreEngine.isTEST_MODE()) {
            RuntimeException e = new RuntimeException();
            throw (e);
//            }
        } else {
            this.game = game;
            getId(); // new id if null
            this.TYPE_ENUM = type.getOBJ_TYPE_ENUM();
            this.type = (type); // no map cloning by default
            this.owner = owner;
            this.setOriginalOwner(owner);
            setProperty(G_PROPS.NAME, type.getName());
            setOriginalName(type.getName());
            LogMaster.log(-1, id + " - NEW ID for " + type.getName());

            setRef(ref);
            master = initMaster();
            init();
        }
    }

    protected EntityMaster initMaster() {
        return new EntityMaster(this) {
            @Override
            protected EntityAnimator createEntityAnimator() {
                return null;
            }

            @Override
            protected EntityLogger createEntityLogger() {
                return null;
            }

            @Override
            protected EntityInitializer createInitializer() {
                return new EntityInitializer(getEntity(), this);
            }

            @Override
            protected EntityChecker createEntityChecker() {
                return null;
            }

            @Override
            protected EntityResetter createResetter() {
                return new EntityResetter(getEntity(), this);
            }

            @Override
            protected EntityCalculator createCalculator() {
                return null;
            }

            @Override
            protected EntityHandler createHandler() {
                return null;
            }
        };
    }

    public void addToState() {
        if (game.isCloningMode())
            return ;

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

    public void toBase() {
        if (getMaster() != null) {
            if (getResetter() != null) {
                getResetter().toBase();
                return;
            }
        }

        getPropCache().clear();
        getIntegerMap(false).clear(); // TODO [OPTIMIZED] no need to clear
        // type's map?
        if (modifierMaps != null) {
            modifierMaps.clear(); // remember? For interesting spells or log
        }
        // info...
        if (!type.checkProperty(G_PROPS.DISPLAYED_NAME)) {
            setProperty(G_PROPS.DISPLAYED_NAME, getName(), true);
        }

        if (this.owner != getOriginalOwner()) {
            LogMaster.log(LogMaster.CORE_DEBUG, getName()
             + ": original owner restored!");
        }

        this.owner = getOriginalOwner();

        HashSet<PARAMETER> params = new HashSet<>(getParamMap().keySet());
        params.addAll(type.getParamMap().keySet());
        for (PARAMETER p : params) {
            if (p == null) {
                continue;
            }
            if (p.isDynamic()) {
                if (p.isWriteToType()) {
                    getType().setParam(p, getParam(p), true);
                }
                continue;
            }

            String baseValue = getType().getParam(p);
            String value = getParam(p);
            if (!value.equals(baseValue)) {
                String amount = getType().getParam(p);
                putParameter(p, amount);
                if (game.isStarted() && !game.isSimulation()) {
                    if (p.isDynamic()) {
                        fireParamEvent(p, amount, CONSTRUCTED_EVENT_TYPE.PARAM_MODIFIED);
                    }
                }
            }
        }
        HashSet<PROPERTY> props = new HashSet<>(getPropMap().keySet());
        props.addAll(type.getPropMap().keySet());
        for (PROPERTY p : props) {

            if (p.isDynamic()) {
                if (p.isWriteToType()) {
                    getType().setProperty(p, getProperty(p));
                }
                continue;
            }
            String baseValue = getType().getProperty(p);
            if (TextParser.isRef(baseValue)) {
                baseValue = new Property(baseValue).getStr(ref);
                if ((baseValue) == null) {
                    baseValue = getType().getProperty(p);
                }
            }
            String value = getProperty(p);
            if (!value.equals(baseValue)) {
                putProperty(p, baseValue);
            } else {
                putProperty(p, baseValue);
            }

        }
//        resetStatus();
        setDirty(false);

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
//TODO temp
        if (getInitializer() != null) {
            getInitializer().construct();
        }

    }

    public boolean isConstructAlways() {
        return false;
    }

    public void resetRef() {
        setRef(ref);
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
        if (game == null) {
            LogMaster.log(1, "Null game on " + toString());
            if (Game.game.isSimulation()) {
                game = Game.game;
            }
        }
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
            TYPE_ENUM = ContentManager.getOBJ_TYPE(getProperty(G_PROPS.TYPE));
        }
        return getProperty(G_PROPS.TYPE);
    }

    public OBJ_TYPE getOBJ_TYPE_ENUM() {
        if (TYPE_ENUM == null) {
            TYPE_ENUM = ContentManager.getOBJ_TYPE(getOBJ_TYPE());
        }
        return TYPE_ENUM;
    }

    public void setOBJ_TYPE_ENUM(OBJ_TYPE TYPE_ENUM) {
        this.TYPE_ENUM = TYPE_ENUM;
    }

    @Override
    public String toString() {
        return getName() + " - " + id + " (" + getOBJ_TYPE() + ")";
        // " (" + getOBJ_TYPE() + ") HAS: "
        // + propMap.getMap().toString() + " "
        // + paramMap.getMap().toString();
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
    public List<AbilityObj> getPassivesFiltered() {
        return null ;
    }

    public void setPassives(List<AbilityObj> passives) {
        this.passives = passives;
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
        icon = new ImageIcon(getImagePath());
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

    public void resetRawValues() {
        for (PARAMETER param : ContentManager.getParamsForType(getOBJ_TYPE(), false)) {
            // get values from ValueIcons?
            getRawValues().put(param, getIntParam(param) + "");
        }

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
}
