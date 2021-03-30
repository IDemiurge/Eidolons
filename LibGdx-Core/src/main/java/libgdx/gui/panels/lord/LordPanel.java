package libgdx.gui.panels.lord;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.game.EidolonsGame;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.main.NF_PartyManager;
import eidolons.game.netherflame.main.death.HeroChain;
import eidolons.game.netherflame.lord.EidolonLord;
import eidolons.game.netherflame.main.soul.eidola.Soul;
import eidolons.game.netherflame.main.soul.eidola.SoulMaster;
import libgdx.gui.panels.lord.sub.SoulTabs;
import libgdx.GDX;
import libgdx.TiledNinePatchGenerator;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.anims.sprite.SpriteAnimationFactory;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.NoHitImage;
import libgdx.shaders.ShaderDrawer;
import libgdx.stage.Blocking;
import libgdx.stage.ConfirmationPanel;
import libgdx.stage.StageWithClosable;
import eidolons.content.consts.Images;
import eidolons.content.consts.Sprites;
import libgdx.texture.TextureCache;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.launch.Flags;

import java.util.List;

public class LordPanel extends GroupX implements Blocking {

    public static final boolean ON = false;
    private static final String BACKGROUND = Sprites.BG_DEFAULT;
    private static LordPanel instance;
    private static LordPanel activeInstance;
    private SoulTabs tabsRight;
    private SoulTabs tabsLeft;
    private LordView lordView;
    private Image background;

    SpriteAnimation backgroundSprite;
    private EidolonLord lord;
    private boolean initialized;

    public static boolean visibleNotNull() {
        if (activeInstance == null) {
            return false;
        }
        return instance.isVisible();
    }
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
        super();
        setSize(1920, 1050);
        instance = this;

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

            if (Eidolons.getGame().getMetaMaster().getPartyManager() instanceof NF_PartyManager) {
                update();
            }
            activeInstance = this;
            fadeIn();
        });

//        debugAll();
    }

    public void init() {
        clearChildren();
        if (Flags.isLiteLaunch()) {
            addActor(background = new Image(TextureCache.getOrCreateR(Images.BG_EIDOLONS, false, null)));
        } else {
            backgroundSprite = SpriteAnimationFactory.getSpriteAnimation(BACKGROUND);
            backgroundSprite.setOffsetX(-960);
            backgroundSprite.setOffsetY(-525);
        }
        addActor(tabsLeft = new SoulTabs(SOUL_TABS.SOULS));
        addActor(lordView = new LordView());
        addActor(tabsRight = new SoulTabs(SOUL_TABS.CHAIN));

        tabsRight.setX(getWidth()-(tabsRight.getPrefWidth()));
        lordView.setX(GDX.centerWidth(lordView));

        Texture frame = TiledNinePatchGenerator.getOrCreateNinePatch(TiledNinePatchGenerator.NINE_PATCH.FRAME,
                TiledNinePatchGenerator.BACKGROUND_NINE_PATCH.TRANSPARENT, 1920, 1050);
        addActor(new NoHitImage(frame));

        tabsLeft.tabSelected(StringMaster.format(SOUL_TABS.SOULS.name()));
        tabsRight.tabSelected(StringMaster.format(SOUL_TABS.CHAIN.name()));
//        backgroundSprite.centerOnParent();
        initialized = true;
    }

    public void update() {
        List<Soul> souls = SoulMaster.getSoulList();
        setUserObject(new LordDataSource(lord, souls, ((NF_PartyManager)
                Eidolons.getGame().getMetaMaster().getPartyManager()).getChain()));

    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        tabsRight.setUserObject(userObject);
        tabsLeft.setUserObject(userObject);
        lordView.setUserObject(userObject);
    }

    public static LordPanel getInstance() {
        if (instance == null) {
            instance = new LordPanel();
        }
        return instance;
    }

    @Override
    public void act(float delta) {
        if (!initialized){
            if (EidolonsGame.isLordPanelEnabled())
                init();
        }
        super.act(delta);
        tabsLeft.setX(350);
        tabsLeft.setY(600);
        if (Flags.isLiteLaunch()) {
            lordView.setZIndex(1);
        } else {
            lordView.setZIndex(0);
        }
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
    }

    @Override
    protected float getFadeOutDuration() {
        return 1;
    }

    @Override
    protected float getFadeInDuration() {
        return 1;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        if (backgroundSprite != null) {
            backgroundSprite.draw(batch);
        }
//        tabsRight.setY(-30);
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

    public static class LordDataSource {
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
