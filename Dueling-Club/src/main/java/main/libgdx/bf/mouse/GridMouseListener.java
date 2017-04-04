package main.libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import main.content.PARAMS;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.DC_Obj;
import main.game.battlefield.Coordinates;
import main.game.core.Eidolons;
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.bf.*;
import main.libgdx.gui.dialog.ValueTooltip;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.texture.TextureCache;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.test.frontend.FAST_DC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 1/7/2017.
 */
public class GridMouseListener extends ClickListener {
    private GridPanel gridPanel;
    private GridCell[][] cells;
    private Map<BattleFieldObject, BaseView> unitViewMap;

    public GridMouseListener(GridPanel gridPanel, GridCell[][] cells, Map<BattleFieldObject, BaseView> unitViewMap) {
        this.gridPanel = gridPanel;
        this.cells = cells;
        this.unitViewMap = unitViewMap;
    }

    private static String getPathFromName(String name) {
        return "UI\\value icons\\" + name.replaceAll("_", " ") + ".png";
    }

    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        gridPanel.getStage().setScrollFocus(gridPanel);

        int cell = (int) (x / cells[0][0].getWidth());
        int row = (int) (y / cells[0][0].getHeight());
        GridCell gridCell = cells[cell][row];
        if (gridCell.getInnerDrawable() != null && !event.isStopped()) {
            GridCellContainer innerDrawable = (GridCellContainer) gridCell.getInnerDrawable();
            Actor a = innerDrawable.hit(x, y, true);
            if (a != null && a instanceof BaseView) {
                BaseView uv = (BaseView) a;
                BattleFieldObject hero = unitViewMap.entrySet().stream()
                        .filter(entry -> entry.getValue() == uv).findFirst()
                        .get().getKey();

                List<ValueContainer> values = new ArrayList<>();

                final ValueContainer nameContainer = new ValueContainer(hero.getName(), "");
                nameContainer.setNameAlignment(Align.left);
                values.add(nameContainer);

                values.add(getValueContainer(hero, PARAMS.C_TOUGHNESS, PARAMS.TOUGHNESS));
                values.add(getValueContainer(hero, PARAMS.C_ENDURANCE, PARAMS.ENDURANCE));

                if (hero.getIntParam(PARAMS.N_OF_ACTIONS) > 0) {
                    values.add(getValueContainer(hero, PARAMS.C_N_OF_ACTIONS, PARAMS.N_OF_ACTIONS));
                }
                if (hero.getIntParam(PARAMS.N_OF_COUNTERS) > 0) {
                    values.add(getValueContainer(hero, PARAMS.C_N_OF_COUNTERS, PARAMS.N_OF_COUNTERS));
                }

                {
                    final ValueContainer valueContainer =
                            new ValueContainer("coord:", hero.getCoordinates().toString());
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                }

                if (hero.getFacing() != null || hero.getDirection() != null) {
                    final String name = "direction: " + (hero.getFacing() != null ?
                            hero.getFacing().getDirection() :
                            hero.getDirection());
                    final ValueContainer valueContainer = new ValueContainer(name, hero.getCoordinates().toString());
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                }

                if (hero.getIntParam(PARAMS.LIGHT_EMISSION) > 0) {
                    final ValueContainer valueContainer =
                            new ValueContainer("LIGHT_EMISSION", hero.getStrParam(PARAMS.LIGHT_EMISSION));
                    valueContainer.setNameAlignment(Align.left);
                    valueContainer.setValueAlignment(Align.right);
                    values.add(valueContainer);
                }

                if (hero.getCustomParamMap() != null) {
                    hero.getCustomParamMap().keySet().forEach(counter -> {
                        final String name = counter + " " + hero.getCustomParamMap().get(counter);
                        final ValueContainer valueContainer = new ValueContainer(name, "");
                        valueContainer.setNameAlignment(Align.left);
                        valueContainer.setValueAlignment(Align.right);
                        values.add(valueContainer);
                    });
                }

                final ValueTooltip tooltip = new ValueTooltip();
                tooltip.setUserObject(values);
                GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(tooltip));
                GuiEventManager.trigger(MOUSE_HOVER, new EventCallbackParam(hero));
                return true;
            }
        }
        GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(null));
        return false;
    }

    private ValueContainer getValueContainer(BattleFieldObject hero, PARAMS cur, PARAMS max) {
        final Integer cv = hero.getIntParam(max);
        final Integer v = hero.getIntParam(cur);
        final String name = max.getName();
        final TextureRegion iconTexture = TextureCache.getOrCreateR(getPathFromName(name));
        final ValueContainer valueContainer = new ValueContainer(iconTexture, name, v + "/" + cv);
        valueContainer.setNameAlignment(Align.left);
        valueContainer.setValueAlignment(Align.right);
        return valueContainer;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        Actor a;

        if (PhaseAnimator.getInstance().checkAnimClicked(x, y, pointer, button)) {
            return true;
        }
        a = gridPanel.hitChildren(x, y, true);
        if (a != null && a instanceof GridCell) {
            GridCell cell = (GridCell) a;
            if (gridPanel.getCellBorderManager().isBlueBorderActive() && event.getButton() == Input.Buttons.LEFT) {
                Borderable b = cell;
                if (cell.getInnerDrawable() != null) {
                    Actor unit = cell.getInnerDrawable().hit(x, y, true);
                    if (unit != null && unit instanceof Borderable) {
                        b = (Borderable) unit;
                    }
                }
                gridPanel.getCellBorderManager().hitAndCall(b);
            }

            if (cell.getInnerDrawable() != null) {
                Actor unit = cell.getInnerDrawable().hit(x, y, true);
                if (unit != null && unit instanceof BaseView) {
                    BattleFieldObject obj = unitViewMap.entrySet()
                            .stream().filter(entry -> entry.getValue() == unit).findFirst()
                            .get().getKey();

                    switch (event.getButton()) {
                        case Input.Buttons.RIGHT:
                            //TODO map the click to the right object in the stack?
                            GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(obj));
                            break;
                        default:
                            if (FAST_DC.getGameLauncher().SUPER_FAST_MODE || Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
                                GuiEventManager.trigger(SHOW_INFO_DIALOG, new EventCallbackParam(obj));
                            }
                    }
                }
            } else if (event.getButton() == Input.Buttons.RIGHT) {
                DC_Obj dc_cell = Eidolons.gameMaster.getCellByCoordinate(new Coordinates(cell.getGridX(), cell.getGridY()));
                GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(dc_cell));
            }
            event.stop();
            return true;
        }
        return false;
    }

}
