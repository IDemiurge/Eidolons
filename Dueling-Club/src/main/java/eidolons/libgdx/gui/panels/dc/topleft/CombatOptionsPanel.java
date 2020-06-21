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
import eidolons.system.options.ControlOptions;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.Options.OPTION;
import eidolons.system.options.OptionsMaster;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.graphics.FontMaster;
import main.system.launch.CoreEngine;

import java.util.LinkedList;
import java.util.List;

public class CombatOptionsPanel extends TablePanelX {
//TODO control options too - e.g. inverts, wasd, clicks,...

    public static final OPTION[] DEV_OPTIONS = {
            GameplayOptions.GAMEPLAY_OPTION.IMMORTALITY,
            GameplayOptions.GAMEPLAY_OPTION.GHOST_MODE,
            GameplayOptions.GAMEPLAY_OPTION.AI_DEBUG,
            GameplayOptions.GAMEPLAY_OPTION.DEBUG_MODE,
            GraphicsOptions.GRAPHIC_OPTION.SHADOW_MAP_OFF,
            GraphicsOptions.GRAPHIC_OPTION.SHARDS_OFF,
            GameplayOptions.GAMEPLAY_OPTION.MANUAL_CONTROL,
//            GameplayOptions.TESTING_OPTION.DEBUG_MODE,
//            GameplayOptions.TESTING_OPTION.DEBUG_MODE,
    };
    public static final OPTION[] OPTIONS_EXPLORE = {
            ControlOptions.CONTROL_OPTION.ALT_MODE_ON,
            ControlOptions.CONTROL_OPTION.CAMERA_ON_ACTIVE,
            ControlOptions.CONTROL_OPTION.AUTO_CAMERA_OFF,
            ControlOptions.CONTROL_OPTION.CAMERA_ON_HERO,
    };
    public static final OPTION[] OPTIONS = {
            ControlOptions.CONTROL_OPTION.ALT_MODE_ON,
            GameplayOptions.GAMEPLAY_OPTION.INPUT_BETWEEN_TURNS,
//            GameplayOptions.GAMEPLAY_OPTION.SPACE_BETWEEN_TURNS,
            ControlOptions.CONTROL_OPTION.CAMERA_ON_ACTIVE,
            ControlOptions.CONTROL_OPTION.AUTO_CAMERA_OFF,
            ControlOptions.CONTROL_OPTION.CAMERA_ON_HERO,
    };
    private final RollDecorator.RollableGroup decorated;

    public void hide() {
        if (decorated.isOpen()) {
            decorated.toggle(false);
        }
    }
    public void show() {
        if (!decorated.isOpen()) {
            decorated.toggle(true);
        }
    }

    public CombatOptionsPanel() {
        GDX.loadVisUI();
        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_ON , p-> hide() );
        GuiEventManager.bind(GuiEventType.MINIMIZE_UI_OFF , p-> show() );
        List<OPTION> list = new LinkedList<>();
        for (OPTION devOption : OPTIONS) {
            list.add(devOption);
        }
        if (CoreEngine.TEST_LAUNCH) {
//            if (CoreEngine.isLogicTest()){
            for (OPTION devOption : DEV_OPTIONS) {
                list.add(devOption);
            }
        }
        final int height = 100 + list.size() * 40;
        TablePanelX<Actor> table = new TablePanelX(200, height) {
            @Override
            public float getPrefHeight() {
                return height;
            }
        };
        table.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        table.defaults().height(40);
        setSize(200, height);
        //can we add same comps as in OptionWindow?
        addOptionBoxes(table, list);

        add(decorated = RollDecorator.decorate(table, FACING_DIRECTION.NORTH, true,
                (ButtonStyled.STD_BUTTON.UP)));

    }

    private void addOptionBoxes(TablePanelX<Actor> table, List<OPTION> list) {
        new OptionCheckBox(list.get(0)); //init style ...
        for (OPTION option : list) {
            table.add(new OptionCheckBox(option)).left().uniform().row();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public static class OptionCheckBox extends VisCheckBox {
        private final OPTION option;

        public OptionCheckBox(OPTION option) {
            super(option.getName());
            this.option = option;
            addCheckListener();
            addListener(new ValueTooltip(option.getTooltip()).getController());

            VisCheckBox.VisCheckBoxStyle style = getStyle();
            style.font = StyleHolder.getSizedLabelStyle(FontMaster.FONT.MAIN, 17).font;
            setStyle(style);
        }

        private void addCheckListener() {
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    OptionsMaster.setOption(option, isChecked(), true);
                }
            });
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            boolean value = OptionsMaster.getOptionsByConst(option).getBooleanValue((Enum) option);
            if (isChecked() != value) {
                clearListeners();
                OptionsMaster.setOption(option, isChecked(), true);
                addCheckListener();
            }
        }
    }
}
