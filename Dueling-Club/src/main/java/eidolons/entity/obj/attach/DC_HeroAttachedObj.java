package eidolons.entity.obj.attach;

import eidolons.entity.obj.DC_Obj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.ability.AbilityObj;
import main.content.ContentManager;
import main.content.enums.entity.UnitEnums;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.AttachedObj;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;
import main.game.core.game.MicroGame;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

public abstract class DC_HeroAttachedObj extends DC_Obj implements AttachedObj {
    private static final String[] STD_PASSIVES_EXCEPTIONS = {UnitEnums.STANDARD_PASSIVES.INDESTRUCTIBLE
     .getName(),};

    protected Unit hero;

    protected Integer heroId;

    public DC_HeroAttachedObj(ObjType type, Player owner, MicroGame game, Ref ref) {
        super(type, owner, game, ref);
    }

    @Override
    public VISIBILITY_LEVEL getVisibilityLevel() {
        return getOwnerObj().getVisibilityLevel();
    }

    @Override
    public VISIBILITY_LEVEL getVisibilityLevel(boolean active) {
        return getOwnerObj().getVisibilityLevel(active);
    }

    @Override
    public DC_Player getOwner() {
        if (getOwnerObj() == null)
            return DC_Player.NEUTRAL;
        return getOwnerObj().getOwner();
    }

    @Override
    public Unit getOwnerObj() {
        return getHero();
    }

    protected KEYS getKey() {
        return null;
    }

    // @Override
    // public void clicked() {
    // // delegate to hero's Item Active!
    // }
    @Override
    public Coordinates getCoordinates() {
        if (hero == null) {
            return super.getCoordinates();
        }
        // new Coordinates(0,0);
        return hero.getCoordinates();
    }

    @Override
    public boolean isOutsideCombat() {
        if (getHero() == null)
            return true;
        return getHero().isOutsideCombat();
    }

    @Override
    public void init() {
        super.init();
        if (isConstructOnInit()) {
            construct();
        }
    }

    protected boolean isConstructOnInit() {
        return getOwnerObj() != null;
    }

    public void apply() {
        // if (getHero() == null)
        initHero();
        setRef(getHero().getRef());
        activatePassives();
    }

    protected Unit getHero() {
        if (hero == null) {
            initHero();
        }

        return hero;
    }

    public void setHero(Unit hero) {
        this.hero = hero;
    }

    protected void modifyHeroParameters() {
        for (PARAMETER p : paramMap.getMap().keySet()) {
            if (ContentManager.isValueForOBJ_TYPE(getHero().getOBJ_TYPE_ENUM(), p)) {
                Integer amount = getIntParam(p);
                if (amount != 0) {
                    getHero().modifyParameter(p, amount);
                }
            }
        }

    }

    @Override
    public void activatePassives() {
        if (ref == null) {
            return;
        }
        for (String prop : StringMaster.open(getProperty(G_PROPS.STANDARD_PASSIVES))) {
            if (!new ListMaster<String>().contains(STD_PASSIVES_EXCEPTIONS, (prop), true)) {
                getHero().addProperty(G_PROPS.STANDARD_PASSIVES, prop);
            }
        }
        for (String prop : StringMaster.open(getProperty(G_PROPS.IMMUNITIES))) {
            getHero().addProperty(G_PROPS.IMMUNITIES, prop);
        }

        for (String prop : StringMaster.open(getProperty(G_PROPS.ACTIVES))) {
            getHero().addProperty(G_PROPS.ACTIVES, prop); // TODO
            // auto-requirement
        }
        if (!isActivatePassives()) {
            for (String prop : StringMaster.open(getProperty(G_PROPS.PASSIVES))) {
                getHero().addProperty(G_PROPS.PASSIVES, prop);
            }
            getHero().setConstructed(false);
            return;
        }
        ref.setID(getKey(), getId());
        if (!isConstructed()) {
            construct();
        }
        if (passives != null) {
            for (AbilityObj abil : passives) {
                try {
                    abil.activatedOn(ref);

                } catch (Exception e) {
                    main.system.ExceptionMaster.printStackTrace(e);
                }
            }
        }

    }

    protected boolean isActivatePassives() {
        return false;
    }

    protected void initHero() {
        if (getGame() == null) {
            return;
        }
        setHero((Unit) getGame().getObjectById(heroId));
    }

    protected void initProp(PROPERTY prop) {
        if (prop.isContainer()) {
            getHero().addProperty(prop, getProperty(prop));
        }

    }

    protected void initParam(PARAMETER param) {
        Integer amount = getIntParam(param);
        if (amount != 0) {
            if (param.isMastery()) {
                param = ContentManager.getMasteryScore(param);
            }
            getHero().modifyParameter(param, amount);
        }
    }

    @Override
    public void setRef(Ref ref) {
        super.setRef(ref);
        this.heroId = ref.getId(KEYS.SOURCE.name());
    }

}
