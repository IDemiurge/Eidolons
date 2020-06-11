package eidolons.game.netherflame.boss.anims;

import com.badlogic.gdx.Input;
import eidolons.game.netherflame.boss.BOSS_PART;
import eidolons.game.netherflame.boss.BossHandler;
import eidolons.game.netherflame.boss.BossManager;
import eidolons.game.netherflame.boss.BossModel;
import eidolons.game.netherflame.boss.anims.old.BossAnimData;
import eidolons.game.netherflame.boss.anims.old.BossPart;
import eidolons.game.netherflame.boss.anims.old.PartAnim;
import eidolons.game.netherflame.boss.anims.old.PartAnimSprite;
import eidolons.game.netherflame.boss.logic.BossCycle;
import main.data.filesys.PathFinder;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class BossAnim3dHandler<T extends BossModel> extends BossHandler<T> implements BossAnimHandler {

    private final boolean fastMode = false;

    public BossAnim3dHandler(BossManager manager) {
        super(manager);

        GuiEventManager.bind(GuiEventType.BOSS_ACTION, p -> {
            //            animate((ActionInput) p.get());
        });
        GuiEventManager.bind(GuiEventType.ANIMATION_DONE, p -> {
            //            animate((ActionInput) p.getVar());
        });
        GuiEventManager.bind(GuiEventType.KEY_TYPED, p -> {
            keyTyped((int) p.get());
        });
        if (!fastMode)
            preloadSprites();
    }

    protected void keyTyped(int i) {
        switch (i) {
            case Input.Keys.BACKSPACE:
                for (BossCycle.BOSS_TYPE type : getEntitiesSet()) {
                    getVisual(type).setVisible(!getVisual(type).isVisible());
                }
        }
    }

    public abstract String getSpritePath();

    public void preloadSprites() {

    }

    public void animateAction() {
        List<Pair<BossPart, PartAnim>> anims = null;

        for (Pair<BossPart, PartAnim> anim : anims) {
            anim.getLeft().addAnim(anim.getRight());
        }
    }

    @Override
    public void animate(BossPart part, BossAnims.BOSS_ANIM animType) {
        part.addAnim(createAnim(part.getType(), animType));
    }

    @Override
    public void handleEvent(Event event) {
        //intercept normal handling?

        //HIT

        //DEATH - re-appear
    }

    private PartAnim createAnim(BOSS_PART type, BossAnims.BOSS_ANIM animType) {
        String atlasFormat = ".txt";
        String path = PathFinder.getBossSpritesPath() + getModel().getName() +
                atlasFormat;


        PartAnimSprite sprite = new PartAnimSprite(path);
        BossAnimData data = null;
        return new PartAnim(data, sprite);
    }
}
