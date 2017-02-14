package main.client.cc.gui.neo.points;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.PointMaster;
import main.content.parameters.PARAMETER;
import main.entity.Entity;
import main.entity.obj.unit.DC_HeroObj;
import main.swing.components.panels.page.info.element.ValueTextComp;
import main.system.launch.CoreEngine;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class PointSpinnerModel {

    protected Entity buffer;
    protected PARAMETER param;
    protected PARAMETER pool;

    protected int modified;
    protected DC_HeroObj hero;

    public PointSpinnerModel(ValueTextComp comp, DC_HeroObj hero, Entity buffer, PARAMETER param,
                             PARAMETER pool) {
        this.hero = hero;
        this.buffer = buffer;
        this.param = param;
        this.pool = pool;
        reset();
    }

    public void reset() {
        modified = 0;
    }

    protected boolean checkPool(int cost) {
        return buffer.getIntParam(pool) >= cost;
    }

    protected int getCost(boolean down) {
        Integer value = buffer.getIntParam(param);
        return getCost(value, down);
    }

    protected int getCost(Integer value, boolean down) {
        if (down) {
            return PointMaster.getPointCost(value, buffer, param);
        }
        return PointMaster.getPointCost(value + 1, buffer, param);
    }

    protected int getCost() {
        return getCost(false);
    }

    public void up() {
        SoundMaster.playStandardSound(getUpSound());
        buffer.modifyParameter(pool, -getCost());
        buffer.modifyParameter(param, 1);
        refresh(true);
        modified++;
    }

    protected STD_SOUNDS getBlockedSound() {
        return STD_SOUNDS.CLICK_BLOCKED;
    }

    protected STD_SOUNDS getUpSound() {
        if (param.isAttribute()) {
            return STD_SOUNDS.ButtonUp;
        }
        return STD_SOUNDS.OK_STONE;
    }

    protected STD_SOUNDS getDownSound() {
        if (param.isAttribute()) {
            return STD_SOUNDS.ButtonDown;
        }
        return STD_SOUNDS.CLICK1;
    }

    protected void refresh(boolean up) {
        // comp.refresh();
        int buffer = modified;
        if (!isUndoOnDown()) {
            CharacterCreator.getHeroManager().applyChangedType(false, hero, this.buffer);
        } else {
            CharacterCreator.getHeroManager().applyChangedType(!up, hero, this.buffer);
        }
        modified = buffer;
        // CharacterCreator.getHeroPanel(hero).getMiddlePanel().getScc()
        // .refreshPools();
        // if (param.isAttribute())
        // CharacterCreator.getHeroPanel(hero).getCurrentTab().refresh();
        // else
        // CharacterCreator.getHeroPanel(hero).getMvp().refresh();
        // CharacterCreator.getHeroPanel(hero).refresh(); // ?
    }

    protected boolean checkUp() {
        if (CoreEngine.isArcaneVault()) {
            return true;
        }
        if (!isUndoOnDown()) {
            if (modified < 0) {
                return true;
            }
        }
        return checkPool(getCost());
    }

    public void tryUp() {
        if (!checkUp()) {
            SoundMaster.playStandardSound(getBlockedSound());
            return;
        }
        up();
    }

    protected boolean checkDown() {
        if (CoreEngine.isArcaneVault()) {
            return true;
        }
        if (!isUndoOnDown()) {
            if ((modified) > 0) {
                return true;
            } else {
                return checkPool(getCost(true));
            }
        }

        return modified > 0;

    }

    public void tryDown() {
        boolean result = checkDown();
        if (!result) {
            // if (Launcher.DEV_MODE) {
            // SoundMaster.playStandardSound(STD_SOUNDS.SLING);
            // down();
            // } else
            SoundMaster.playStandardSound(getBlockedSound());
        } else {
            down();
        }
    }

    public void down() {
        SoundMaster.playStandardSound(getDownSound());
        buffer.modifyParameter(pool, getCost(true)); // *previous* cost!
        buffer.modifyParameter(param, -1);
        refresh(false);
        int buffer = modified;
        // TODO ???
        if (isUndoOnDown()) {
            CharacterCreator.getHeroManager().stepBack(hero);
        }
        modified = buffer;
        modified--;
    }

    protected boolean isUndoOnDown() {
        return true;
    }

    public void setEntity(Entity entity) {
        this.buffer = entity;
    }

}
