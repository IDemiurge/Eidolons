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
    private boolean updatePanel;
    private MainParamPanel mainParamPanel;
    private ResourcePanel resourcePanel;
    private AvatarPanel avatarPanel;
    private InitiativeAndActionPointsPanel pointsPanel;
    private EffectAndAbilitiesPanel effectAndAbilitiesPanel;
    private MainAttributesPanel attributesPanel;
    private ResistInfoTabsPanel resistTabs;
    private OffWeaponPanel offWeaponPanel;
    private MainWeaponPanel mainWeaponPanel;
    private StatsTabsPanel statsTabsPanel;

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
        mainWeaponPanel = new MainWeaponPanel();
        addElement(mainWeaponPanel);

        List<VerticalValueContainer> resourceValues = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            resourceValues.add(new VerticalValueContainer(TextureCache.getOrCreateR("UI/value icons/morale.png"), "param" + i, "146%"));
        }

        resourcePanel = new ResourcePanel();
        addElement(resourcePanel.left().bottom());

        mainParamPanel = new MainParamPanel();

        addElement(mainParamPanel.left().bottom());

        effectAndAbilitiesPanel = new EffectAndAbilitiesPanel();

        addElement(effectAndAbilitiesPanel.left().bottom());


        addCol();


        avatarPanel = new AvatarPanel();
        addElement(avatarPanel);

        pointsPanel = new InitiativeAndActionPointsPanel();
        addElement(pointsPanel);

        addPanelSeparator();

        attributesPanel = new MainAttributesPanel();
        addElement(attributesPanel.pad(10, 10, 0, 10).fill().left().bottom());

        List<ValueContainer> armorParams = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("param" + i, "14/88");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            armorParams.add(valueContainer);
        }

        ArmorPanel armorPanel = new ArmorPanel(armorParams);
        addElement(armorPanel);

        addPanelSeparator();

        resistTabs = new ResistInfoTabsPanel();
        addElement(resistTabs);

        addCol();

        offWeaponPanel = new OffWeaponPanel();
        addElement(offWeaponPanel);

        statsTabsPanel = new StatsTabsPanel();
        addElement(statsTabsPanel);
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
                //System.out.println("mouse exit form");
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, new EventCallbackParam(null));
            }
        });

        GuiEventManager.bind(GuiEventType.SHOW_UNIT_INFO_PANEL, (obj) -> {
            setUserObject(obj.get());
            outside.setTouchable(Touchable.enabled);
        });
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        //todo replace this with child.forEach
        mainParamPanel.setUserObject(userObject);
        resourcePanel.setUserObject(userObject);
        avatarPanel.setUserObject(userObject);
        pointsPanel.setUserObject(userObject);
        effectAndAbilitiesPanel.setUserObject(userObject);
        attributesPanel.setUserObject(userObject);
        resistTabs.setUserObject(userObject);
        offWeaponPanel.setUserObject(userObject);
        mainWeaponPanel.setUserObject(userObject);
        statsTabsPanel.setUserObject(userObject);
        updatePanel = true;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (updatePanel) {
            if (getUserObject() == null) {
                setVisible(false);
            } else {
                setVisible(true);

            }
            updatePanel = false;
        }
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
