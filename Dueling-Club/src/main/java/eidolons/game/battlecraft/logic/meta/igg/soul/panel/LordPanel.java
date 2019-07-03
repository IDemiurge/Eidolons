package eidolons.game.battlecraft.logic.meta.igg.soul.panel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.igg.IGG_PartyManager;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.death.HeroChain;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.Soul;
import eidolons.game.battlecraft.logic.meta.igg.soul.eidola.SoulMaster;
import eidolons.game.battlecraft.logic.meta.igg.soul.panel.sub.SoulTabs;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.shaders.ShaderDrawer;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.Closable;
import eidolons.libgdx.stage.ConfirmationPanel;
import eidolons.libgdx.stage.StageWithClosable;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.CoreEngine;

import java.util.List;

public class LordPanel extends TablePanelX implements Blocking {

    private static final String BACKGROUND = Sprites.BG_DEFAULT;
    private static LordPanel instance;
    private static LordPanel activeInstance;
    private SoulTabs tabsRight;
    private SoulTabs tabsLeft;
    private LordView lordView;
    private Image background;

    SpriteAnimation backgroundSprite;
    private EidolonLord lord;

    public static LordPanel getActiveInstance() {
        return activeInstance;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return background;
        }
        return actor;
    }

    private LordPanel() {
        super(1920, 1050);
        instance = this;

        if (CoreEngine.isLiteLaunch()) {
            addActor(background = new Image(TextureCache.getOrCreate(Images.BG_EIDOLONS)));
        } else {
            backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(BACKGROUND);
            backgroundSprite.setOffsetX(-960);
            backgroundSprite.setOffsetY(-525);
        }
        add(tabsLeft = new SoulTabs(SOUL_TABS.SOULS));
        add(lordView = new LordView());
        add(tabsRight = new SoulTabs(SOUL_TABS.CHAIN));

        Texture frame = TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.FRAME,
                TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT, 1920, 1050);
        addActor(new NoHitImage(frame));

        tabsLeft.tabSelected(StringMaster.getWellFormattedString(SOUL_TABS.SOULS.name()));
        tabsRight.tabSelected(StringMaster.getWellFormattedString(SOUL_TABS.CHAIN.name()));
//        backgroundSprite.centerOnParent();

        GuiEventManager.bind(GuiEventType.UPDATE_LORD_PANEL, p -> {
            update();
        });
        GuiEventManager.bind(GuiEventType.SHOW_LORD_PANEL, p -> {
            if (p.get() == null) {
                fadeOut();
                activeInstance = null;
                return;
            }
            lord = (EidolonLord) p.get();

            if (Eidolons.getGame().getMetaMaster().getPartyManager() instanceof IGG_PartyManager) {
                update();
            }
            activeInstance = this;
            fadeIn();
        });

//        debugAll();
    }

    @Override
    public void update() {
        List<Soul> souls = SoulMaster.getSoulList();
        setUserObject(new LordDataSource(lord, souls, ((IGG_PartyManager)
                Eidolons.getGame().getMetaMaster().getPartyManager()).getChain()));

    }

    public static LordPanel getInstance() {
        if (instance == null) {
            instance = new LordPanel();
        }
        return instance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (CoreEngine.isLiteLaunch()) {
            lordView.setZIndex(1);
        } else {
            lordView.setZIndex(0);
        }
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        if (backgroundSprite != null) {
            backgroundSprite.draw(batch);
        }
        tabsLeft.setY(-30);
        tabsRight.setY(-30);
        if (parentAlpha == ShaderDrawer.SUPER_DRAW ||
                ConfirmationPanel.getInstance().isVisible())
            super.draw(batch, 1);
        else
            ShaderDrawer.drawWithCustomShader(this, batch, null);
//        super.draw(batch, parentAlpha);
    }

    @Override
    public StageWithClosable getStageWithClosable() {
        return (StageWithClosable) getStage();
    }

    public class LordDataSource {
        private final EidolonLord lord;
        List<Soul> souls;
        HeroChain chain;

        public List<Soul> getSouls() {
            return souls;
        }

        public EidolonLord getLord() {
            return lord;
        }

        public HeroChain getChain() {
            return chain;
        }


        public LordDataSource(EidolonLord lord, List<Soul> souls, HeroChain chain) {
            this.lord = lord;
            this.souls = souls;
            this.chain = chain;
        }
    }

    public enum SOUL_TABS {
        CHAIN, SOULS, MEMORIES,

        LORD,
        FLAME, //IDEA - use souls to quench Affliction slots!
        //        GODS,
        STATS, //outta place, but ?
    }
}
