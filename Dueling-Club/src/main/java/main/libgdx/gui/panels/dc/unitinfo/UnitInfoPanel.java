package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

public class UnitInfoPanel extends Container<TablePanel> {
    private Actor outside;

    public UnitInfoPanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/background.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(Gdx.graphics.getHeight());

        outside = new Actor();
        outside.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        outside.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                UnitInfoPanel.this.setVisible(false);
                outside.setTouchable(Touchable.disabled);
                return false;
            }
        });

        TablePanel tablePanel = new TablePanel();
        tablePanel.fill();
        fill().left().bottom().pad(20);
        setActor(tablePanel);

        initInnerPanels();

        initListeners();

        setVisible(false);
        setClip(true);
        setTouchable(Touchable.enabled);
    }

    private void initInnerPanels() {
        MainWeaponPanel mainWeaponPanel = new MainWeaponPanel();
        addElement(mainWeaponPanel);

        List<VerticalValueContainer> resourceValues = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            resourceValues.add(new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/morale.png"), "param" + i, "146%"));
        }

        ResourcePanel resourcePanel = new ResourcePanel(resourceValues);
        addElement(resourcePanel.left().bottom());

        List<ValueContainer> valueContainers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            valueContainers.add(new ValueContainer(TextureCache.getOrCreateR("/UI/abils.jpg"), "param" + i, "146%"));
        }

        MainParamPanel mainParamPanel = new MainParamPanel(valueContainers);

        addElement(mainParamPanel.left().bottom());


        List<TextureRegion> abils = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            abils.add(TextureCache.getOrCreateR("/UI/abils.jpg"));
        }
        List<TextureRegion> effects = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            effects.add(TextureCache.getOrCreateR("/UI/buffs.jpg"));
        }

        EffectAndAbilitiesPanel effectAndAbilitiesPanel = new EffectAndAbilitiesPanel(abils, effects);

        addElement(effectAndAbilitiesPanel.left().bottom());


        addCol();


        AvatarPanel avatarPanel = new AvatarPanel(TextureCache.getOrCreateR("/UI/Empty5.jpg"), "Elf", "level 80", "code name: \"Legolas\"");
        addElement(avatarPanel);

        InitiativeAndActionPointsPanel pointsPanel = new InitiativeAndActionPointsPanel("50/50", "9999");
        addElement(pointsPanel);

        addPanelSeparator();

        List<ValueContainer> armorParams = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("param" + i, "14/88");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            armorParams.add(valueContainer);
        }

        ArmorPanel armorPanel = new ArmorPanel(armorParams);
        addElement(armorPanel);

        addPanelSeparator();

        List<ValueContainer> resists = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            resists.add(new ValueContainer(TextureCache.getOrCreateR("UI/value icons/resistance.jpg"), "146%"));
        }

        ResistPanel resistPanel = new ResistPanel(resists);
        InfoPanelTabsPanel tabsPanel = new InfoPanelTabsPanel();

        tabsPanel.addTab(resistPanel, "Resistance");
        tabsPanel.addTab(resistPanel, "Another resistance");
        tabsPanel.addTab(resistPanel, "MORE RESISTANCE");
        tabsPanel.resetCheckedTab();
        addElement(tabsPanel);

        addCol();

        OffWeaponPanel offWeaponPanel = new OffWeaponPanel();
        addElement(offWeaponPanel);

        InfoPanelTabsPanel tabsPanel2 = new InfoPanelTabsPanel();


        List<ValueContainer> statsValues = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("Combat param" + i, "9000");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
            statsValues.add(valueContainer);
        }

        //StatsPanel statsPanel = new StatsPanel(statsValues);

        List<ValueContainer> statsValues2 = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("Magic param" + i, "9000");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
            statsValues2.add(valueContainer);
        }
        //StatsPanel statsPanel2 = new StatsPanel(statsValues);

        List<ValueContainer> statsValues3 = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("Misc param" + i, "9000");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
            statsValues3.add(valueContainer);
        }
        StatsPanel statsPanel3 = new StatsPanel(statsValues, statsValues2, statsValues3);


        tabsPanel2.addTab(statsPanel3, "Combat");
/*        tabsPanel2.addTab(statsPanel2, "Magic");
        tabsPanel2.addTab(statsPanel3, "Misc");*/

        tabsPanel2.resetCheckedTab();
        addElement(tabsPanel2);
    }

    private void addCol() {
        getActor().addCol();
    }

    private <T extends Container> void addElement(T panel) {
        getActor().addElement(panel);
    }

    private void initListeners() {
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                event.stop();
                return false;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                System.out.println("mouse exit form");
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(null));
            }
        });

        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, (obj) -> {
            setVisible(true);
            outside.setTouchable(Touchable.enabled);
        });
    }

    private void addPanelSeparator() {
        Container<Image> panelSeparator = new Container<>();
        Image image = new Image(TextureCache.getOrCreateR("/UI/components/infopanel/panel_separator.png"));
        panelSeparator.setActor(image);
        panelSeparator.fill().center().bottom();
        addElement(panelSeparator);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            actor = outside;
        }
        return actor;
    }
}