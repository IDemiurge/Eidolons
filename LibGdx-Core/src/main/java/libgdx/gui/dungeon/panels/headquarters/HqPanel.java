package libgdx.gui.dungeon.panels.headquarters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.entity.unit.Unit;
import eidolons.game.core.Core;
import eidolons.netherflame.campaign.assets.NF_Images;
import libgdx.GdxMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.bf.Fluctuating;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.headquarters.datasource.HqDataMaster;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.panels.headquarters.hero.*;
import libgdx.gui.dungeon.panels.headquarters.party.HqPartyMembers;
import libgdx.gui.dungeon.panels.headquarters.tabs.HqTabs;
import libgdx.gui.dungeon.panels.headquarters.tabs.stats.HqAttributeTable;
import libgdx.gui.dungeon.panels.headquarters.tabs.stats.HqMasteryTable;
import libgdx.gui.dungeon.panels.headquarters.tabs.stats.HqNewMasteryPanel;
import libgdx.gui.dungeon.tooltips.SmartClickListener;
import libgdx.gui.dungeon.tooltips.ToolTipManager;
import libgdx.screens.handlers.ScreenMaster;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.Blocking;
import libgdx.stage.ConfirmationPanel;
import libgdx.stage.StageWithClosable;
import eidolons.content.consts.Sprites;
import libgdx.assets.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.content.values.properties.G_PROPS;
import main.system.GuiEventManager;
import main.system.launch.Flags;

import java.util.List;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqPanel extends TablePanel implements Blocking {
// Gdx revamp

    HqPartyMembers partyMembers;
    HqHeroViewPanel heroViewPanel;
    HqTabs hqTabs;
    HqHeroHeader header = new HqHeroHeader();
    HqHeroPts heroXp;
    HqVerticalValueTable heroValues;
    HqScrolledValuePanel scrolledValuePanel;
    public HqTraitsPanel traits;
    HqControlPanel controlPanel;
    HqTooltipPanel tooltipPanel;
    private TablePanel infoTable;
    private boolean editable;
    HqButtonPanel buttonPanel;
    private HqMasteryTable masteryTable;
    private HqAttributeTable attributeTable;
    private static HqPanel activeInstance;
    HqParamPanel dynamicParams;
    HqParamPanel staticParams;
    private List<HqHeroDataSource> heroes;
    private boolean initialized;
    Fluctuating fluctuating = new Fluctuating(GenericEnums.ALPHA_TEMPLATE.HQ_SPRITE);

    SpriteAnimation bgSprite;
    Image bg = new Image(TextureCache.getOrCreateR(
            NF_Images.MAIN_ART.HALL3.getPath()
    )); //variants!

    @Override
    public void draw(Batch batch, float parentAlpha) {
        bg.setPosition(-30, 0);
        bg.draw(batch, 1);
//        bg.setPosition(200, 0);
//        bg.draw(batch, 1);
        if (bgSprite != null) {
            bgSprite.draw(batch);
        }
        removeBackground();
        if (parentAlpha == ShaderDrawer.SUPER_DRAW ||
                ConfirmationPanel.getInstance().isVisible())
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
//        debugAll();
    }

    public HqPanel() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
    }

    public void init() {

        if (!Flags.isActiveTestMode())
            if (initialized || Core.getGame().isBossFight()) {
                return; //really?
            }
        if (!Flags.isLiteLaunch()) {
            bgSprite = SpriteAnimationFactory.getSpriteAnimation(Sprites.BG_DEFAULT, false, false);
            if (bgSprite != null) {
                bgSprite.setAlpha(0.4f);
                bgSprite.setFrameDuration(0.1f);
                bgSprite.setOffsetX(GdxMaster.getWidth() / 2);
                bgSprite.setOffsetY(GdxMaster.getHeight() / 2);
            }
        }
        initialized = true;
//        setBackground(NinePatchFactory.getHqDrawable());
        tooltipPanel = new HqTooltipPanel();
        partyMembers = createPartyMembers();
        hqTabs = createTabs();
        heroViewPanel = new HqHeroViewPanel();
        addActor(header);
        header.setPosition(GdxMaster.centerWidth(header), GdxMaster.getTopY(header));
        heroXp = new HqHeroPts();
        heroValues = new HqVerticalValueTable(PARAMS.LEVEL, G_PROPS.RACE, G_PROPS.DEITY);
        dynamicParams = new HqParamPanel(true);
        staticParams = new HqParamPanel(false);
        traits = new HqTraitsPanel();
        controlPanel = new HqControlPanel();
        buttonPanel = new HqButtonPanel();
        infoTable = createInfoTable();
        addElements();
        addListener(new SmartClickListener(this) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Actor actor = hit(x, y, true);
                if (actor == HqPanel.this) {
                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                    super.clicked(event, x, y);
                }
            }
        });
//        debugAll();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        Actor actor = super.hit(x, y, touchable);
        if (actor == null)
            if (isVisible())
                return this;
        return actor;
    }

    public static void setActiveInstance(HqPanel activeInstance) {
        HqPanel.activeInstance = activeInstance;
    }

    public static HqPanel getActiveInstance() {
        return activeInstance;
    }


    @Override
    public void act(float delta) {
        if (bgSprite != null) {
            fluctuating.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HQ_SPRITE);
            fluctuating.fluctuate(delta);
            bgSprite.setAlpha(fluctuating.getColor().a);
        }
        if (Core.getGame().isBossFight()) {
            return;
        }
        addActor(header);
        header.setPosition(130, GdxMaster.getTopY(header) - 75);
        header.setVisible(true);
        if (!ScreenMaster.isFullscreen())
            setX(-60);

        super.act(delta);
    }

    private void addElements() {
        left();
        row();

        add(heroViewPanel).left().padRight(20).width(565);

        add(infoTable).center(); //.padLeft(65);
        add(hqTabs);
        row();

        add(buttonPanel).colspan(3);
    }


    private TablePanel createInfoTable() {
        infoTable = new TablePanel<>();
        infoTable.top();
        infoTable.add(heroValues).left();
        infoTable.add(heroXp).right().row();
        infoTable.add(dynamicParams).center().colspan(2).row();
        infoTable.add(staticParams).center().colspan(2).row();

        masteryTable = new HqMasteryTable();
        attributeTable = new HqAttributeTable();

        infoTable.add(attributeTable).left().top().padLeft(30);
        //separator
        infoTable.add(masteryTable).right().top().row();
        infoTable.add(traits).center().colspan(2).row(); //TODO OVER THE PORTRAIT BOTTOM
        infoTable.add(tooltipPanel).center().colspan(2).row();
        attributeTable.setEditable(isEditable());

        HqNewMasteryPanel newMastery = new HqNewMasteryPanel();
//        newMastery.setPosition();
        infoTable.addActor(newMastery);

        masteryTable.setEditable(isEditable());
        if (isScrollValuesOn()) {
            infoTable.row();
            infoTable.addActor(scrolledValuePanel = new HqScrolledValuePanel());
        }

        infoTable.row();
//        infoTable.add(controlPanel).padTop(100).bottom().center().colspan(2).row();

        infoTable.setFixedSize(true);
        infoTable.setSize(400, 800);
        return infoTable;
    }

    private boolean isScrollValuesOn() {
        return false;
    }

    private HqTabs createTabs() {
        return new HqTabs();
    }

    private HqPartyMembers createPartyMembers() {
        return new HqPartyMembers(this, isVerticalPartyMembers());
    }

    private boolean isVerticalPartyMembers() {
        return false;
    }

    public void memberSelected(HqHeroDataSource source) {
        setUserObject(source);
    }

    public void memberSelected(Unit hero) {
        memberSelected(HqDataMaster.getHeroDataSource(hero));
    }

    public HqHeroDataSource getSelectedHero() {
        return getUserObject();
    }

    @Override
    public void close() {
//        ActorMaster.addFadeOutAction(this, 0.3f );
//        ActorMaster.addHideAfter(this );
//        GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
        HqMaster.closeHqPanel();

    }

    public void closed() {
        HqPanel.setActiveInstance(null);
        ToolTipManager.setTooltipPanel(null);
        getStageWithClosable().closeClosable(this);

        for (HqHeroDataSource sub : heroes) {
            sub.getEntity().getModificationList().clear();
        }
    }

    @Override
    public void open() {
        HqPanel.setActiveInstance(this);
        getStageWithClosable().openClosable(this);
        ToolTipManager.setTooltipPanel(tooltipPanel);
//        hqTabs.tabSelected("Class");
//        hqTabs.tabSelected("Class");
//        hqTabs.tabSelected("Class");
//        hqTabs.tabSelected("Class");///aaaaaaaaaaaaa the default pos won't fix
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) super.getStage();
    }

    public List<HqHeroDataSource> getHeroes() {
        return heroes;
    }

    @Override
    public void setUserObject(Object userObject) {
        clear();
        addElements();

        if (userObject instanceof List) {
            ((List) userObject).removeIf((h) -> {
                if (h instanceof HqHeroDataSource) {
                    return !((HqHeroDataSource) h).getName().equalsIgnoreCase(Core.getMainHero().getName());
                }
                return false;
            });
        }
        if (userObject instanceof List) {
            heroes = (List<HqHeroDataSource>) userObject;
            userObject = ((List) userObject).get(0);
        }
        if (userObject instanceof HqHeroDataSource) {
            HqHeroDataSource source = (HqHeroDataSource) userObject;
            source.setEditable(editable);
            HqMaster.setActiveHero(source.getEntity().getHero());
        }
        super.setUserObject(userObject);

        partyMembers.setUserObject(heroes);
        partyMembers.setUpdateRequired(true);

        header.setUserObject(userObject);
        header.updateAct(1f);

    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        masteryTable.setEditable(isEditable());
        attributeTable.setEditable(isEditable());
    }

    public void modelChanged() {
        setUserObject(getUserObject());
    }
}
