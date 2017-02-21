package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.VerticalValueContainer;
import main.libgdx.texture.TextureManager;

import java.util.ArrayList;
import java.util.List;

public class UnitInfoPanel extends TablePanel {
    public UnitInfoPanel() {
        TextureRegion textureRegion = TextureManager.getOrCreateR("/UI/components/infopanel/background.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(Gdx.graphics.getHeight());

        MainWeaponPanel mainWeaponPanel = new MainWeaponPanel();
        addElement(mainWeaponPanel);

        List<VerticalValueContainer> resourceValues = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            resourceValues.add(new VerticalValueContainer(TextureManager.getOrCreateR("UI/value icons/morale.png"), "param" + i, "146%"));
        }

        ResourcePanel resourcePanel = new ResourcePanel(resourceValues);
        addElement(resourcePanel.left().bottom().padLeft(20));

        List<ValueContainer> valueContainers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            valueContainers.add(new ValueContainer(TextureManager.getOrCreateR("/UI/abils.jpg"), "param" + i, "146%"));
        }

        MainParamPanel mainParamPanel = new MainParamPanel(valueContainers);

        addElement(mainParamPanel.left().bottom().padLeft(20));


        List<TextureRegion> abils = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            abils.add(TextureManager.getOrCreateR("/UI/abils.jpg"));
        }
        List<TextureRegion> effects = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            effects.add(TextureManager.getOrCreateR("/UI/buffs.jpg"));
        }

        EffectAndAbilitiesPanel effectAndAbilitiesPanel = new EffectAndAbilitiesPanel(abils, effects);

        addElement(effectAndAbilitiesPanel.left().bottom().padLeft(20).padBottom(20));

        addCol();

        AvatarPanel avatarPanel = new AvatarPanel(TextureManager.getOrCreateR("/UI/Empty5.jpg"), "Elf", "level 80", "code name: \"Legolas\"");
        addElement(avatarPanel);

        InitiativeAndActionPointsPanel pointsPanel = new InitiativeAndActionPointsPanel("50/50", "9999");
        addElement(pointsPanel);

        addPanelSeparator();

        List<ValueContainer> armorParams = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("param" + i, "14/88");
            valueContainer.setBorder(TextureManager.getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            armorParams.add(valueContainer);
        }

        ArmorPanel armorPanel = new ArmorPanel(armorParams);
        addElement(armorPanel);

        addPanelSeparator();

        List<ValueContainer> resists = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            resists.add(new ValueContainer(TextureManager.getOrCreateR("UI/value icons/resistance.jpg"), "146%"));
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
            ValueContainer valueContainer = new ValueContainer("param" + i, "over9000");
            valueContainer.padLeft(10);
            valueContainer.setBorder(TextureManager.getOrCreateR("UI/components/infopanel/simple_value_border.png"), true);
            statsValues.add(valueContainer);
        }

        StatsPanel statsPanel = new StatsPanel(statsValues);
        StatsPanel statsPanel2 = new StatsPanel(statsValues);
        StatsPanel statsPanel3 = new StatsPanel(statsValues);


        tabsPanel2.addTab(statsPanel, "Combat");
        tabsPanel2.addTab(statsPanel2, "Magic");
        tabsPanel2.addTab(statsPanel3, "Misc");

        tabsPanel2.resetCheckedTab();
        addElement(tabsPanel2);

        initListeners();
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
                return super.mouseMoved(event, x, y);
            }
        });
    }

    private void addPanelSeparator() {
        Container<Image> panelSeparator = new Container<>();
        Image image = new Image(TextureManager.getOrCreateR("/UI/components/infopanel/panel_separator.png"));
        panelSeparator.setActor(image);
        panelSeparator.fill().center().bottom();
        addElement(panelSeparator);
    }
}
