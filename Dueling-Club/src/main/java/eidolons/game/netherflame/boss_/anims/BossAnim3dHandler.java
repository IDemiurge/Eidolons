package eidolons.game.netherflame.boss_.anims;

import eidolons.game.netherflame.boss_.BOSS_PART;
import eidolons.game.netherflame.boss_.BossHandler;
import eidolons.game.netherflame.boss_.BossManager;
import eidolons.game.netherflame.boss_.anims.view.BossPart;
import main.data.filesys.PathFinder;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class BossAnim3dHandler extends BossHandler implements BossAnimHandler {

    private final boolean fastMode=false;

    public BossAnim3dHandler(BossManager manager) {
        super(manager);

        GuiEventManager.bind(GuiEventType.BOSS_ACTION, p -> {
//            animate((ActionInput) p.get());
        });
        GuiEventManager.bind(GuiEventType.ANIMATION_DONE, p -> {
//            animate((ActionInput) p.getVar());
        });
        if (!fastMode)
            preloadSprites();
    }

    public abstract String getSpritePath();

    public  void preloadSprites() {

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
        String atlasFormat=".txt";
        String path = PathFinder.getBossSpritesPath()+getModel().getName()+
                atlasFormat;



        PartAnimSprite sprite=new PartAnimSprite(path);
        BossAnimData data = null;
        return  new PartAnim(data, sprite);
    }
}
