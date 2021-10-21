package libgdx.bf.grid.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.content.consts.VisualEnums;
import eidolons.content.consts.libgdx.GdxStringUtils;
import eidolons.entity.obj.unit.DummyUnit;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.game.module.cinematic.Cinematics;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.anims.actions.WaitAction;
import libgdx.anims.sprite.SpriteX;
import libgdx.anims.text.FloatingText;
import libgdx.anims.text.FloatingTextMaster;
import libgdx.bf.GridMaster;
import libgdx.bf.generic.ImageContainer;
import libgdx.bf.grid.GridPanel;
import libgdx.bf.grid.cell.BaseView;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.NoHitGroup;
import libgdx.gui.tooltips.SmartClickListener;
import libgdx.stage.camera.MotionData;
import eidolons.content.consts.Images;
import eidolons.content.consts.Sprites;
import libgdx.texture.TextureCache;
import eidolons.system.options.ControlOptions;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.Texts;
import main.content.enums.GenericEnums;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.FontMaster;
import main.system.images.ImageManager;
import main.system.threading.TimerTaskMaster;
import main.system.threading.WaitMaster;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static main.system.GuiEventType.*;

/**
 * Created by JustMe on 5/2/2018.
 */
public class GridCommentHandler extends GridHandler{
    private static final String SEQUENTIAL = "=SEQ=";
    private static final String COMMENT_WAIT_KEY = "[COMMENT_WAIT]";
    public static GridCommentHandler instance;

    FACING_DIRECTION f = FACING_DIRECTION.NORTH;
    private Integer waitCounter = 0;
    private final List<Runnable> commentRunnables =    new ArrayList<>() ;

    public GridCommentHandler(GridPanel panel) {
        super(panel);
        instance = this; //ToDo-Cleanup

    }

    @Override
    protected void bindEvents() {
        GuiEventManager.bind(CLEAR_COMMENTS, p -> {
            for (Runnable commentRunnable : commentRunnables) {
                commentRunnable.run();
            }
            commentRunnables.clear();
        });
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
        Coordinates c;
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

    public ObjectMap<Obj, BaseView> getViewMap() {
        return grid.getViewMap();
    }


    public void comment(CommentData data) {
        //TODO
    }

    public void comment(String img, FACING_DIRECTION facingDirection, Coordinates c, boolean textOnTop, String text, Vector2 at) {
        text = parseColors(text);

        if (!isSequentialComment(text)) {
            main.system.auxiliary.log.LogMaster.devLog("NON Sequential comment: " + text);
//            createFloatText(c, at, facingDirection, text, false);
            String finalText1 = text;
            Gdx.app.postRunnable(() ->
                    commentGdx(img, facingDirection, c, textOnTop, finalText1, at, null, false));
        } else {
            text = removeSequentialKey(text);
            String finalText = text;
            String key;
//       TODO outdated???
//        if (waitCounter > 0)
//                WaitMaster.waitLock(key, 8000);
            waitCounter++;
            key = COMMENT_WAIT_KEY + (getWaitCounter());
            String finalKey = key;

            main.system.auxiliary.log.LogMaster.devLog(key + "Sequential comment: " + text);

            Gdx.app.postRunnable(() -> commentGdx(img, facingDirection, c, textOnTop, finalText, at, finalKey, true));

        }
    }

    private FloatingText createFloatText(Coordinates c, Vector2 at, FACING_DIRECTION facingDirection, String text, AtomicBoolean flag,
                                         boolean seq) {
        Label.LabelStyle style = null;
        if (seq) {
            style = StyleHolder.getSizedLabelStyle(FontMaster.FONT.AVQ, 22);
            flag = new AtomicBoolean(false);
        }

        return FloatingTextMaster.getInstance().createFloatingText
                (VisualEnums.TEXT_CASES.BATTLE_COMMENT, text, new DummyUnit() {
                    @Override
                    public FACING_DIRECTION getFacing() {
                        return facingDirection;
                    }

                    @Override
                    public Coordinates getCoordinates() {
                        return c;
                    }
                }, null, at, style, flag, false);
    }

    public static String removeSequentialKey(String text) {
        return text.replace(SEQUENTIAL, "").trim();
    }

    public static String parseColors(String text) {
        return GdxStringUtils.parseColors(text);
    }

    private boolean isSequentialComment(String text) {
        return text.contains(SEQUENTIAL);
    }

    private void comment(Unit unit, String text, Vector2 at) {
        boolean textTop = false;
        String portrait;
//        if (unit == Eidolons.getMainHero()) {
//            portrait = (Sprites.COMMENT_KESERIM);
//        } else {
//            textTop = true;
        portrait = ImageManager.getBlotch(unit);
//        }

        comment(portrait, unit.getFacing(), unit.getCoordinates(), textTop, text, at);
        LogMaster.devLog(text + "\n - Comment by " + unit.getNameAndCoordinate());
        Core.getGame().getLogManager().log(unit.getName() + ": \n" +
                StringMaster.removeNewLines(text).trim());
    }

    private void commentGdx(String image, FACING_DIRECTION f, Coordinates c, boolean textTop, String text,
                            Vector2 at, String key, boolean seq) {
        AtomicBoolean flag = new AtomicBoolean();
        FloatingText floatingText = createFloatText(c, at, f, text, flag, seq);

        if (!Cinematics.ON && !seq)
            if (OptionsMaster.getControlOptions().getBooleanValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_ON_COMMENTS))
                GuiEventManager.trigger(CAMERA_ZOOM, new MotionData(-10f, 1f));

        SpriteX commentBgSprite = new SpriteX(Sprites.INK_BLOTCH) {
            @Override
            public boolean remove() {
                getParent().remove();
                return super.remove();
            }
        };
//        commentBgSprite=checkReplaceStatic(commentBgSprite);
        if (commentBgSprite.getSprite() == null || commentBgSprite.getSprite().isDefault()
                || commentBgSprite.getSprite().getRegions().size < 1) {
            main.system.auxiliary.log.LogMaster.important("INK BLOTCH IS EMPTY! ");
            commentBgSprite = new SpriteX("ui/INK BLOTCH.png") {
                @Override
                public boolean remove() {
                    getParent().remove();
                    return super.remove();
                }
            };
        } else {
            commentBgSprite.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);
        }
        GroupX portrait = createPortrait(image);
        portrait.setX((int) (commentBgSprite.getWidth() / 2 - portrait.getWidth()));
        portrait.setY((int) (commentBgSprite.getHeight() / 2 - portrait.getHeight()));

        SpriteX commentTextBgSprite = new SpriteX(Sprites.INK_BLOTCH);

        if (commentTextBgSprite.getSprite() == null || commentTextBgSprite.getSprite().isDefault()
                || commentTextBgSprite.getSprite().getRegions().size < 1) {
            main.system.auxiliary.log.LogMaster.important("INK BLOTCH IS EMPTY! ");
            commentTextBgSprite = new SpriteX("ui/INK BLOTCH.png");
        } else
            commentTextBgSprite.setBlending(GenericEnums.BLENDING.INVERT_SCREEN);


        floatingText.setStayFullCondition(delta -> flag.get());
        floatingText.setMaxWidth(seq ? 700 : 600);
        floatingText.init();

        commentBgSprite.getSprite().setAlpha(0.8f);
        commentTextBgSprite.getSprite().setAlpha(0.8f);

        Vector2 finalAt = at;
        SpriteX finalCommentBgSprite = commentBgSprite;
        SpriteX finalCommentTextBgSprite = commentTextBgSprite;
        DIRECTION textPlacement = textTop ? DIRECTION.DOWN
                : (c.dst(Core.getPlayerCoordinates()) > 4) ? DIRECTION.LEFT : DIRECTION.RIGHT;
        if (finalAt != null) {
            Vector2 v = GridMaster.getCenteredPos(Core.getPlayerCoordinates());
            if (v.x < finalAt.x) {
                textPlacement = DIRECTION.LEFT;
            } else {
                textPlacement = DIRECTION.RIGHT;
            }
        }
        DIRECTION finalTextPlacement = textPlacement;
        GroupX commentGroup = new NoHitGroup() {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                //              TODO do we do click to fade?
//               if (flag == null) {
//                    return null;
//                } else {
//                    if (!flag.get()) {
//                        return null;
//                    }
//                }
                return super.hit(x, y, touchable);
            }

            @Override
            public void act(float delta) {
                if (textTop) {
                    finalCommentTextBgSprite.setScale(0.85f);
                    finalCommentTextBgSprite.setOrigin(finalCommentBgSprite.getWidth() / 4, finalCommentBgSprite.getHeight() / 4);
                    finalCommentTextBgSprite.setRotation(90);
                    finalCommentTextBgSprite.setPosition(finalCommentBgSprite.getWidth() / 2f, -30 - finalCommentBgSprite.getHeight() / 2.5f);
                } else {
                    finalCommentTextBgSprite.setOrigin(finalCommentBgSprite.getWidth() / 2, finalCommentBgSprite.getHeight() / 2);
                    finalCommentTextBgSprite.setRotation(90);
                    if (finalTextPlacement == DIRECTION.RIGHT) {
                        finalCommentTextBgSprite.setPosition(finalCommentBgSprite.getWidth() / 2.5f, -100 - finalCommentBgSprite.getHeight() / 5);
                    } else if (finalTextPlacement == DIRECTION.LEFT) {

                        finalCommentTextBgSprite.setPosition(-finalCommentBgSprite.getWidth() / 2.5f, -100 - finalCommentBgSprite.getHeight() / 5);
                    }
                }

                float bgWidth = finalCommentBgSprite.getWidth();
                float bgHeight = finalCommentBgSprite.getHeight();

                float w = Math.min(floatingText.getLabel().getPrefWidth(), 626);
                float h = Math.min(floatingText.getLabel().getPrefHeight(), 626);
                float x = finalCommentTextBgSprite.getX();
                float y = finalCommentTextBgSprite.getY();

                switch (finalTextPlacement) {
                    case LEFT:
                        x = x - bgHeight / 4 + w * 0.8f / 2 - 30;
                        y = y + (h - y) / 2 + h * 0.57f + 28;
                        break;
                    case RIGHT:
                        x = x - bgWidth / 2 + bgHeight - w * 0.7f - 45;
//                        x = x - bgWidth / 2 + bgHeight - w - 20;
//                        y = y + (h - y) / 2 + h*0.67f + 4;
                        y = y + (h - y) / 2 + h * 0.22f + 68;
                        break;
                    case DOWN:
                        x = x - w / 2 - bgWidth + bgHeight / 2 + 210;
                        if (seq) {
                            y = y + (h - y) / 2 - 5 - h / 6.5f;
                        } else {
                            y = y + (h - y) / 2 - 7 - h / 4.9f;
                        }
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
        ActionMasterGdx.addFadeInAction(commentBgSprite, 2);
        ActionMasterGdx.addFadeInAction(commentTextBgSprite, 2);
        ActionMasterGdx.addFadeInAction(portrait, 3);
//            commentSprite.setScale(0.5f);
        //flip?
        Vector2 v = GridMaster.getCenteredPos(c);
        commentGroup.setPosition(v.x, v.y);
        Coordinates panTo = c.getOffsetByY(2);
        if (at != null) {
            commentGroup.setX(at.x);
            commentGroup.setY(at.y);
            switch (finalTextPlacement) {
                case RIGHT:
                    commentGroup.setX(commentGroup.getX() - 150);
                    break;
                case LEFT:
                    commentGroup.setX(commentGroup.getX() + 150);
                    break;
            }
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
        if (!seq) {
            if (OptionsMaster.getControlOptions().getBooleanValue(ControlOptions.CONTROL_OPTION.CENTER_CAMERA_ON_COMMENTS))
                GuiEventManager.triggerWithParams(CAMERA_PAN_TO, seq ? v : at, true, 3f);
        }


        final boolean[] faded = {false};
        Runnable r = () -> {
            if (faded[0]) {
                return;
            }
            faded[0] = true;
            main.system.auxiliary.log.LogMaster.devLog(key + " key; fade comment   " + text);
            if (key != null) {
                if (flag != null) {
                    flag.set(true);
                }
                WaitMaster.doAfterWait(5000, () -> WaitMaster.unlock(key));
            }
            floatingText.fadeOut();

            grid.getActiveCommentSprites().remove(commentGroup);
//       this will be a mess!     GdxMaster.inputPass();

            ActionMasterGdx.addAction(finalCommentBgSprite, new WaitAction(delta -> Core.getGame().isPaused()));
            ActionMasterGdx.addAction(portrait, new WaitAction(delta -> Core.getGame().isPaused()));
            ActionMasterGdx.addAction(finalCommentTextBgSprite, new WaitAction(delta -> Core.getGame().isPaused()));

            ActionMasterGdx.addFadeOutAction(finalCommentBgSprite, seq ? 6 : 4);
            ActionMasterGdx.addRemoveAfter(finalCommentBgSprite);
            ActionMasterGdx.addFadeOutAction(portrait, seq ? 4 : 3);
            ActionMasterGdx.addFadeOutAction(finalCommentTextBgSprite, seq ? 5 : 4);

            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.COMMENT_DONE, true);
        };
        boolean onInput = !Cinematics.ON && seq;
        WaitMaster.doAfterWait(1000 + text.length() * 3, () ->
        {
            if (onInput) {
                String hotkey = StringMaster.getBracketedPart(text);
                if (hotkey.isEmpty())
                    hotkey = "[Space]";
                String finalHotkey = hotkey;
                Timer timer = TimerTaskMaster.newTimer(new TimerTask() {
                    @Override
                    public void run() {
                        EUtils.showInfoText("Press " + finalHotkey + " to Continue");
                    }
                }, 2500);

                GdxMaster.input();// TODO need it?
                GdxMaster.onPassInput(() -> {
                    r.run();
                    timer.cancel();
                });
            } else {
//           TODO      GdxMaster.onInput(() -> {
//                    r.run();
//                }, null , true);

//            AFTER MIN TIME?!    GdxMaster.onInputGdx(() -> {
//                    r.run();
//                });
            }

//        TODO doesn't work    commentGroup.addListener(getCommentMouseListener(commentGroup, finalFade, key));
        });
        {
        }
        float time = 1200 + text.length() * 42; //HALF TIME! minimum to wait

        //wait if seq already showing?
        grid.setName(seq ? "seq" : "");
        if (grid.getActiveCommentSprites().size() > 0) {
//            Eidolons.onNonGdxThread(() -> {
//                WaitMaster.waitForCondition(max -> panel.getActiveCommentSprites().size() == 0
////                        stream().filter(d->d.getName().contains("seq")).count()==0
//                        , 0);
//                addAndQueueRemoval(commentGroup, seq, onInput, time, r);
////            TODO ?!     panel.getActiveCommentSprites().add(commentGroup);
//            });
//            return;
        }
        addAndQueueRemoval(commentGroup, seq, onInput, time, r);
    }

    private void addAndQueueRemoval(GroupX commentGroup, boolean seq, boolean onInput, float time, Runnable r) {
        grid.addActor(commentGroup);
        grid.getCommentSprites().add(commentGroup);
        if (seq) {
            grid.getActiveCommentSprites().add(commentGroup);
        }
        commentRunnables.add(r);
//        commentMap.put(co)
        if (!onInput)
            WaitMaster.doAfterWait((int) time, () -> {
                GdxMaster.onInputGdx(r::run);

                WaitMaster.WAIT((int) time);
                r.run();
                GdxMaster.input();
            });
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
                if (getTapCount() == 1) {
                    return;
                }
                main.system.auxiliary.log.LogMaster.devLog(" manual fade   " + key);
                fadeRunnable.run();
                done = true;
//                GdxMaster.clearPassInput();
                super.clicked(event, x, y);
            }
        };
    }

    public Integer getWaitCounter() {
        return waitCounter;
    }

}
