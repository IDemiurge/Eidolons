package main.libgdx.screens.map.ui.time;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.game.module.adventure.MacroGame;
import main.game.module.adventure.global.GameDate;
import main.game.module.adventure.global.TimeMaster;
import main.libgdx.GdxMaster;
import main.libgdx.StyleHolder;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.gui.panels.GroupX;
import main.libgdx.gui.panels.dc.ButtonStyled.STD_BUTTON;
import main.libgdx.gui.panels.dc.TextButtonX;
import main.libgdx.gui.tooltips.DynamicTooltip;
import main.libgdx.texture.TextureCache;
import main.swing.PointX;
import main.system.GuiEventManager;
import main.system.MapEvent;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

import java.util.HashMap;
import java.util.Map;

import static main.libgdx.screens.map.ui.time.MapTimePanel.MOON.*;


/**
 * Created by JustMe on 2/9/2018.
 * rotate moon circle?
 * control the middle-'sun' brightness
 * cut the circle in two, don't show the upper part...
 * tooltip
 * <p>
 * Month
 * where to show date?
 * perhaps I will now display what later will become a tooltip?
 * date could be displayed in a rim, classic fashion
 * <p>
 * dawn/noon/dusk/night
 */
public class MapTimePanel extends GroupX {

    private static final float SIZE = 64;
    SunActor sun;
    SunActor undersun;
    Map<MOON, MoonActor> moonMap = new HashMap<>();
    MoonActor[] displayedMoons = new MoonActor[3];
    Label timeLabel;
    ImageContainer weave = new ImageContainer();
    ImageContainer mainCircle = new ImageContainer();
    //    Image stoneCircleHighlight = new Image(TextureCache.getOrCreateR(StrPathBuilder.build(PathFinder.getMacroUiPath()
//     , "component", "time panel", "stone circle hl.png")));

    boolean movingUp;
    boolean moving;
    PointX sunPoint = new PointX(103, 128);
    PointX[] points = {new PointX(53, 122), new PointX(134, 91), new PointX(214, 127)};
    PointX timeLabelPoint = new PointX(155, 80);
    PointX timeLabelBgPoint = new PointX(143, 62);
    PointX pauseBtnPoint = new PointX(134, 25);
    PointX speedUpBtnPoint = new PointX(178, 59);
    PointX speedDownBtnPoint = new PointX(110, 59);
    PointX controlsPoint = new PointX(71, 23);
    TextButtonX pauseButton;
    MoonActor activeMoon;
    private TextButtonX speedUpBtn;
    private TextButtonX speedDownBtn;
    private Image labelBg;

    public MapTimePanel() {
        init();
    }

    public String getPath() {
        return StrPathBuilder.build(PathFinder.getMacroUiPath(), "component", "time panel") + StringMaster.getPathSeparator();
    }

    public void init() {
        setSize((355)
         , (203));

        speedUpBtn =
         new TextButtonX(
          "", STD_BUTTON.SPEED_UP, () -> {
             MacroGame.getGame().getLoop().getTimeMaster().speedUp();
         });
        speedDownBtn =
         new TextButtonX(
          "", STD_BUTTON.SPEED_DOWN, () -> {
             MacroGame.getGame().getLoop().getTimeMaster().speedDown();
         });
        pauseButton =
         new TextButtonX(
          "", STD_BUTTON.PAUSE, () -> {
             MacroGame.getGame().getLoop().togglePaused();
             MacroGame.getGame().getLoop().getTimeMaster().resetSpeed();
         });

        float moonSize = GdxMaster.adjustSize(SIZE);
        addListener(new DynamicTooltip(() -> getDateString()).getController());
        //display DatePanel?
        for (MOON moon : MOON.values()) {
            MoonActor container = new MoonActor(moon);
            moonMap.put(moon, container);
        }
        float sunSize = moonSize * 2f;

        sun = new SunActor(false);
        sun.setSize(sunSize, sunSize);
        sun.setVisible(false);

        undersun = new SunActor(true);

        undersun.setSize(sunSize, sunSize);

        undersun.setVisible(false);

        timeLabel = new Label("", StyleHolder.getSizedLabelStyle(FONT.NYALA, 18));

        addActor(weave);
        addActor(mainCircle);
        addActor(sun);
        addActor(undersun);
        addActor(labelBg = new Image(TextureCache.getOrCreateR(getPath() + "label bg.png")));


        addActor(pauseButton);
        addActor(speedUpBtn);
        addActor(speedDownBtn);
        addActor(timeLabel);

        initPositions();


        GuiEventManager.bind(MapEvent.TIME_CHANGED, p -> {
            update((DAY_TIME) p.get());
        });
        sun.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MacroGame.getGame().getLoop().getTimeMaster().newMonth();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        undersun.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                MacroGame.getGame().getLoop().getTimeMaster().newMonth();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
//        adjustSizes();
    }

    private void adjustSizes() {
        for (Actor sub : getChildren()) {
            sub.setScale(GdxMaster.getFontSizeMod());
        }

    }
        private void initPositions() {

        labelBg.setPosition(GdxMaster.adjustPos(true, timeLabelBgPoint.x), GdxMaster.adjustPos(false, timeLabelBgPoint.y));

        pauseButton.setPosition(GdxMaster.adjustPos(true, pauseBtnPoint.x),
         GdxMaster.adjustPos(false, pauseBtnPoint.y));
        speedUpBtn.setPosition(GdxMaster.adjustPos(true, speedUpBtnPoint.x),
         GdxMaster.adjustPos(false, speedUpBtnPoint.y));
        speedDownBtn.setPosition(GdxMaster.adjustPos(true, speedDownBtnPoint.x),
         GdxMaster.adjustPos(false, speedDownBtnPoint.y));
        weave.setPosition(GdxMaster.adjustPos(true, controlsPoint.x), GdxMaster.adjustPos(false, controlsPoint.y));
//        weave.setPosition(GdxMaster.adjustPos(true, controlsPoint.x), GdxMaster.adjustPos(false, controlsPoint.y));
        timeLabel.setPosition(GdxMaster.adjustPos(true, timeLabelPoint.x),
         GdxMaster.adjustPos(false, timeLabelPoint.y));
        sun.setPosition(GdxMaster.adjustPos(true, sunPoint.x),
         GdxMaster.adjustPos(false, sunPoint.y));
        undersun.setPosition(GdxMaster.adjustPos(true, sunPoint.x),
         GdxMaster.adjustPos(false, sunPoint.y));
    }

    private void resetZIndices() {
        mainCircle.setZIndex(0);
        pauseButton.setZIndex(Integer.MAX_VALUE);
        speedUpBtn.setZIndex(Integer.MAX_VALUE);
        speedDownBtn.setZIndex(Integer.MAX_VALUE);
    }


    public void update(DAY_TIME time) {

        if (time.isNight())
            weave.setImage(getPath() + "weave.png");
        else
            weave.setImage(getPath() + "weave.png");

        if (time.isNight())
            mainCircle.setImage(getPath() + StrPathBuilder.build("circle",   "night.png"));
        else
            mainCircle.setImage(getPath() + StrPathBuilder.build("circle",   "day.png"));
//        mainCircle.setImage(getPath() + StrPathBuilder.build("circle", time.toString() + ".png"));
        setSize(GdxMaster.adjustSize(mainCircle.getWidth())
         , GdxMaster.adjustSize(mainCircle.getHeight()));

        mainCircle.setOrigin(mainCircle.getWidth() / 2, mainCircle.getHeight() * 2);

        undersun.setVisible(time.isUndersunVisible());
        sun.setVisible(time.isSunVisible());
        MOON[] moons = getDisplayedMoons();
        main.system.auxiliary.log.LogMaster.log(1, "update: " + time);
        int i = 0;
        for (MoonActor sub : displayedMoons) {
            if (sub != null) sub.remove();
        }
        for (MOON sub : moons) {
            main.system.auxiliary.log.LogMaster.log(1, "added: " + sub);
            //smooth?!
            //or black out on update()?
            displayedMoons[i] = moonMap.get(sub);
            displayedMoons[i].setVisible(true);
            addActor(displayedMoons[i]);
            int offset = 7;
            displayedMoons[i].setPosition(GdxMaster.adjustPos(true, points[i].x - offset),
             GdxMaster.adjustPos(false, points[i].y - offset));
            i++;
        }
        MOON m = TimeMaster.getDate().getMonth().getActiveMoon(time.isNight());
        if (activeMoon != null)
            activeMoon.setActive(false);
        activeMoon = moonMap.get(m);
        activeMoon.setActive(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
    @Override
    public void act(float delta) {
//        initPositions();
        setDebug(false, true);
        float deltaX = delta +
         (delta / 5 * MacroGame.getGame().getLoop().getTimeMaster().getSpeed());

        for (MoonActor moon : displayedMoons) {
            if (moon==null )
                return;
            moon.act(deltaX);
        }
        activeMoon.act(deltaX);
        sun.act(deltaX);
        deltaX = delta * MacroGame.getGame().getLoop().getTimeMaster().getSpeed( );
        undersun.act(deltaX);

        String text = TimeMaster.getDate().getHour() + "";
        int minutes = (int) MacroGame.getGame().getLoop().getTimeMaster().getMinuteCounter();
        if (minutes / 10 == 0) {
            text += ":0" + minutes;
        } else
            text += ":" + minutes;
        timeLabel.setText(text);
        resetZIndices();
//        float r = mainCircle.getRotation();
//        float dx = 0.5f * delta;
//        if (r > 180)
//            movingUp = true;
//        if (movingUp)
//            if (r <= 0)
//                movingUp = false;
//
//        if (movingUp)
//            dx = -dx;
//        stoneCircle.setRotation(r+dx);
    }




    @Override
    public float getHeight() {
        return super.getHeight();
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
    }

    private String getDateString() {
        //show exact time on tooltip?

        GameDate date;
//        getPhase();
        String string = "";
        return string;
    }

    private MOON[] getDisplayedMoons() {
        switch (MacroGame.getGame().getTime()) {
            case MORNING:
                return new MOON[]{
                 HAVEN, TEMPEST, RIME,
                };
            case MIDDAY:
                return new MOON[]{
                 FAE, HAVEN, TEMPEST,
                };
            case DUSK:
                return new MOON[]{
                 FEL, FAE, HAVEN,
                };
            case NIGHTFALL:
                return new MOON[]{
                 SHADE, FEL, FAE,
                };
            case MIDNIGHT:
                return new MOON[]{
                 RIME, SHADE, FEL,
                };
            case DAWN:
                return new MOON[]{
                 TEMPEST, RIME, SHADE
                };
        }
        return new MOON[0];
    }

    public enum MOON {
        FAE, TEMPEST, HAVEN, RIME, FEL, SHADE
    }
}
