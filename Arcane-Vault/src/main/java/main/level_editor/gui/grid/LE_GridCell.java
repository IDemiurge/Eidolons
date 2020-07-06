package main.level_editor.gui.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.bf.grid.cell.GridCellContainer;
import eidolons.libgdx.gui.LabelX;
import main.game.bf.Coordinates;
import main.level_editor.LevelEditor;
import main.level_editor.backend.display.LE_DisplayMode;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LE_GridCell extends GridCellContainer {

    //colorOverlay
    Map<Color, FadeImageContainer> colorOverlays = new HashMap<>();

    LabelX scriptsLabel;
    LabelX aiLabel;
    LabelX decorLabel;
    public static LE_GridCell hoveredCell;

    public LE_GridCell(TextureRegion backTexture, int gridX, int gridY, Function<Coordinates, Color> colorFunc) {
        super(backTexture, gridX, gridY, colorFunc);
        addActor(scriptsLabel = new LabelX("", StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.NYALA,
                12, GdxColorMaster.PURPLE)));
        addActor(aiLabel = new LabelX("", StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.NYALA,
                12, GdxColorMaster.CYAN)));
        addActor(decorLabel = new LabelX("", StyleHolder.getSizedColoredLabelStyle(FontMaster.FONT.NYALA,
                12, GdxColorMaster.RED)));
        scriptsLabel.setTouchable(Touchable.disabled);
        aiLabel.setTouchable(Touchable.disabled);
        decorLabel.setTouchable(Touchable.disabled);

        addListener(new LE_GridCellHighlighter(this).getController());
    }

    @Override
    public void applyColor(float lightness, Color c) {
        if (!getDisplayMode().isGameView()) {
            super.applyColor(0, c);
        }
          else super.applyColor(lightness, c);
    }

    @Override
    public void setHovered(boolean hovered) {
        super.setHovered(hovered);
        if (hovered) {
            hoveredCell = this;
        }
    }

    @Override
    protected boolean isCoordinatesShown() {
        return getDisplayMode().isShowCoordinates();
    }

    private LE_DisplayMode getDisplayMode() {
        return LevelEditor.getModel().getDisplayMode();
    }

    public void displayModeUpdated(LE_DisplayMode mode) {
        for (Color color : colorOverlays.keySet()) {
            //check?
            if (mode.isShowAllColors())
                colorOverlays.get(color).fadeIn();
            else
                colorOverlays.get(color).fadeOut();
        }

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        GdxMaster.right(aiLabel);
        GdxMaster.center(scriptsLabel);
        scriptsLabel.setY(GdxMaster.getTopY(scriptsLabel) - 15);
        aiLabel.setVisible(getDisplayMode().isShowMetaAi());
        scriptsLabel.setVisible(getDisplayMode().isShowScripts());
        decorLabel.setVisible(getDisplayMode().isShowDecorText());
        aiLabel.setZIndex(Integer.MAX_VALUE);
        scriptsLabel.setZIndex(Integer.MAX_VALUE);
    }

    public LabelX getScriptsLabel() {
        return scriptsLabel;
    }

    public LabelX getAiLabel() {
        return aiLabel;
    }

    public LabelX getDecorLabel() {
        return decorLabel;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        super.draw(batch, parentAlpha);

    }

    @Override
    public void setTeamColor(Color teamColor) {
        super.setTeamColor(teamColor);
        setColor(teamColor);
    }

    @Override
    public int getUnitViewCountEffective() {
        return super.getUnitViewCountEffective() + 1;
    }

    @Override
    protected EventListener createListener() {
        return new ClickListener(-1) {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);

            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                LogMaster.log(1, x + " " + y);
                InputEvent e = new InputEvent();
                e.setButton(event.getButton());
                Eidolons.onNonGdxThread(() ->
                        LevelEditor.getCurrent().getManager().getMouseHandler().
                                handleCellClick(e, getTapCount(), getGridX(), getGridY()));
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                //                if (button==0)
                //                    return false;
                return true;
            }
        };
    }

    protected boolean checkIgnored() {
        if (!isVisible())
            return true;
        if (Eidolons.game == null)
            return true;

        return !isWithinCamera();
    }

    protected boolean isShadingSupported() {
        return false;
    }
}
