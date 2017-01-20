package main.libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.PARAMS;
import main.entity.obj.DC_HeroObj;
import main.entity.obj.DC_Obj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.libgdx.anims.phased.PhaseAnimator;
import main.libgdx.bf.*;
import main.libgdx.bf.mouse.ToolTipManager.ToolTipRecordOption;
import main.libgdx.texture.TextureManager;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.test.frontend.FAST_DC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 1/7/2017.
 */
public class GridMouseListener extends ClickListener {
    private GridPanel gridPanel;
    private GridCell[][] cells;
    private Map<DC_HeroObj, BaseView> unitViewMap;

    public GridMouseListener(GridPanel gridPanel, GridCell[][] cells, Map<DC_HeroObj, BaseView> unitViewMap) {
        this.gridPanel = gridPanel;
        this.cells = cells;
        this.unitViewMap = unitViewMap;
    }


    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
        int cell = (int) (x / cells[0][0].getWidth());
        int row = (int) (y / cells[0][0].getHeight());
        GridCell gridCell = cells[cell][row];
        if (gridCell.getInnerDrawable() != null) {
            GridCellContainer innerDrawable = (GridCellContainer) gridCell.getInnerDrawable();
            Actor a = innerDrawable.hit(x, y, true);
            if (a != null && a instanceof BaseView) {
                BaseView uv = (BaseView) a;
                DC_HeroObj hero = unitViewMap.entrySet().stream()
                        .filter(entry -> entry.getValue() == uv).findFirst()
                        .get().getKey();

                Map<String, String> tooltipStatMap = new HashMap<>();
                List<ToolTipRecordOption> recordOptions = new ArrayList<>();

                tooltipStatMap.put(PARAMS.C_TOUGHNESS.getName(), "Toughness");
                tooltipStatMap.put("C_Endurance", "Endurance");
                tooltipStatMap.put("C_N_Of_Actions", "N_Of_Actions");

                tooltipStatMap.entrySet().forEach(entry -> {
                    ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                    recordOption.curVal = hero.getIntParam(entry.getKey());
                    recordOption.maxVal = hero.getIntParam(entry.getValue());
                    recordOption.name = entry.getValue();
                    recordOption.recordImage = TextureManager.getOrCreate("UI\\value icons\\" + entry.getValue().replaceAll("_", " ") + ".png");
                    recordOptions.add(recordOption);
                });

                ToolTipManager.ToolTipRecordOption recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = hero.getName();
                recordOptions.add(0, recordOption);

                recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = hero.getCoordinates().toString();
                recordOptions.add(recordOption);

                recordOption = new ToolTipManager.ToolTipRecordOption();
                recordOption.name = "direction: " + hero.getFacing().getDirection();
                recordOptions.add(recordOption);

                if (a instanceof OverlayView) {
                    recordOption = new ToolTipManager.ToolTipRecordOption();
                    recordOption.name = "LIGHT_EMISSION";
                    recordOption.curVal = hero.getIntParam(PARAMS.LIGHT_EMISSION);
                    recordOptions.add(recordOption);
                }

                GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(recordOptions));
                return true;
            }
        }
        GuiEventManager.trigger(SHOW_TOOLTIP, new EventCallbackParam(null));
        return false;
    }

            /*
            Entity e = ((Entity) obj);
            obj.isAttack();
            obj.getTargeting() instanceof SelectiveTargeting;
            obj.getTargeting().getFilter().getObjects().contains(Game.game.getCellByCoordinate(new Coordinates(0, 0)));
            obj.isMove();
            obj.isTurn();
            ((Entity) obj).getImagePath();*/

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
                    DC_HeroObj heroObj = unitViewMap.entrySet()
                            .stream().filter(entry -> entry.getValue() == unit).findFirst()
                            .get().getKey();

                    switch (event.getButton()) {
                        case Input.Buttons.RIGHT:
                            //TODO map the click to the right object in the stack?
                            GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(heroObj));
                            break;
                        default:
                            if (FAST_DC.getGameLauncher().SUPER_FAST_MODE || Gdx.input.isKeyPressed(Keys.ALT_LEFT)) {
                                GuiEventManager.trigger(SHOW_INFO_DIALOG, new EventCallbackParam(heroObj));
                            }
                    }
                }
            } else if (event.getButton() == Input.Buttons.RIGHT) {
                DC_Obj dc_cell = DC_Game.game.getCellByCoordinate(new Coordinates(cell.getGridX(), cell.getGridY()));
                GuiEventManager.trigger(CREATE_RADIAL_MENU, new EventCallbackParam(dc_cell));
            }
            event.stop();
            return true;
        }
        return false;
    }

}
