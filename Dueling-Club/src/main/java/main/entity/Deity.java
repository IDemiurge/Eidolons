package main.entity;

import main.content.DC_ContentManager;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.ability.construct.AbilityConstructor;
import main.entity.obj.ActiveObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.system.auxiliary.StringMaster;
import main.system.images.ImageManager;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Deity extends Entity {
    private List<String> unitPool;
    private LinkedList<Deity> alliedDeities;
    private LinkedList<Deity> friendDeities;
    private LinkedList<Deity> enemyDeities;
    private ImageIcon emblem;

    public Deity(ObjType type, Game game, Ref ref) {
        super(type, ref.getPlayer(), game, ref);
    }

    private void initUnitPool() {
        // String value = deity.getProperty(DC_PROPS.FOLLOWER_UNITS);
        // this.unitPool = ListMaster.toList(value.toString(), false);
        unitPool = (DataManager.getTypesSubGroupNames(DC_TYPE.UNITS, getName()));
        // for (ObjType type : alliedDeities) {
        // // value = type.getProperty(DC_PROPS.FOLLOWER_UNITS);
        // // unitPool.addAll(ListMaster.toList(value.toString(), false));
        // unitPool.addAll(DataManager.getTypesSubGroup(OBJ_TYPES.UNITS,
        // type.getName()));
        // }
        // for (ObjType type : friendDeities) {
        // // value = type.getProperty(DC_PROPS.FOLLOWER_UNITS);
        // // unitPool.addAll(ListMaster.toList(value.toString(), false));
        // unitPool.addAll(DataManager.getTypesSubGroup(OBJ_TYPES.UNITS,
        // type.getName()));
        // }
    }

    @Override
    public Integer getId() {
        return -1;
    }

    public List<String> getUnitPool() {
        if (unitPool == null) {
            initUnitPool();
        }
        return unitPool;
    }

    private void initEnemyDeities() {

        if (enemyDeities == null) {
            enemyDeities = new LinkedList<>();
        }
        for (String type : StringMaster.open(getProperty(PROPS.ENEMY_DEITIES))) {
            Deity deity = DC_ContentManager.getDeity(ref, type);
            if (deity != null) {
                enemyDeities.add(deity);
            }
        }
    }

    public LinkedList<Deity> getEnemyDeities() {
        if (enemyDeities == null) {
            initEnemyDeities();
        }
        return enemyDeities;
    }

    public LinkedList<Deity> getAllyDeities() {
        if (alliedDeities == null) {
            initAlliedDeities();
        }
        return alliedDeities;
    }

    public LinkedList<Deity> getFriendDeities() {
        if (friendDeities == null) {
            initFriendDeities();
        }
        return friendDeities;
    }

    private void initFriendDeities() {

        if (friendDeities == null) {
            friendDeities = new LinkedList<>();
        }
        for (String type : StringMaster.open(getProperty(PROPS.FRIEND_DEITIES))) {
            Deity deity = DC_ContentManager.getDeity(ref, type);
            if (deity != null) {
                friendDeities.add(deity);
            }
        }
    }

    private void initAlliedDeities() {

        if (alliedDeities == null) {
            alliedDeities = new LinkedList<>();
        }
        for (String type : StringMaster.open(getProperty(PROPS.ALLIED_DEITIES))) {
            Deity deity = DC_ContentManager.getDeity(ref, type);
            if (deity != null) {
                alliedDeities.add(deity);
            }
        }

    }

    private void initEmblem() {
        if (getProperty(G_PROPS.EMBLEM, true).equals("")) {
            setProperty(G_PROPS.EMBLEM, getProperty(G_PROPS.EMBLEM, true));
        }

        if (getProperty(G_PROPS.EMBLEM, true).equals("")) {
            setProperty(G_PROPS.EMBLEM, ImageManager.DEFAULT_EMBLEM);

        }
        this.setEmblem(ImageManager.getIcon(getProperty(G_PROPS.EMBLEM, true)));
        // if (ImageManager.getIcon(getProperty(G_PROPS.EMBLEM, true)) != null)
        // // if hero is party commander; diversified emblems for monsters
        // owner.setEmblem(ImageManager
        // .getIcon(getProperty(G_PROPS.EMBLEM, true)).getEmitterPath());
    }

    @Override
    public void construct() {
        AbilityConstructor.constructActives(this);
        setConstructed(true);

    }

    @Override
    public void init() {

        initEmblem();
        initFriendDeities();
        initAlliedDeities();
        initEnemyDeities();
        toBase();
        // initUnitPool();

    }

    public ImageIcon getEmblem() {
        return emblem;
    }

    public void setEmblem(ImageIcon emblem) {
        this.emblem = emblem;
    }

    public void applyHeroBonuses(Unit hero) {
        if (!isConstructed()) {
            construct();
        }
        for (ActiveObj active : getActives()) {
            active.activatedOn(Ref.getSelfTargetingRefCopy(hero));
        }
    }

}
