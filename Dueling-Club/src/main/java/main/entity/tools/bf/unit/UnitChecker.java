package main.entity.tools.bf.unit;

import main.content.enums.entity.UnitEnums;
import main.content.enums.entity.UnitEnums.CLASSIFICATIONS;
import main.content.enums.entity.UnitEnums.STANDARD_PASSIVES;
import main.content.enums.entity.UnitEnums.STATUS;
import main.content.values.properties.G_PROPS;
import main.entity.obj.DC_Obj;
import main.entity.obj.Obj;
import main.entity.obj.unit.Unit;
import main.entity.tools.EntityChecker;
import main.entity.tools.EntityMaster;
import main.game.core.game.DC_Game;

/**
 * Created by JustMe on 2/26/2017.
 */
public class UnitChecker extends EntityChecker<Unit> {
    public UnitChecker(Unit entity, EntityMaster<Unit> entityMaster) {
        super(entity, entityMaster);
    }

    @Override
    public UnitCalculator getCalculator() {
        return (UnitCalculator) super.getCalculator();
    }

    @Override
    public UnitInitializer getInitializer() {
        return (UnitInitializer) super.getInitializer();
    }


    @Override
    public UnitResetter getResetter() {
        return (UnitResetter) super.getResetter();
    }

    public boolean canUseItems() {
        return canUseWeapons() || canUseArmor();
    }

    public boolean canUseArmor() {
        return checkContainerProp(G_PROPS.CLASSIFICATIONS, UnitEnums.CLASSIFICATIONS.HUMANOID.toString());
    }


    public boolean canUseWeapons() {
        return isHero(); //TODO ?
        // return checkContainerProp(G_PROPS.CLASSIFICATIONS,
        // CLASSIFICATIONS.HUMANOID
        // .toString());
    }

    public boolean checkDualWielding() {
        if (getEntity().getSecondWeapon() == null || getEntity().getMainWeapon() == null) {
            return false;
        }
        if (getEntity().getMainWeapon().isRanged() || getEntity().getMainWeapon().isMagical()) {
            return false;
        }
        if (getEntity().getSecondWeapon().isRanged() || getEntity().getSecondWeapon().isMagical()) {
            return false;
        }
        return (getEntity().getSecondWeapon().isWeapon());

    }

    public boolean checkInSight() {
        return getEntity().checkInSight();
    }

    public boolean checkInSightForUnit(Unit unit) {
        return getEntity().checkInSightForUnit(unit);
    }

    public boolean checkClassification(CLASSIFICATIONS PROP) {
        return getEntity().checkClassification(PROP);
    }

    public boolean checkPassive(STANDARD_PASSIVES PROP) {
        return getEntity().checkPassive(PROP);
    }

    public boolean checkSelectHighlighted() {
        return getEntity().checkSelectHighlighted();
    }

    public boolean checkVisible() {
        return getEntity().checkVisible();
    }

    public boolean checkStatus(STATUS STATUS) {
        return getEntity().checkStatus(STATUS);
    }

    public boolean isImmortalityOn() {
        if (isMine()) {
            if (equals(getEntity().getOwner().getHeroObj())) {
                return game.isDummyMode();
            }
        }
        if (getGame().getTestMaster().isImmortal() != null) {
            return
             getGame().getTestMaster().isImmortal();
        }
        return getGame().isDummyPlus();
    }

    public boolean isMine() {
        return getEntity().getOwner().isMe();
    }

    public Boolean isLandscape() {
        return getEntity().isLandscape();
    }

    public boolean isWall() {
        return getEntity().isWall();
    }

    public boolean isObstructing(Obj obj) {
        return getEntity().isObstructing(obj);
    }

    public boolean isObstructing(Obj obj, DC_Obj target) {
        return getEntity().isObstructing(obj, target);
    }

    public boolean isSmall() {
        return getEntity().isSmall();
    }

    public boolean isShort() {
        return getEntity().isShort();
    }

    public boolean isTransparent() {
        return getEntity().isTransparent();
    }

    public boolean isTall() {
        return getEntity().isTall();
    }

    public boolean isBfObj() {
        return getEntity().isBfObj();
    }

    public boolean isHero() {
        return getEntity().isHero();
    }

    public boolean isDone() {
        return getEntity().isDone();
    }

    public boolean isUnmoved() {
        return getEntity().isUnmoved();
    }

    public boolean isFull() {
        return getEntity().isFull();
    }

    public boolean isHuge() {
        return getEntity().isHuge();
    }

    public boolean isTurnable() {
        return getEntity().isTurnable();
    }

    public boolean isDisabled() {
        return getEntity().isDisabled();
    }

    public boolean isUnconscious() {
        return getEntity().isUnconscious();
    }

    public boolean isIncapacitated() {
        return getEntity().isIncapacitated();
    }

    public boolean isImmobilized() {
        return getEntity().isImmobilized();
    }

    public boolean isLiving() {
        if (checkClassification(UnitEnums.CLASSIFICATIONS.UNDEAD)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.WRAITH)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.ELEMENTAL)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.CONSTRUCT)) {
            return false;
        }
        if (checkClassification(UnitEnums.CLASSIFICATIONS.STRUCTURE)) {
            return false;
        }
        return !checkClassification(UnitEnums.CLASSIFICATIONS.MECHANICAL);
    }

    public boolean isHidden() {
        return getEntity().isHidden();
    }

    @Override
    public DC_Game getGame() {
        return (DC_Game) super.getGame();
    }

}
