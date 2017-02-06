package main.entity.obj;

import main.ability.AbilityObj;
import main.content.CONTENT_CONSTS.STANDARD_PASSIVES;
import main.content.ContentManager;
import main.content.parameters.PARAMETER;
import main.content.properties.G_PROPS;
import main.content.properties.PROPERTY;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.game.battlefield.Coordinates;
import main.game.player.DC_Player;
import main.game.player.Player;
import main.rules.mechanics.ConcealmentRule.VISIBILITY_LEVEL;
import main.system.auxiliary.ListMaster;
import main.system.auxiliary.StringMaster;

public abstract class DC_HeroAttachedObj extends DC_Obj implements AttachedObj {
    private static final String[] STD_PASSIVES_EXCEPTIONS = {STANDARD_PASSIVES.INDESTRUCTIBLE
            .getName(),};

    protected DC_HeroObj hero;

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
        return (DC_Player) getOwnerObj().getOwner();
    }

    @Override
    public DC_HeroObj getOwnerObj() {
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
    public void init() {
        super.init();
        if (isConstructOnInit()) {
            construct();
        }
    }

    protected boolean isConstructOnInit() {
        return true;
    }

    public void apply() {
        // if (getHero() == null)
        initHero();
        setRef(getHero().getRef());
        activatePassives();
    }

    protected DC_HeroObj getHero() {
        if (hero == null) {
            initHero();
        }

        return hero;
    }

    public void setHero(DC_HeroObj hero) {
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
    protected void activatePassives() {
        if (ref == null) {
            return;
        }
        for (String prop : StringMaster.openContainer(getProperty(G_PROPS.STANDARD_PASSIVES))) {
            if (!new ListMaster<String>().contains(STD_PASSIVES_EXCEPTIONS, (prop), true)) {
                getHero().addProperty(G_PROPS.STANDARD_PASSIVES, prop);
            }
        }
        for (String prop : StringMaster.openContainer(getProperty(G_PROPS.IMMUNITIES))) {
            getHero().addProperty(G_PROPS.IMMUNITIES, prop);
        }

        for (String prop : StringMaster.openContainer(getProperty(G_PROPS.ACTIVES))) {
            getHero().addProperty(G_PROPS.ACTIVES, prop); // TODO
            // auto-requirement
        }
        if (!isActivatePassives()) {
            for (String prop : StringMaster.openContainer(getProperty(G_PROPS.PASSIVES))) {
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
                    abil.activate(ref);

                } catch (Exception e) {
                    e.printStackTrace();
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
        setHero((DC_HeroObj) getGame().getObjectById(heroId));
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
