package eidolons.libgdx.gui.panels.dc.topleft;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import eidolons.libgdx.GDX;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.RollDecorator;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.ValueTooltip;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.Options;
import eidolons.system.options.OptionsMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;

import java.util.LinkedList;
import java.util.List;

public class CombatOptionsPanel extends TablePanelX {
//TODO control options too - e.g. inverts, wasd, clicks,...

    public static final GameplayOptions.GAMEPLAY_OPTION[] DEV_OPTIONS={
            GameplayOptions.GAMEPLAY_OPTION.IMMORTALITY,
            GameplayOptions.GAMEPLAY_OPTION.MANUAL_CONTROL,

    };
    public static final GameplayOptions.GAMEPLAY_OPTION[] OPTIONS={
            GameplayOptions.GAMEPLAY_OPTION.INPUT_BETWEEN_TURNS,
            GameplayOptions.GAMEPLAY_OPTION.SPACE_BETWEEN_TURNS,
    };

    public CombatOptionsPanel() {
        GDX.loadVisUI();
        List<Options.OPTION> list = new LinkedList<>();
        for (GameplayOptions.GAMEPLAY_OPTION devOption : OPTIONS) {
            list.add(devOption);
        }
        if (CoreEngine.isIDE()){
//            if (CoreEngine.isLogicTest()){
            for (GameplayOptions.GAMEPLAY_OPTION devOption : DEV_OPTIONS) {
                list.add(devOption);
            }
        }
        TablePanelX<Actor> table = new TablePanelX (200, 400+list.size()*40){
            @Override
            public float getPrefHeight() {
                return 400+list.size()*40;
            }
        };
        table. setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        table.defaults().height(40);
        setSize(200, 400 + list.size() * 40);
        //can we add same comps as in OptionWindow?
        addOptionBoxes(table, list);

        add(RollDecorator.decorate(table, FACING_DIRECTION.NORTH, true,
                (ButtonStyled.STD_BUTTON.UP)));

    }

    private void addOptionBoxes(TablePanelX<Actor> table, List<Options.OPTION> list) {
        for (Options.OPTION option : list) {
            table.add(new OptionCheckBox(option)).left().uniform().row();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public static class OptionCheckBox extends VisCheckBox {
        private final Options.OPTION option;

        public OptionCheckBox(Options.OPTION option) {
            super(option.getName());
            this.option = option;
            addCheckListener();
            addListener(new ValueTooltip(option.getTooltip()).getController());

            VisCheckBox.VisCheckBoxStyle style =  getStyle();
            style.font = StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 17).font;
            setStyle(style);
        }

        private void addCheckListener() {
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    OptionsMaster.setOption(option , isChecked(), true);
                }
            });
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            boolean value = OptionsMaster.getOptionsByConst(option).getBooleanValue((Enum) option);
            if (isChecked() != value){
                clearListeners();
                OptionsMaster.setOption(option , isChecked(), true);
                addCheckListener();
            }
        }
    }
}
