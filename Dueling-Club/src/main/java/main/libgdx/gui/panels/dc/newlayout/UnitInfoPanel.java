package main.libgdx.gui.panels.dc.newlayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.unitinfo.*;
import main.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

public class UnitInfoPanel extends NewTable {
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
        setBackground(drawable);
        init(new float[]{36, 28, 36}, 1, AlignW.LEFT, AlignH.TOP);

        setSize(textureRegion.getRegionWidth(), Gdx.graphics.getHeight());

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


        initInnerPanels();

        initListeners();

        setVisible(false);
        setTouchable(Touchable.enabled);
    }

    private void initInnerPanels() {
        NewTable left = new NewTable(new float[]{100}, 5, AlignW.LEFT, AlignH.BOTTOM);
        left.setName("LEFT");
        addAt(0, 0, left).setH(getHeight());

        NewTable mid = new NewTable(new float[]{100}, 7, AlignW.CENTER, AlignH.TOP);
        mid.setName("MID");
        addAt(1, 0, mid).setH(getHeight());

        NewTable right = new NewTable(new float[]{100}, 3, AlignW.CENTER, AlignH.CENTER);
        right.setName("RIGHT");
        addAt(2, 0, right).setH(getHeight());

        mainWeaponPanel = new MainWeaponPanel();
        mainWeaponPanel.debug();
        left.addAt(0, 1, mainWeaponPanel);

        resourcePanel = new ResourcePanel();
        left.addAt(0, 2, resourcePanel);

        mainParamPanel = new MainParamPanel();
        left.addAt(0, 3, mainParamPanel);

        effectAndAbilitiesPanel = new EffectAndAbilitiesPanel();
        left.addAt(0, 4, mainParamPanel);
        //------------------------------

        avatarPanel = new AvatarPanel();
        mid.addAt(0, 0, avatarPanel).setH(262);

        pointsPanel = new InitiativeAndActionPointsPanel();
        mid.addAt(0, 1, avatarPanel).setH(60);

        mid.addAt(0, 2, getPanelSeparator()).setH(30);

        attributesPanel = new MainAttributesPanel();
        mid.addAt(0, 3, attributesPanel).setH(60);

        List<ValueContainer> armorParams = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ValueContainer valueContainer = new ValueContainer("param" + i, "14/88");
            valueContainer.setBorder(TextureCache.getOrCreateR("UI/components/infopanel/simple_value_border.png"));
            armorParams.add(valueContainer);
        }

        ArmorPanel armorPanel = new ArmorPanel(armorParams);
        mid.addAt(0, 4, armorPanel);

        mid.addAt(0, 5, getPanelSeparator()).setH(30);

        resistTabs = new ResistInfoTabsPanel();
        mid.addAt(0, 6, resistTabs);


        offWeaponPanel = new OffWeaponPanel();
        right.addAt(0, 1, offWeaponPanel);

        statsTabsPanel = new StatsTabsPanel();
        right.addAt(0, 2, statsTabsPanel);

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

    private Container<Image> getPanelSeparator() {
        Container<Image> panelSeparator = new Container<>();
        Image image = new Image(TextureCache.getOrCreateR("/UI/components/infopanel/panel_separator.png"));
        panelSeparator.setActor(image);
        panelSeparator.fill().center().bottom();
        return panelSeparator;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
