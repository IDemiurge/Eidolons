package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.DummyUnit;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.tools.prune.PruneMaster;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.MoveAnimation;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.bf.overlays.HpBarManager;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.NoHitGroup;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.libgdx.texture.Sprites;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.text.Texts;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.images.ImageManager;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 5/2/2018.
 */
public class GridManager {
    private static final String SEQUENTIAL = "=SEQ=";
    private static final String COMMENT_WAIT_KEY = "[COMMENT_WAIT]";
    public static GridManager instance;

    FACING_DIRECTION f = FACING_DIRECTION.NORTH;
    GridPanel panel;
    private Integer waitCounter = 0;

    public GridManager(GridPanel panel) {
        this.instance = this;
        this.panel = panel;
        GuiEventManager.bind(INGAME_EVENT_TRIGGERED, onIngameEvent());

        GuiEventManager.bind(SHOW_COMMENT_PORTRAIT, p -> {
            List list = (List) p.get();
            comment(list);
        });
    }

    public void comment_(Object... params) {
        comment(Arrays.asList(params));
    }

    private void comment(List list) {
        String key = (String) list.get(1);
        String text = Texts.getComments().get(key);
        if (text == null) {
            text = key;
        }
        Coordinates c = null;
        if (list.get(0) instanceof Unit) {
            Vector2 offset = null;
            if (list.size() > 2) {
                offset = (Vector2) list.get(2);
            }
            Unit hero = (Unit) list.get(0);
            comment(hero, text, offset);
        } else {
            String img = list.get(0).toString();
            c = (Coordinates) list.get(2);
            if (Cinematics.ON) {
                f = f.rotate(true);
            } else {
                f = FACING_DIRECTION.NORTH;
            }
            comment(img, f, c, true, text, null);
        }
    }

    public Map<BattleFieldObject, BaseView> getViewMap() {
        return panel.getViewMap();
    }


    public void comment(String img, FACING_DIRECTION facingDirection, Coordinates c, boolean b, String text, Vector2 at) {
        if (!isSequentialComment(text)) {
            main.system.auxiliary.log.LogMaster.dev("NON Sequential comment: " + text);
            createFloatText(c, at, facingDirection, text, false);
            String finalText1 = text;
            Gdx.app.postRunnable(() ->
                    commentGdx(img, facingDirection, c, b, finalText1, at, null, null));
        } else {
            text = removeSequentialKey(text);
            String finalText = text;
            String key = COMMENT_WAIT_KEY + (getWaitCounter());
            if (waitCounter > 0)
                WaitMaster.waitLock(key, 8000);
            waitCounter++;
            key = COMMENT_WAIT_KEY + (getWaitCounter());
            String finalKey = key;

            main.system.auxiliary.log.LogMaster.dev(key+ "Sequential comment: " + text);

            AtomicBoolean flag = createFloatText(c, at, facingDirection, finalText, true);
            Gdx.app.postRunnable(() -> commentGdx(img, facingDirection, c, b, finalText, at, finalKey, flag));
        }

//            Eidolons.onNonGdxThread(() -> {
//                String key = COMMENT_WAIT_KEY + (getWaitCounter());
//                if (waitCounter > 0)
//                    WaitMaster.waitLock(key);
//                waitCounter++;
//                key = COMMENT_WAIT_KEY + (getWaitCounter());
//                String finalKey = key;
//                Gdx.app.postRunnable(() -> {
//                    AtomicBoolean flag = createFloatText(c, at, facingDirection, finalText, true);
//                    commentGdx(img, facingDirection, c, b, finalText, at, finalKey, flag);
//                });
//            });
//        }

    }

    private AtomicBoolean createFloatText(Coordinates c, Vector2 at, FACING_DIRECTION facingDirection, String text, boolean seq) {
//        if (c != null) {
//            at = GridMaster.getCenteredPos(c);
//        }

        AtomicBoolean flag = null;
        Label.LabelStyle style = null;
        if (seq) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 22);
            flag = new AtomicBoolean(false);
        }

        FloatingTextMaster.getInstance().createFloatingText
                (FloatingTextMaster.TEXT_CASES.BATTLE_COMMENT, text, new DummyUnit() {
                    @Override
                    public FACING_DIRECTION getFacing() {
                        return facingDirection;
                    }

                    @Override
                    public Coordinates getCoordinates() {
                        return c;
                    }
                }, null, at, style, flag);

        return flag;
    }

    public static String removeSequentialKey(String text) {
        return text.replace(SEQUENTIAL, "").trim();
    }

    private boolean isSequentialComment(String text) {
        return text.contains(SEQUENTIAL);
    }

    private void comment(Unit unit, String text, Vector2 at) {
        boolean textTop = false;
        String portrait = null;
//        if (unit == Eidolons.getMainHero()) {
//            portrait = (Sprites.COMMENT_KESERIM);
//        } else {
//            textTop = true;
            portrait = ImageManager.getBlotch(unit);
//        }

        comment(portrait, unit.getFacing(), unit.getCoordinates(), textTop, text, at);
        LogMaster.dev(text + "\n - Comment by " + unit.getNameAndCoordinate());
        Eidolons.getGame().getLogManager().log(unit.getName() + " :" + text);
    }

    private void commentGdx(String image, FACING_DIRECTION f, Coordinates c, boolean textTop, String text,
                            Vector2 at, String key, AtomicBoolean flag) {
        if (!Cinematics.ON)
            GuiEventManager.trigger(CAMERA_ZOOM, new CameraMan.MotionData(-10f, 1f));

        SpriteX commentBgSprite = new SpriteX(Sprites.INK_BLOTCH) {
            @Override
            public boolean remove() {
                getParent().remove(); //TODO could be better.
                WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.COMMENT_DONE, text);
                return super.remove();
            }
        };
        GroupX portrait = new SpriteX(image);
        portrait.setX((int) (commentBgSprite.getWidth() / 2 - portrait.getWidth()));
        portrait.setY((int) (commentBgSprite.getHeight() / 2 - portrait.getHeight()));

        SpriteX commentTextBgSprite = new SpriteX(Sprites.INK_BLOTCH);
        commentTextBgSprite.setBlending(SuperActor.BLENDING.INVERT_SCREEN);
        if (textTop){
            commentTextBgSprite.setScale(0.75f);
            commentTextBgSprite.setOrigin(commentBgSprite.getWidth() / 4, commentBgSprite.getHeight() / 4);
            commentTextBgSprite.setRotation(90);
            commentTextBgSprite.setPosition(commentBgSprite.getWidth() / 2f, -100 - commentBgSprite.getHeight() / 8);
        } else {
            commentTextBgSprite.setOrigin(commentBgSprite.getWidth() / 2, commentBgSprite.getHeight() / 2);
            commentTextBgSprite.setRotation(90);
            commentTextBgSprite.setPosition(commentBgSprite.getWidth() / 2f, -100 - commentBgSprite.getHeight() / 8);
        }

//            commentBgSprite.setShader(ShaderMaster.SHADER.INVERT);
        commentBgSprite.setBlending(SuperActor.BLENDING.INVERT_SCREEN);
        //TODO CACHED??
        GroupX commentGroup = new NoHitGroup();
        commentGroup.setSize(commentBgSprite.getWidth(), commentBgSprite.getHeight());
        commentGroup.addActor(commentBgSprite);

        if (!textTop) {
            commentGroup.addActor(commentTextBgSprite);
            commentGroup.addActor(portrait);
        } else {
            commentGroup.addActor(portrait);
            commentGroup.addActor(commentTextBgSprite);
        }
        panel.addActor(commentGroup);

        Vector2 v = GridMaster.getCenteredPos(c);
        commentGroup.setPosition(v.x, v.y);

        ActionMaster.addFadeInAction(commentBgSprite, 2);
        ActionMaster.addFadeInAction(commentTextBgSprite, 2);
        ActionMaster.addFadeInAction(portrait, 3);
//            commentSprite.setScale(0.5f);
        //flip?
        Coordinates panTo = c.getOffsetByY(2);
        if (at != null) {
            commentGroup.setX(at.x);
            commentGroup.setY(at.y);
//            commentGroup.setX(commentGroup.getX()+at.x); TODO is it better
//            commentGroup.setY(commentGroup.getY()+at.y);
        } else
            switch (f.flip().rotate(f.flip().isCloserToZero())) {
                case NORTH:
                    commentGroup.setY(commentGroup.getY() + ((int) commentGroup.getHeight() / 2));
                    panTo = panTo.getOffsetByY(-3);
                    break;
                case WEST:
                    commentGroup.setX(commentGroup.getX() - ((int) commentGroup.getWidth() / 2));
                    panTo = panTo.getOffsetByX(-3);
                    break;
                case EAST:
                    commentGroup.setX(commentGroup.getX() + ((int) commentGroup.getWidth() / 2));
                    panTo = panTo.getOffsetByX(3);
                    break;
                case SOUTH:
                    commentGroup.setY(commentGroup.getY() - ((int) commentGroup.getHeight() / 2));
                    panTo = panTo.getOffsetByY(3);
                    break;
            }

        if (at == null)
            at = GridMaster.getCenteredPos(panTo);
        if (Cinematics.ON) {
//            at.y= at.y-100;
        }
        GuiEventManager.trigger(CAMERA_PAN_TO, at, true);

        GroupX finalPortrait = portrait;
        Runnable r = () -> {
            main.system.auxiliary.log.LogMaster.dev(key + " key; fade comment   " + text);
            boolean seq=false;
            if (key != null) {
                if (flag != null) {
                    flag.set(true);
                    seq=true;
                }
                WaitMaster.doAfterWait(5000, () -> WaitMaster.unlock(key));
            }
            ActionMaster.addFadeOutAction(commentBgSprite,seq? 6: 4);
            ActionMaster.addRemoveAfter(commentBgSprite);
            ActionMaster.addFadeOutAction(finalPortrait, seq? 4:3);
            ActionMaster.addFadeOutAction(commentTextBgSprite, seq? 5:4);

        };
        boolean onInput = !Cinematics.ON;
        if (onInput) {
            Runnable finalR = r;
            r = () -> GdxMaster.onInputGdx(finalR);
        }

        WaitMaster.doAfterWait(4000 + text.length() * 12, r);
        panel.commentSprites.add(commentGroup);
    }

    private EventCallback onIngameEvent() {
        return param -> {
            Event event = (Event) param.get();
            Ref ref = event.getRef();

            boolean caught = false;

            if (event.getType() == STANDARD_EVENT_TYPE.EFFECT_HAS_BEEN_APPLIED) {
                GuiEventManager.trigger(EFFECT_APPLIED, event.getRef().getEffect());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_CHANGED_FACING
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_CLOCKWISE
                    || event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_TURNED_ANTICLOCKWISE) {
                if ((ref.getObj(KEYS.TARGET) instanceof BattleFieldObject)) {
                    BattleFieldObject hero = (BattleFieldObject) ref.getObj(KEYS.TARGET);
//                if (hero.isMainHero()) TODO this is an experiment (insane) feature...
//                    if (hero.isMine()) {
//                        turnField(event.getType());
//                    }
                    BaseView view = getViewMap().get(hero);
                    if (view != null && view instanceof GridUnitView) {
                        GridUnitView unitView = ((GridUnitView) view);
                        unitView.updateRotation(hero.getFacing().getDirection().getDegrees());
//                    SoundController.getCustomEventSound(SOUND_EVENT.UNIT_TURNS, );
                        if (hero instanceof Unit)
                            DC_SoundMaster.playTurnSound((Unit) hero);
                    }
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_FALLEN_UNCONSCIOUS
            ) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_ON, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_RECOVERED_FROM_UNCONSCIOUSNESS) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_KILLED) {
                GuiEventManager.trigger(UNIT_GREYED_OUT_OFF, ref.getSourceObj());
                if (!DeathAnim.isOn() || ref.isDebug()) {
                    GuiEventManager.trigger(DESTROY_UNIT_MODEL, ref.getTargetObj());
                }
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_BEING_MOVED) {
                if (!MoveAnimation.isOn()) //|| AnimMaster.isAnimationOffFor(ref.getSourceObj(), viewMap.get(ref.getSourceObj())))
                    panel.removeUnitView((BattleFieldObject) ref.getSourceObj());
                caught = true;
            } else if (event.getType() == STANDARD_EVENT_TYPE.UNIT_FINISHED_MOVING) {
                unitMoved(event.getRef().getSourceObj());
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_BEING_MODIFIED")) {
                caught = true;
            } else if (event.getType().name().startsWith("PROP_")) {
                caught = true;
            } else if (event.getType().name().startsWith("ABILITY_")) {
                caught = true;
            } else if (event.getType().name().startsWith("EFFECT_")) {
                caught = true;
            } else if (event.getType().name().startsWith("PARAM_MODIFIED")) {
                if (GuiEventManager.isParamEventAlwaysFired(event.getType().getArg())) {
                    if (!HpBar.isResetOnLogicThread())
                        checkHpBarReset(event.getRef().getSourceObj());
                }
                caught = true;
            }
            if (event.getType() == STANDARD_EVENT_TYPE.UNIT_HAS_BEEN_DEALT_PURE_DAMAGE) {
                if (!HpBar.isResetOnLogicThread())
                    checkHpBarReset(event.getRef().getTargetObj());
                caught = true;
            }

            if (!caught) {
                /*      System.out.println("catch ingame event: " + event.getType() + " in " + event.getRef());
                 */
            }
        };
    }

    public void unitMoved(Obj sourceObj) {
        if (!MoveAnimation.isOn() || AnimMaster.isAnimationOffFor(sourceObj,
                getViewMap().get(sourceObj))
                || sourceObj.getGame().getManager().getActiveObj() != sourceObj
            //what about COUNTER ATTACK?!
            //TODO igg demo hack for force and teleport now...
        )
            panel.unitMoved((BattleFieldObject) sourceObj);
    }

    public void checkHpBarReset(Obj obj) {
        HpBarView view = (HpBarView) getViewMap().get(obj);
        if (view != null)
            if (view.getActor().isVisible())
                if (view.getHpBar() != null)
                    if (
                            !ExplorationMaster.isExplorationOn()
                                    || HpBarManager.canHpBarBeVisible((BattleFieldObject) view.getActor().getUserObject()))
                        view.resetHpBar();
    }


    public Integer getWaitCounter() {
        return waitCounter;
    }

}
