package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.DummyUnit;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.Cinematics;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.anims.actions.WaitAction;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.anims.std.DeathAnim;
import eidolons.libgdx.anims.std.MoveAnimation;
import eidolons.libgdx.anims.text.FloatingText;
import eidolons.libgdx.anims.text.FloatingTextMaster;
import eidolons.libgdx.bf.GridMaster;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.bf.overlays.HpBar;
import eidolons.libgdx.bf.overlays.HpBarManager;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.screens.DungeonScreen;
import eidolons.libgdx.stage.camera.CameraMan;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.Sprites;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.audio.DC_SoundMaster;
import eidolons.system.text.Texts;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.EventCallback;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
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
//            createFloatText(c, at, facingDirection, text, false);
            String finalText1 = text;
            Gdx.app.postRunnable(() ->
                    commentGdx(img, facingDirection, c, b, finalText1, at, null, false));
        } else {
            text = removeSequentialKey(text);
            String finalText = text;
            String key = COMMENT_WAIT_KEY + (getWaitCounter());
            if (waitCounter > 0)
                WaitMaster.waitLock(key, 8000);
            waitCounter++;
            key = COMMENT_WAIT_KEY + (getWaitCounter());
            String finalKey = key;

            main.system.auxiliary.log.LogMaster.dev(key + "Sequential comment: " + text);

            Gdx.app.postRunnable(() -> commentGdx(img, facingDirection, c, b, finalText, at, finalKey, true));
        }
    }

    private FloatingText createFloatText(Coordinates c, Vector2 at, FACING_DIRECTION facingDirection, String text, AtomicBoolean flag,
                                         boolean seq) {
        Label.LabelStyle style = null;
        if (seq) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 22);
            flag = new AtomicBoolean(false);
        }
        FloatingText floatText = FloatingTextMaster.getInstance().createFloatingText
                (FloatingTextMaster.TEXT_CASES.BATTLE_COMMENT, text, new DummyUnit() {
                    @Override
                    public FACING_DIRECTION getFacing() {
                        return facingDirection;
                    }

                    @Override
                    public Coordinates getCoordinates() {
                        return c;
                    }
                }, null, at, style, flag, false);

        return floatText;
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
        Eidolons.getGame().getLogManager().log(unit.getName() + ": \n" +
                StringMaster.removeNewLines(text).trim());
    }

    private void commentGdx(String image, FACING_DIRECTION f, Coordinates c, boolean textTop, String text,
                            Vector2 at, String key, boolean seq) {
        boolean uiStage = seq;
        AtomicBoolean flag = new AtomicBoolean();
        FloatingText floatingText = createFloatText(c, at, f, text, flag, seq);

        if (!Cinematics.ON && !uiStage)
            GuiEventManager.trigger(CAMERA_ZOOM, new CameraMan.MotionData(-10f, 1f));

        SpriteX commentBgSprite = new SpriteX(Sprites.INK_BLOTCH) {
            @Override
            public boolean remove() {
                getParent().remove();
                return super.remove();
            }
        };
//        commentBgSprite=checkReplaceStatic(commentBgSprite);
        if (commentBgSprite.getSprite()==null || commentBgSprite.getSprite().isDefault()
                || commentBgSprite.getSprite().getRegions().size<1) {
            main.system.auxiliary.log.LogMaster.important("INK BLOTCH IS EMPTY! " );
            commentBgSprite = new SpriteX("ui/INK BLOTCH.png"){
                @Override
                public boolean remove() {
                    getParent().remove();
                    return super.remove();
                }
            };
        }else {
            commentBgSprite.setBlending(SuperActor.BLENDING.INVERT_SCREEN);
        }
        GroupX portrait = createPortrait(image);
        portrait.setX((int) (commentBgSprite.getWidth() / 2 - portrait.getWidth()));
        portrait.setY((int) (commentBgSprite.getHeight() / 2 - portrait.getHeight()));

        SpriteX commentTextBgSprite = new SpriteX(Sprites.INK_BLOTCH);

        if (commentTextBgSprite.getSprite()==null || commentTextBgSprite.getSprite().isDefault()
                || commentTextBgSprite.getSprite().getRegions().size<1) {
            main.system.auxiliary.log.LogMaster.important("INK BLOTCH IS EMPTY! " );
            commentTextBgSprite = new SpriteX("ui/INK BLOTCH.png");
        } else
            commentTextBgSprite.setBlending(SuperActor.BLENDING.INVERT_SCREEN);


        floatingText.setStayFullCondition(delta -> flag.get());
        floatingText.setMaxWidth(seq ? 700 : 600);
        floatingText.init();

        Vector2 finalAt = at;
        SpriteX finalCommentBgSprite = commentBgSprite;
        SpriteX finalCommentTextBgSprite = commentTextBgSprite;
        GroupX commentGroup = new GroupX() {
            @Override
            public Actor hit(float x, float y, boolean touchable) {

                if (flag == null) {
                    return null;
                } else {
                    if (!flag.get()) {
                        return null;
                    }
                }
                return super.hit(x, y, touchable);
            }

            @Override
            public void act(float delta) {
                DIRECTION textPlacement = textTop ? DIRECTION.DOWN
                        : (c.dst(Eidolons.getMainHero().getCoordinates()) > 4) ? DIRECTION.LEFT : DIRECTION.RIGHT;
                if (finalAt != null) {
                    Vector2 v = GridMaster.getCenteredPos(Eidolons.getMainHero().getCoordinates());
                    if (v.x < finalAt.x) {
                        textPlacement = DIRECTION.LEFT;
                    } else {
                        textPlacement = DIRECTION.RIGHT;
                    }
                }
                if (textTop) {
                    finalCommentTextBgSprite.setScale(0.85f);
                    finalCommentTextBgSprite.setOrigin(finalCommentBgSprite.getWidth() / 4, finalCommentBgSprite.getHeight() / 4);
                    finalCommentTextBgSprite.setRotation(90);
                    finalCommentTextBgSprite.setPosition(finalCommentBgSprite.getWidth() / 2f, -50 - finalCommentBgSprite.getHeight() / 4);
                } else {
                    finalCommentTextBgSprite.setOrigin(finalCommentBgSprite.getWidth() / 2, finalCommentBgSprite.getHeight() / 2);
                    finalCommentTextBgSprite.setRotation(90);
                    if (textPlacement == DIRECTION.RIGHT) {
                        finalCommentTextBgSprite.setPosition(finalCommentBgSprite.getWidth() / 2.5f, -100 - finalCommentBgSprite.getHeight() / 7);
                    } else if (textPlacement == DIRECTION.LEFT) {

                        finalCommentTextBgSprite.setPosition(-finalCommentBgSprite.getWidth() / 2.5f, -100 - finalCommentBgSprite.getHeight() / 7);
                    }
                }

                float bgWidth = finalCommentBgSprite.getWidth();
                float bgHeight = finalCommentBgSprite.getHeight();

                float w = Math.min(floatingText.getLabel().getPrefWidth(), 626);
                float h = Math.min(floatingText.getLabel().getPrefHeight(), 626);
                float x = finalCommentTextBgSprite.getX();
                float y = finalCommentTextBgSprite.getY();

                switch (textPlacement) {
                    case LEFT:
                        x = x - bgHeight / 4 + w*0.8f / 2 - 30;
                        y = y + (h - y) / 2 + h*0.57f + 38;
                        break;
                    case RIGHT:
                        x = x - bgWidth / 2 + bgHeight - w - 20;
                        y = y + (h - y) / 2 + h + 21;
                        break;
                    case DOWN:
                        x = x - w / 2 - bgWidth + bgHeight / 2 + 180;
                        y = y + (h - y) / 2 - 25 - h / 8;
                        break;

                }
                //TODO CACHED??
                float finalX = x;
                float finalY = y;

                floatingText.setPosition(finalX, finalY);
                super.act(delta);
            }
        };

        commentGroup.act(0); //TODO remove

        commentGroup.setSize(commentBgSprite.getWidth(), commentBgSprite.getHeight());
        commentGroup.addActor(commentBgSprite);
        if (!textTop) {
            commentGroup.addActor(commentTextBgSprite);
            commentGroup.addActor(portrait);
        } else {
            commentGroup.addActor(portrait);
            commentGroup.addActor(commentTextBgSprite);
        }

        commentGroup.addActor(floatingText);//        floatingText.getFontStyle().font.getSpaceWidth()*floatingText.getText() there gotta be max width!


        floatingText.setFadeInDuration(textTop ? 5 : 4);

        floatingText.fadeIn();
        ActionMaster.addFadeInAction(commentBgSprite, 2);
        ActionMaster.addFadeInAction(commentTextBgSprite, 2);
        ActionMaster.addFadeInAction(portrait, 3);
//            commentSprite.setScale(0.5f);
        //flip?
        Vector2 v = GridMaster.getCenteredPos(c);
        commentGroup.setPosition(v.x, v.y);
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
        else {
            at.add(commentTextBgSprite.getX(), commentTextBgSprite.getY());
        }

        if (Cinematics.ON) {
//            at.y= at.y-100;
        }
        if (!uiStage) {
            GuiEventManager.trigger(CAMERA_PAN_TO, at, true);
        }


        GroupX finalPortrait = portrait;
        final boolean[] faded = {false};
        Runnable r = () -> {
            if (faded[0]) {
                return;
            }
            faded[0] = true;
            main.system.auxiliary.log.LogMaster.dev(key + " key; fade comment   " + text);
            if (key != null) {
                if (flag != null) {
                    flag.set(true);
                }
                WaitMaster.doAfterWait(5000, () -> WaitMaster.unlock(key));
            }
            floatingText.fadeOut();

            panel.getActiveCommentSprites().remove(commentGroup);
            GdxMaster.inputPass();

            ActionMaster.addAction(finalCommentBgSprite, new WaitAction(delta -> Eidolons.getGame().isPaused()));
            ActionMaster.addAction(finalPortrait, new WaitAction(delta -> Eidolons.getGame().isPaused()));
            ActionMaster.addAction(finalCommentTextBgSprite, new WaitAction(delta -> Eidolons.getGame().isPaused()));

            ActionMaster.addFadeOutAction(finalCommentBgSprite, seq ? 6 : 4);
            ActionMaster.addRemoveAfter(finalCommentBgSprite);
            ActionMaster.addFadeOutAction(finalPortrait, seq ? 4 : 3);
            ActionMaster.addFadeOutAction(finalCommentTextBgSprite, seq ? 5 : 4);

            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.COMMENT_DONE, true);
        };

        Runnable finalFade = r;
        WaitMaster.doAfterWait(1000 + text.length() * 3, () ->
                commentGroup.addListener(getCommentMouseListener(commentGroup, finalFade, key)));

        boolean onInput = !Cinematics.ON && (seq || textTop);
//        if (onInput)
        {
             GdxMaster.onPassInput(r);
        }
//        else
        float time = 2500 + text.length() * 25;
        if (onInput)
            time=time*2f;

            WaitMaster.doAfterWait((int) time,()-> {
                r.run();
            }  );

//      TODO is it useful?
//       if (uiStage) {
//            commentGroup.setX(commentGroup.getX() - DungeonScreen.getInstance().getController().getXCamPos()
//                    + GdxMaster.getWidth() / 2);
//            commentGroup.setY(commentGroup.getY() - DungeonScreen.getInstance().getController().getYCamPos()
//                    + GdxMaster.getHeight() / 2);
//            DungeonScreen.getInstance().getGuiStage().addActor(commentGroup);
//        } else
            {
            panel.addActor(commentGroup);
            panel.getCommentSprites().add(commentGroup);
            panel.getActiveCommentSprites().add(commentGroup);
        }
    }

    private GroupX createPortrait(String image) {
        if (TextureCache.isImage(image)) {
            return new SpriteX(image);
        }
        return new ImageContainer(ImageManager.getLargeImage(Images.DEMIURGE));
    }

    private EventListener getCommentMouseListener(GroupX commentGroup, Runnable fadeRunnable, String key) {
        return new SmartClickListener(commentGroup) {
            private boolean done;

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (done)
                    return;
                main.system.auxiliary.log.LogMaster.dev(" manual fade   " + key);
                fadeRunnable.run();
                done = true;
//                GdxMaster.clearPassInput();
                super.clicked(event, x, y);
            }
        };
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
                if (!MoveAnimation.isOn()) //|| AnimMaster.isAnimationOffFor(ref.getSourceObj(), viewMap.getVar(ref.getSourceObj())))
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
