package libgdx.bf.overlays.bar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.EidolonsGame;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.gui.panels.dc.actionpanel.bar.DualParamBar;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.texture.Textures;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import main.content.values.parameters.PARAMETER;

/**
 * Created by JustMe on 11/21/2017. What if Secondary% is less than primary?
 */
public class HpBar extends DualParamBar {

    protected static Boolean hpAlwaysVisible;
    protected boolean queue;

    public HpBar(BattleFieldObject dataSource) {
        super(dataSource);
    }

    @Override
    protected String getBarBgPath() {
        return Textures.HP_BAR_BG;
    }

    @Override
    public void animateChange() {
        if (!HpBarManager.isHpBarVisible(getDataSource())) {
            return;
        }
        super.animateChange();
    }

    public static Boolean getHpAlwaysVisible() {
        if (hpAlwaysVisible == null) {
            hpAlwaysVisible = OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.HP_BARS_ALWAYS_VISIBLE);
        }
        return hpAlwaysVisible;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        barBg2.setVisible(!underIsGreater);

    }

    @Override
    protected PARAMETER getOverParam(boolean current) {
        return current ? PARAMS.C_TOUGHNESS : PARAMS.TOUGHNESS;
    }

    @Override
    protected PARAMETER getUnderParam(boolean current) {
        return current ? PARAMS.C_ENDURANCE : PARAMS.ENDURANCE;
    }

    @Override
    protected boolean isIgnored() {
        if (isDisplayedAlways())
            return false;
        if (displayedSecondaryPerc == 0)
            return true;
        if (fullLengthPerc == 0) {
            if (isAnimated()) {
                //                return true; TODO why?
            }
            if (!getDataSource().isPlayerCharacter() || EidolonsGame.getVar("endurance"))
                fullLengthPerc = displayedSecondaryPerc;
            else {
                fullLengthPerc = displayedPrimaryPerc;
            }
        }
        return false;
    }

    protected boolean isDisplayedAlways() {
        return HpBar.getHpAlwaysVisible() == true;

    }


    @Override
    public void setTeamColor(Color teamColor) {
        if (teamColor == GdxColorMaster.NEUTRAL)
            teamColor = GdxColorMaster.RED;
        super.setTeamColor(teamColor);
        initColors();
    }


    protected Color getColor(boolean over) {
        return over
                ? GdxColorMaster.lighter(getTeamColor(), 0.45f)
                : GdxColorMaster.darker(getTeamColor(), 0.25f);
    }

    @Override
    public Stage getStage() {
        return (queue) ? DungeonScreen.getInstance().getGuiStage()
                : DungeonScreen.getInstance().getGridStage();
    }

    @Override
    public boolean isTeamColorBorder() {
        return true;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
        labelsDisplayed = !queue;
    }

    @Override
    public String toString() {
        return
                (queue ? "queue hp bar" : label2.getText() + " bar ") +
                        getDataSource().getName();
    }

    @Override
    protected boolean isLabelsDisplayed() {
        return !queue && super.isLabelsDisplayed();
    }

    public boolean canHpBarBeVisible() {
        return HpBarManager.canHpBarBeVisible(getDataSource());
    }

    public boolean isHpBarVisible() {
        return HpBarManager.isHpBarVisible(getDataSource());
    }

    public static boolean isResetOnLogicThread() {
        return true;
    }
}
