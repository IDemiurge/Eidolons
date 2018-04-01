package eidolons.client.cc.gui.neo.points;

import eidolons.client.cc.CharacterCreator;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.swing.components.buttons.CustomButton;
import eidolons.system.math.DC_MathManager;
import main.system.sound.SoundMaster.STD_SOUNDS;

public class BuyButton extends CustomButton {
    protected boolean attr;
    protected boolean gold;
    protected StatsControlComponent statsControlComponent;

    public BuyButton(StatsControlComponent statsControlComponent, boolean attr, boolean gold) {
        super(VISUALS.REMOVE_BLOCKED);
        this.attr = attr;
        this.gold = gold;
        this.statsControlComponent = statsControlComponent;
        setVisuals(getVisuals(attr));
        this.gold = gold;
    }

    public BuyButton(boolean attr, boolean gold) {
        this(CharacterCreator.getHeroPanel().getMiddlePanel().getScc(), attr, gold);
    }

    protected VISUALS getVisuals(boolean attr) {
        return (attr) ? isEnabled() ? VISUALS.ADD : VISUALS.ADD_BLOCKED
         : isEnabled() ? VISUALS.REMOVE : VISUALS.REMOVE_BLOCKED;
    }

    @Override
    public void refresh() {
        setVisuals(getVisuals(attr));
    }

    @Override
    protected boolean isBackGroundLabelRequired() {
        return false;
    }

    // public VISUALS getVisuals() {
    // return (attr) ? isEnabled() ? VISUALS.ADD : VISUALS.ADD_BLOCKED
    // : isEnabled() ? VISUALS.REMOVE : VISUALS.REMOVE_BLOCKED
    //
    // ;
    // }

    public boolean isEnabled() {
        return statsControlComponent.checkParam(DC_MathManager.getBuyCost(attr, gold,
         statsControlComponent.getBufferType()), gold);
    }

    protected void playClickSound() {
        if (gold) {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__COINS);
        } else {
            DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOOK_OPEN);
        }

    }

    public void handleClick() {
        if (!isEnabled()) {
            playDisabledSound();
        }
        buyPoints(attr, gold);
        playClickSound();
    }

    protected void buyPoints(boolean attr, boolean gold) {
        statsControlComponent.buyPoints(attr, gold);
    }
}
