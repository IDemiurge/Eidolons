package libgdx.anims.std;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.obj.BattleFieldObject;
import libgdx.anims.AnimData;
import libgdx.anims.AnimData.ANIM_VALUES;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.main.AnimMaster;
import libgdx.anims.sprite.SpriteAnimation;
import libgdx.bf.grid.cell.BaseView;
import libgdx.screens.ScreenMaster;
import libgdx.screens.dungeon.DungeonScreen;
import libgdx.texture.TextureCache;
import main.entity.Ref.KEYS;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallbackParam;
import main.system.GuiEventType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

import static main.system.GuiEventType.DESTROY_UNIT_MODEL;

/**
 * Created by JustMe on 1/16/2017.
 */
public class DeathAnim extends ActionAnim {
    public static final STANDARD_EVENT_TYPE EVENT_TYPE = STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED;
    private static boolean on = true;
    BattleFieldObject unit;
    DEATH_ANIM template;
    private Image skull;

    SpriteAnimation sprite;

    public DeathAnim(Event e) {
        super(e.getRef().getObj(KEYS.ACTIVE), getDeathAnimData(e));
        unit = (BattleFieldObject) e.getRef().getTargetObj();
        template = getTemplate(getActive(), unit);
        setDuration(2);
        setRef(e.getRef());
    }
    public DeathAnim(BattleFieldObject obj) {
        super(null, new AnimData());
        unit = obj;
        template = getTemplate(getActive(), unit);
        setDuration(2);
        // setRef(e.getRef()); //TODO
    }

    private static AnimData getDeathAnimData(Event e) {
        AnimData data = new AnimData();
        data.setValue(ANIM_VALUES.PARTICLE_EFFECTS, "impact/Crimson Death");
        return data;
    }

    public static boolean isOn() {
        if (!DungeonScreen.getInstance().isDrawGrid())
            return false;
        if (!AnimMaster.isOn()) {
            return false;
        }
        return on;
    }

    public static void setOn(boolean on) {
        DeathAnim.on = on;
    }

    @Override
    public boolean tryDraw(Batch batch) {
        return super.draw(batch);
    }

    @Override
    public List<Pair<GuiEventType, EventCallbackParam>> getEventsOnFinish() {
        return Collections.singletonList(new ImmutablePair<>(DESTROY_UNIT_MODEL, new EventCallbackParam(unit)));
    }

    @Override
    protected Action getAction() {
        return null;
    }

    @Override
    protected void dispose() {
        super.dispose();
        if (getActor() != null) {
            getActor().remove();
        }
    }

    @Override
    protected void add() {
//        main.system.auxiliary.log.LogMaster.log(1,"Death Anim adds with \n" +getRef());
        if (getActor() == null) {
            main.system.auxiliary.log.LogMaster.log(1,"Death Anim no actor for \n" +getRef());
            return;
        }
//        AnimMaster.getInstance().addActor(getActor());
//        getActor().setPosition(getOrigin().x, getOrigin().y);
        AlphaAction action = ActionMasterGdx.addFadeOutAction(getActor());
        if (action == null) {
            main.system.auxiliary.log.LogMaster.log(1,"Death Anim fade failed for \n" +getRef());
            getActor().setVisible(false);
            return;
        }
        action.setDuration(duration);
        ActionMasterGdx.addRemoveAfter(getActor());
    }

    @Override
    public Coordinates getOriginCoordinates() {
        return unit.getCoordinates();
    }

    @Override
    public Actor getActor() {
        BaseView actor = ScreenMaster.getGrid().getViewMap()
         .get(unit);
        if (actor != null)
            return actor;
        if (skull == null) {
            skull = new Image(TextureCache.getOrCreate("ui/empty.png")) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    act(Gdx.graphics.getDeltaTime());
                    super.draw(batch, parentAlpha);
                }
            };
        }
        return skull;
//        return null;
    }

    private DEATH_ANIM getTemplate(DC_ActiveObj active, BattleFieldObject unit) {
//        getRef().getEvent().getRef().getDamageType();
        return DEATH_ANIM.FADE;
    }

    @Override
    public void start() {
//        addSfx();
        //skull / grave?

        if (!unit.isDead()){
            main.system.auxiliary.log.LogMaster.log(1,"Screw that Death Anim" +getRef());
            return;
        }
        super.start();
        add();
    }

    @Override
    public void finished() {
        super.finished();
        getActor().setVisible(false);
        dispose();

//     TODO    GuiEventManager.trigger(GuiEventType.SHOW_SPRITE,
//                HitAnim.getSpritePath(HitAnim.SPRITE_TYPE.BONE,
//                        HitAnim.HIT.BONE_CRACK),
//                getActive());
    }

    public enum DEATH_ANIM {
        FADE, FLASH,
        EXPLODE,
        BURN,
        COLLAPSE,
        ATOMIZE, SHATTER,;
        String spritePath;
    }

}
