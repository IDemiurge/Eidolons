package main.level_editor.gui.top;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import eidolons.game.core.Core;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.panels.TablePanelX;
import main.level_editor.LevelEditor;

public class LE_ButtonStripe extends HorizontalFlowGroup {

    SymbolButton controlPanel;
    SymbolButton palettePanel;
    SymbolButton structurePanel;
    SymbolButton brushes;
    SymbolButton viewModes;
    SymbolButton save;
    SymbolButton saveV;
    SymbolButton AV;

    SymbolButton undo;

    public LE_ButtonStripe() {
        super(10);
        setHeight(80);
        setWidth(900);
        // addActor(AV = new SymbolButton(ButtonStyled.STD_BUTTON.LE_AV, ()-> LE_AvIntegration.openAvWindow()));
        addActor(undo = new SymbolButton(ButtonStyled.STD_BUTTON.LE_UNDO, () ->
                Core.onGdxThread(() -> {
                    try {
                        LevelEditor.getCurrent().getManager().getOperationHandler().undo();
                    } catch (Exception e) {
                        main.system.ExceptionMaster.printStackTrace(e);
                    }
                })));
        TablePanelX<Actor> container = new TablePanelX(80, 60) {
            @Override
            public void layout() {
                super.layout();
                save.setY(save.getY() + 20);
                //                saveV.setY(saveV.getY()+20);
            }
        };
        container.setBackground(NinePatchFactory.getLightPanelFilledDrawable());
        addActor(container);
        container.add(
                save = new SymbolButton(ButtonStyled.STD_BUTTON.REPAIR, () ->
                        Core.onNonGdxThread(() -> LevelEditor.getCurrent().getManager().getDataHandler().saveFloor()))).top();
        container.add(
                saveV = new SymbolButton(ButtonStyled.STD_BUTTON.CHEST, () ->
                        Core.onGdxThread(() -> LevelEditor.getCurrent().getManager().
                                getDataHandler().saveVersion()))).top();
        //        addActor(new TablePanelX<>(40, getHeight()));
        addActor(controlPanel = new SymbolButton(ButtonStyled.STD_BUTTON.LE_CTRL, null));
        addActor(palettePanel = new SymbolButton(ButtonStyled.STD_BUTTON.LE_PALETTE, null));
        addActor(structurePanel = new SymbolButton(ButtonStyled.STD_BUTTON.LE_STRUCT, null));

        addActor(brushes = new SymbolButton(ButtonStyled.STD_BUTTON.LE_BRUSH, () -> {
            boolean b = LevelEditor.getModel().isBrushMode();
            LevelEditor.getModel().setBrushMode(!b);
            //            if (getStage() instanceof LE_GuiStage) {
            //                ((LE_GuiStage) getStage()).toggleUiVisible();
            //            }
        }));
        addActor(viewModes = new SymbolButton(ButtonStyled.STD_BUTTON.LE_VIEWS, () -> {
            LevelEditor.getModel().getDisplayMode().toggleAll();
        }));
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        brushes.setChecked(LevelEditor.getModel().isBrushMode());
    }

    public SymbolButton getControlPanel() {
        return controlPanel;
    }

    public SymbolButton getPalettePanel() {
        return palettePanel;
    }

    public SymbolButton getStructurePanel() {
        return structurePanel;
    }

    public SymbolButton getBrushes() {
        return brushes;
    }

    public SymbolButton getViewModes() {
        return viewModes;
    }
}
