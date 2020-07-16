package eidolons.system.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.actions.ActionMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.options.Options.OPTION;
import eidolons.system.options.OptionsMaster.OPTIONS_GROUP;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.launch.Flags;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.util.Map;

/**
 * Created by JustMe on 4/2/2018.
 */
public class OptionsWindow extends VisWindow {
    private static OptionsWindow instance;
    private static boolean active;
    Stage stage;
    Array<OptionsTab> tabs = new Array<>();
    private Map<OPTIONS_GROUP, Options> optionsMap;

    private OptionsWindow(
    ) {
        super("Options", new WindowStyle(StyleHolder.getHqLabelStyle(
         GdxMaster.adjustFontSize(20)).font
         , StyleHolder.getDefaultLabelStyle().fontColor,
         new NinePatchDrawable(NinePatchFactory.getLightDecorPanelFilledDrawable())
        ));

        setVisible(false);
        setSize(GdxMaster.adjustSize(800), GdxMaster.adjustSize(600));
        pad(GdxMaster.adjustSize(12));
        closeOnEscape();
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().pack();
        getTitleLabel().setY(getTitleLabel().getY()-getTitleLabel().getHeight()/2);

    }

    public static boolean isActive() {
        return active;
    }

    public static OptionsWindow getInstance() {
        if (instance == null) {
            GDX.loadVisUI();
            instance = new OptionsWindow();
        }
        return instance;
    }



    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram shader = batch.getShader();
        batch.setShader(null);
        super.draw(batch, parentAlpha);
        batch.setShader(shader);
    }

    public static void setInstance(OptionsWindow instance) {
        OptionsWindow.instance = instance;
    }

    public void open(Map<OPTIONS_GROUP, Options> optionsMap
     , Stage stage) {
        GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
        this.optionsMap = optionsMap;
        this.stage = stage;
        setVisible(true);
        clearChildren();
        Actor content = init();
        add(content).expand().fill().center().top().row();
        Table bottomMenu = createBottomPanel();
        add(bottomMenu).expandX().left().bottom();

        stage.addActor(this);
        centerWindow();
        center();
        fadeIn();
        active = true;
    }

    public void forceClose() {
        close();
    }
    @Override
    protected void close() {
//        super.close();
        active = false;
        ActionMaster.addFadeOutAction(this, 1, false);
        ActionMaster.addHideAfter(this );
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
    }

    private Table init() {
        tabs = new Array<>();
        TabbedPane optionsPane = new TabbedPane();
        final Table content = new Table();

        add(optionsPane.getTable()).expandX().fillX().row();
        optionsPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                content.clearChildren();
                content.add(tab.getContentTable()).expand().top().left();
                DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__TAB);
            }
        });

        for (OPTIONS_GROUP group : optionsMap.keySet()) {
            OptionsTab tab = new OptionsTab(group);
            tabs.add(tab);
            optionsPane.add(tab);
        }

        optionsPane.switchTab(0);

        return content;
    }

    private Table createBottomPanel() {
        Table bottomMenu = new Table();
        bottomMenu.defaults().pad(4);
        {
            VisTextButton button = new VisTextButton("Ok");
            addClickListener(button, this::ok);
            bottomMenu.add(button);
        }
        {
            VisTextButton button = new VisTextButton("Save");
            addClickListener(button, this::save);
            bottomMenu.add(button);
        }
        {
            VisTextButton button = new VisTextButton("Apply");
            addClickListener(button, this::apply);
            bottomMenu.add(button);
        }
        {
            VisTextButton button = new VisTextButton("Cancel");
            addClickListener(button, this::cancel);
            bottomMenu.add(button);
        }
        {
            VisTextButton button = new VisTextButton("Defaults");
            addClickListener(button, this::toDefaults);
            bottomMenu.add(button);
        }
        return bottomMenu;
    }


    private void toDefaults() {
        OptionsMaster.getInstance().resetToDefaults();
        for (OptionsTab tab : tabs) {
            tab.refresh();
        }
        Gdx.app.log("Options", "defaults");
    }

    private void addClickListener(VisTextButton button, Runnable run) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                run.run();
            }
        });
    }

    private void ok() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__OK);
        Gdx.app.log("Options", "ok");
        apply();
        close();
    }

    private void save() {
        Gdx.app.log("Options", "save");
        apply();
        OptionsMaster.saveOptions();
    }

    private void apply() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__CLICK);
        Gdx.app.log("Options", "apply");
        OptionsMaster.getInstance().cacheOptions();
        for (OptionsTab tab : tabs) {
            tab.apply();
        }
        try {
            OptionsMaster.applyOptions();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    private void cancel() {
        DC_SoundMaster.playStandardSound(STD_SOUNDS.NEW__CLICK_DISABLED);
        Gdx.app.log("Options", "cancel");
        OptionsMaster.getInstance().resetToCached();
        close();
    }

    class OptionsTab extends Tab {
        String title;
        Table content;
        ObjectMap<OPTION, OptionActor> optionActors = new ObjectMap<>();

        public OptionsTab(OPTIONS_GROUP group) {
            super(false, false);
            this.title = StringMaster.format(group.toString());
            Options options = optionsMap.get(group);
            content = new Table();
            content.defaults().pad(GdxMaster.adjustSize(8));
            final Map values = options.getValues();

            int columns=1;
            if (values.size()>GdxMaster.adjustHeight(14)) {
                columns=2;
            }
            int n =0;
            for (Object v : values.keySet()) {
                final OPTION option = options.getKey(v.toString());
                if (option == null)
                    continue;
                if (option.isHidden())
                    continue;
                if (option.isDevOnly())
                    if (!Flags.isIDE())
                        continue;
                VisLabel label = new VisLabel(option.getName());
                content.add(label).left();
                label.setStyle(StyleHolder.getSizedLabelStyle(FONT.NYALA, 16));
                String optionType = options.getValueClass(option).getSimpleName();
                final String optionStr = option.toString();
                switch (optionType) {
                    case "String": { // aka combo box
                        Object[] optionValues = option.getOptions();
                        String[] strings = ListMaster.toStringList(optionValues).toArray(new String[optionValues.length]);
                        String selected = options.getValue(optionStr);

                        final VisSelectBox<String> selectBox = new VisSelectBox<>();
                        content.add(selectBox);
                        selectBox.setItems(strings);
                        selectBox.setSelected(selected);
                        selectBox.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                Gdx.app.log("Options", option + " -> " + selectBox.getSelected());
                            }
                        });
                        optionActors.put(option, new OptionActor() {
                            @Override
                            void apply() {
                                options.setValue(optionStr, selectBox.getSelected());
                            }

                            @Override
                            void refresh() {
                                selectBox.setSelected(options.getValue(optionStr));
                            }
                        });
                        selectBox.getStyle().font=label.getStyle().font;
                    }
                    break;
                    case "Integer": { // aka slider
                        int min = option.getMin();
                        int max = option.getMax();
                        int current = options.getIntValue(optionStr);
                        final VisSlider slider = new VisSlider(min, max, 1, false);
                        content.add(slider);
                        slider.setValue(current);
                        slider.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                int value = (int) slider.getValue();
                                Gdx.app.log("Options", option + " -> " + value);
                            }
                        });
                        optionActors.put(option, new OptionActor() {
                            @Override
                            void apply() {
                                int value = (int) slider.getValue();
                                options.setValue(optionStr, String.valueOf(value));
                            }

                            @Override
                            void refresh() {
                                slider.setValue(options.getIntValue(optionStr));
                            }
                        });
                        slider.setScale(GdxMaster.adjustFontSize(1));
                    }
                    break;
                    case "Boolean": { // aka checkbox
                        boolean checked = options.getBooleanValue((Enum) option);
                        VisCheckBox checkBox = new VisCheckBox("", checked);
                        content.add(checkBox);
                        checkBox.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                Gdx.app.log("Options", option + " -> " + checkBox.isChecked());
                            }
                        });
                        optionActors.put(option, new OptionActor() {
                            @Override
                            void apply() {
                                boolean checked = checkBox.isChecked();
                                options.setValue(optionStr, String.valueOf(checked));
                            }

                            @Override
                            void refresh() {
                                checkBox.setChecked(options.getBooleanValue((Enum) option));
                            }
                        });
                        checkBox.getStyle().font=label.getStyle().font;
                    }
                    break;
                    default: {
                        Gdx.app.log("Options", "unknown option type:" + optionType);
                    }
                }
                n++;
                if (n>=columns){
                    content.row();
                    n=0;
                }
            }
        }

        @Override
        public String getTabTitle() {
            return title;
        }

        @Override
        public Table getContentTable() {
            return content;
        }

        public void apply() {
            for (ObjectMap.Entry<OPTION, OptionActor> optionActor : optionActors) {
                optionActor.value.apply();
            }
        }

        public void refresh() {
            for (ObjectMap.Entry<OPTION, OptionActor> optionActor : optionActors) {
                optionActor.value.refresh();
            }
        }

        abstract class OptionActor {
            abstract void apply();

            abstract void refresh();
        }
    }


}

