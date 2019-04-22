package eidolons.libgdx.anims.fullscreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.content.PARAMS;
import eidolons.game.battlecraft.logic.battlefield.FacingMaster;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxImageMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitImage;
import eidolons.libgdx.texture.TextureCache;
import main.data.filesys.PathFinder;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 10/12/2018.
 */
public class FullscreenAnims extends GroupX {
    public static final boolean randomFacingMode = true;
    private static final float DELAY = 0.5f;
    Map<FULLSCREEN_ANIM, Map<FACING_DIRECTION, Actor>> groupCache = new HashMap<>();
    private FullscreenAnimDataSource data;
    private float delayTimer;
    private float showingTimer;

    public FullscreenAnims() {
        init();
//        GuiEventManager.bind(GuiEventType.SHOW_FULLSCREEN_ANIM, p -> {
//            Color color;
//            FullscreenAnimDataSource dataSource = (FullscreenAnimDataSource) p.get();
            //sprite?
            //            FACING_DIRECTION facing=
            //resize
//        });
        GuiEventManager.bind(GuiEventType.INGAME_EVENT_TRIGGERED, p -> {
            Event e = (Event) p.get();
            if (e.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE) {
                                if (
                                        e.getRef().getTargetObj() == Eidolons.getMainHero())
                {
                    FACING_DIRECTION facing = null;
                    if (randomFacingMode) {
                        facing = FacingMaster.getRandomFacing();
                    } else
                        facing = FacingMaster.getRelativeFacing(e.getRef().getSourceObj().getCoordinates(),
                         e.getRef().getTargetObj().getCoordinates());

                    FULLSCREEN_ANIM type = FULLSCREEN_ANIM.BLOOD;
//                    if (RandomWizard.random()) {
//                        type = FULLSCREEN_ANIM.POISON;
//                    }
                    float intensity = RandomWizard.getRandomFloatBetween(0.15f, 0.35f);
                    intensity+=
                     e.getRef().getAmount() / e.getRef().getTargetObj().getIntParam(PARAMS.ENDURANCE);
                    intensity+=
                     e.getRef().getAmount() / e.getRef().getTargetObj().getIntParam(PARAMS.TOUGHNESS)/2;

                    if (showingTimer>0){
                        data.intensity += intensity;
                        return;
                    }
                    data = new FullscreenAnimDataSource(type, intensity,
                     facing);
                    delayTimer = DELAY;
                    showingTimer = getDuration() + delayTimer;
                }
            }
        });

    }

    @Override
    public void act(float delta) {
        if (showingTimer<=0)
            return;
        showingTimer -= delta;
        if (delayTimer>0) {
            delayTimer -= delta;
            return;
        }
        show(data);
        super.act(delta);
        //        if (getChildren().size>0) {
        //            main.system.auxiliary.log.LogMaster.log(1, " " + getChildren().get(0).getColor());
        //        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (showingTimer<=0)
            return; //precaution...
        super.draw(batch, parentAlpha);
    }
    private void init() {
        for (FULLSCREEN_ANIM type : FULLSCREEN_ANIM.values()) {
            Map<FACING_DIRECTION, Actor> map = new HashMap<>();
            for (FACING_DIRECTION facing : FACING_DIRECTION.values) {
                //                Actor group = new Actor();

                String path = PathFinder.getImagePath() + getCorePath() + type;
                path = FileManager.getRandomFilePathVariant(path, ".png");
                path = GdxImageMaster.cropImagePath(path);
                Image img = new NoHitImage(TextureCache.getOrCreateR(path));
                //                group.addActor(img);
                img.setVisible(false);
                img.getColor().a = 0;
                //                addActor(img);
                map.put(facing, img);
                img.setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
            }
            groupCache.put(type, map);
        }
    }

    private String getCorePath() {
        return PathFinder.getSpritesPathNew() + "fullscreen/";
    }

    private void show(FullscreenAnimDataSource data) {
        Actor group = groupCache.get(data.getType()).get(data.getFrom());
        group.setVisible(true);
        group.getColor().a = 0;
        ActorMaster.addAlphaAction(group, getFadeInDuration(), data.getIntensity());
        ActorMaster.addAfter(group, ActorMaster.getAlphaAction(group, getFadeOutDuration(), 0, false));
        ActorMaster.addRemoveAfter(group);
        addActor(group);
    }

    private float getDuration() {
        return getFadeInDuration() + getFadeOutDuration();
    }

    @Override
    protected float getFadeInDuration() {
        return 0.5f;
    }

    @Override
    protected float getFadeOutDuration() {
        return 1.5f;
    }


    public enum FULLSCREEN_ANIM {
        //        BLACK,
        BLOOD,
        POISON,
        //        DAMAGE
    }
}
