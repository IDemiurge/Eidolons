package eidolons.system.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.system.options.Options.OPTION;
import eidolons.system.options.OptionsMaster.OPTIONS_GROUP;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ListMaster;

import java.util.Map;

/**
 * Created by JustMe on 4/2/2018.
 */
public class OptionsWindow extends VisWindow {
    Stage stage;
    Table root;
    Array<OptionsTab> tabs = new Array<>();
    private Map<OPTIONS_GROUP, Options> optionsMap;
    private static OptionsWindow instance;

    private OptionsWindow(
    ) {
        super("Options", new WindowStyle(StyleHolder.getDefaultLabelStyle().font
         , StyleHolder.getDefaultLabelStyle().fontColor,
         new NinePatchDrawable(NinePatchFactory.getTooltip())
        ));
         setVisible(false);
        setSize(800, 600);
        closeOnEscape();

    }

    public static OptionsWindow getInstance() {
        if (instance == null) {
            instance = new OptionsWindow();
        }
        return instance;
    }

    public static void setInstance(OptionsWindow instance) {
        OptionsWindow.instance = instance;
    }

    public void open(Map<OPTIONS_GROUP, Options> optionsMap
     , Stage stage) {
        this.optionsMap = optionsMap;
        this.stage = stage;
        setVisible(true);

        clearChildren();
        root = new Table();
        root.setFillParent(true);
        add(root);
        Table content = init();
        root.add(content).expand().fill().row();
        stage.addActor(this);
        root.setPosition(GdxMaster.centerWidth(root),
         GdxMaster.centerHeight(root));
        centerWindow();
        center();
        fadeIn();
    }

    @Override
    protected void close() {
        super.close();
    }

    private Table init() {
        tabs = new Array<>();
        TabbedPane optionsPane = new TabbedPane();
        // tabs
        add(optionsPane.getTable()).expandX().fillX().row();
        // tab content
        final Table content = new Table();

        optionsPane.addListener(new TabbedPaneAdapter() {
            @Override
            public void switchedTab(Tab tab) {
                content.clear();
                content.add(tab.getContentTable()).expand().top().left();
            }
        });

        for (OPTIONS_GROUP group : optionsMap.keySet()) {
            OptionsTab tab = new OptionsTab(group);
            tabs.add(tab);
            optionsPane.add(tab);
        }

        optionsPane.switchTab(0);

        Table bottomMenu = new Table();
        bottomMenu.defaults().pad(4);
        add(bottomMenu).expandX().left();
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

        return content;
    }

    private void toDefaults() {
        OptionsMaster.resetToDefaults();
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
        Gdx.app.log("Options", "apply");
        OptionsMaster.cacheOptions();
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
        Gdx.app.log("Options", "cancel");
        OptionsMaster.resetToCached();
        close();
    }

    class OptionsTab extends Tab {
        String title;
        Table content;
        ObjectMap<OPTION, OptionActor> optionActors = new ObjectMap<>();

        public OptionsTab(OPTIONS_GROUP group) {
            super(false, false);
            this.title = StringMaster.getWellFormattedString(group.toString());
            Options options = optionsMap.get(group);
            content = new Table();
            content.defaults().pad(4);
            final Map values = options.getValues();
            for (Object v : values.keySet()) {
                final OPTION option = options.getKey(v.toString());
                if (option == null)
                    continue;
                VisLabel label = new VisLabel(option.getName());
                content.add(label).left();
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
                    }
                    break;
                    default: {
                        Gdx.app.log("Options", "unknown option type:" + optionType);
                    }
                }
                content.row();
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

