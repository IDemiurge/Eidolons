package main.system.graphics;

import main.content.PARAMS;
import main.content.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.entity.obj.Obj;
import main.entity.obj.unit.DC_HeroObj;
import main.game.DC_Game;
import main.game.battlefield.Coordinates;
import main.game.battlefield.Coordinates.DIRECTION;
import main.game.battlefield.PointX;
import main.game.battlefield.attack.Attack;
import main.game.battlefield.attack.AttackCalculator.MOD_IDENTIFIER;
import main.libgdx.anims.phased.PhaseAnim;
import main.swing.components.battlefield.DC_BattleFieldGrid;
import main.system.EventCallbackParam;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.*;
import main.system.auxiliary.FontMaster.FONT;
import main.system.auxiliary.secondary.GeometryMaster;
import main.system.graphics.AnimPhase.PHASE_TYPE;
import main.system.graphics.AnimationManager.ANIM_TYPE;
import main.system.graphics.AnimationManager.MOUSE_ITEM;
import main.system.graphics.AnimationManager.MouseItem;
import main.system.images.ImageManager;
import main.system.images.ImageManager.ALIGNMENT;
import main.system.images.ImageManager.BORDER;
import main.system.launch.CoreEngine;
import main.system.options.OptionsMaster;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.TextItem;
import main.system.text.TextItem.TEXT_TYPE;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class PhaseAnimation implements ANIM {

    public static final int MAX_MINI_ICON_SIZE = 40;
    public static final int CENTERED_Y = -9999;
    public static final int CENTERED_X = -6666;
    public static final int TARGET_ONLY = 10;
    public static final int SOURCE_ONLY = 20;
    protected static final int DEFAULT_MAX_PHASES = 3;
    protected static int ID = 0;
    protected int drawMode;
    protected ANIM_TYPE type;
    // protected int status;
    // protected int phase;
    // protected List<Integer> statuses = new LinkedList<>();
    protected List<AnimPhase> phases = new LinkedList<>();
    protected List<AnimPhase> subPhases = new LinkedList<>();
    protected AnimPhase phase;
    protected int index = 0;
    protected int baseTime;
    protected int timeRemaining;
    protected int id;
    protected int maxStatus;
    protected boolean started;
    protected String idString;
    protected Object key;
    protected DC_Game game;
    protected int zoom;
    protected int h;
    protected int w;
    protected Map<Rectangle, MouseItem> mouseMap;
    protected boolean paused;
    protected int offsetX;
    protected int offsetY;
    protected Graphics g;
    protected DC_HeroObj source;
    protected Obj target;
    protected Point sourcePoint;
    protected Point targetPoint;
    protected Coordinates sourceCoordinates;
    protected Coordinates targetCoordinates;
    protected Timer timer;
    protected Rectangle area;
    protected boolean finished;
    protected Font font;
    protected boolean drawTextBackground;
    protected Stack<AnimPhase> bufferedPhases;
    protected int fontHeight;
    protected Map<Integer, List<AnimPhase>> parallelPhases;
    protected List<AnimPhase> staticPhases;
    protected Map<Rectangle, TextItem> tooltipMap;
    protected boolean drawing;
    protected boolean drawn;
    protected boolean pending;
    protected Map<Image, PHASE_TYPE> subPhaseTypeMap;
    protected Map<Image, List<String>> subPhaseTooltipMap;
    protected Boolean drawOnTargetOrSource;
    protected ALIGNMENT alignmentX;
    protected ALIGNMENT alignmentY;
    protected List<PHASE_TYPE> phaseFilter;
    protected boolean autoFinish = true;
    protected boolean freeze;
    protected boolean stackDrawingOn;
    protected int offsetTargetY;
    protected int offsetSourceX;
    protected int offsetSourceY;
    protected int offsetTargetX;
    protected boolean autoResetDrawParam = true;
    protected boolean running;
    protected boolean replay;
    protected Rectangle areaSource;
    protected Rectangle areaTarget;
    protected boolean thumbnail;
    protected Integer thumbnailIndex;
    PhaseAnim phaseAnim;
    private int staticPhaseOffsetY;
    private int staticPhaseOffsetX;
    private int offsetGenericsX;
    private int offsetGenericsY;
    private boolean genericsAbove;
    private boolean logged;
    private int staticOffsetY = 0;
    private int staticOffsetX = 0;
    private boolean flippingDisabled;
    private boolean debugInfoDrawDisabled;
    private boolean flipOver;

    public PhaseAnimation(ANIM_TYPE type) {
        this(type, OptionsMaster.getAnimPhasePeriod(), DEFAULT_MAX_PHASES);
    }

    public PhaseAnimation(ANIM_TYPE type, int time, int phases) {
        this.type = type;
        this.id = ID;
        baseTime = time;
        maxStatus = phases;
        ID++;
        timeRemaining = baseTime;
        font = getFontNeutral();
        fontHeight = FontMaster.getFontHeight(font);
        drawTextBackground = isDrawTextBackgroundAutomatically();
    }

    protected boolean isAutoFinishDefault() {
        return true;
    }

    /**
     * Invoke before calling super.paint()
     */
    // public void start() {
    //
    // }
    public void run() {
        if (isLogged()) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "RUNNING ANIM: " + this);
        }
        Chronos.mark(getIdentifierString());
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(getUpdateTask(), 0, getUpdatePeriod());
        started = true;
        running = true;
        setPending(false);

    }

    public boolean updatePoints() {
        sourcePoint = getGrid().getGridComp().getPointForCoordinateWithOffset(
                getSourceCoordinates());
//        sourcePoint =
//         GameScreen.getInstance().getPointForCoordinateWithOffset(getSourceCoordinates());
        if (sourcePoint.getX() < 0) {
            return false;
        }
        if (sourcePoint.getY() < 0) {
            return false;
        }

        targetPoint = getGrid().getGridComp().getPointForCoordinateWithOffset(
                getTargetCoordinates());
        if (targetPoint.getX() < 0) {
            return false;
        }
        return targetPoint.getY() >= 0;

    }

    public void start() {
        if (isLogged()) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "STARTED ANIM: " + this);
        }
        // start() all *pending* anims on paint() ?
        getGame().getAnimationManager().newAnimation(this);
        index = 0;
        timeRemaining = baseTime;
        phase = (AnimPhase) ListMaster.getListItem(phases, index);

        setPending(true);
        setFinished(false);
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public void toggleThumbnail() {
        thumbnail = !thumbnail;
    }

    public void drawThumbnail(boolean source, Point offsetPoint) {
        String text = getThumbnailText();
        Image image = getThumbnailImage();
        int x = offsetPoint.x;
        int y = offsetPoint.y;
        int width = Math.max(FontMaster.getStringWidth(font, text), image.getWidth(null));
        int height = image.getHeight(null);
        y += height;
        drawOnTarget(image, x, y);
        if (!text.isEmpty()) {
            height += drawTextOn(source, text, font, x, y, getDefaultTextColor());
        }
        addMouseItem(source, x, y, width, height, MOUSE_ITEM.THUMBNAIL);
    }

    protected abstract Image getThumbnailImage();

    protected String getThumbnailText() {
        return getIdentifierString();
    }

    protected boolean drawPhase(AnimPhase phase) {
        return false;
    }

    protected TimerTask getUpdateTask() {

        return new TimerTask() {
            @Override
            public void run() {
                update();
            }

        };
    }

    public void update() {
        if (checkTime()) {
            game.getAnimationManager().updateLastThumbnail(this);
            finished();
        }
    }

    protected void finished() {
        paused = false;
        running = false;
        replay = false;
        setFinished(true);
        timer.cancel();

        // if (!isThumbnail())
        // remove();
    }

    public void remove() {
        game.getAnimationManager().removeAnimation(this);

    }

    public long getUpdatePeriod() {
        return 100;
    }

    public void donePhase() {
        if (isOnLastPhase())
            // if set to false, always freeze
            // if not, check defaul

        {
            if (!autoFinish || !isAutoFinishDefault()) {
                if (!freeze) {
                    if (isLogged()) {
                        LogMaster.log(LogMaster.ANIM_DEBUG, "Anim FROZEN: "
                                + this);
                    }
                    freeze = true;
                }
                return;
            }
        }
        moveAnimation(true);

        playSound();
        timeRemaining = baseTime;

    }

    protected boolean isOnLastPhase() {
        if (phase == null) {
            return false;
        }
        int size = getPhases().size();
        if (size < 1) {
            return false;
        }
        return (phase == getPhases().get(size - 1));
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        Chronos.mark(getIdentifierString());
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    protected void moveAnimation(boolean forward) {

        if (forward) {
            index++;
        } else {
            index--;
        }
        if (index >= phases.size() || index < 0) {
            if (flipOver) {
                if (index >= phases.size()) {
                    index = 0;
                } else if (index < 0) {
                    index = phases.size();
                }
            }
            phase = null;
        } else {
            freeze = false;
            phase = phases.get(index);
            if (phaseFilter != null) {
                if (!phaseFilter.contains(phase.getType())) {
                    moveAnimation(forward);
                    return;
                }
            }
        }
//        mouseMap.clear();
        //      repaint();

        if (getPhaseAnim() != null) {
            GuiEventManager.trigger(GuiEventType.UPDATE_PHASE_ANIM,
                    new EventCallbackParam(getPhaseAnim()));
        }

    }

    public boolean checkTime() {
        if (isPaused()) {
            return false;
        }
        timeRemaining = (int) (baseTime - Chronos.getTimeElapsedForMark(getIdentifierString()));
        if (freeze) {
            return false;
        }
        if (timeRemaining <= 0) {
            donePhase();
            if (checkDone()) {
                if (isLogged()) {
                    LogMaster.log(LogMaster.ANIM_DEBUG, "Anim DONE: " + this);
                }
                return true;
            } else {
                if (isLogged()) {
                    LogMaster.log(LogMaster.ANIM_DEBUG, "Next phase for "
                            + this + ": " + phase);
                }

                Chronos.mark(getIdentifierString());
            }
        }
        // else
        // refresh();
        phase = (AnimPhase) ListMaster.getListItem(phases, index);

        return false;
    }

    public void addParallelPhase(AnimPhase animPhase) {
        AnimPhase parentPhase = getPhase(animPhase.getType());
        if (parentPhase == null) {
            addParallelPhase(animPhase, phases.size());
        } else {
            int index = phases.indexOf(parentPhase);
            if (index < 0) {
                return;
            }
            addParallelPhase(animPhase, index);
        }

    }

    public void addStaticPhase(AnimPhase animPhase) {
        addStaticPhase(animPhase, true);

    }

    public void addStaticPhase(AnimPhase animPhase, boolean replace) {
        if (replace) {
            for (AnimPhase a : getStaticPhases()) {
                if (a.getType() == animPhase.getType()) {
                    getStaticPhases().remove(a);
                }
            }
        }
        if (isLogged()) {
            LogMaster.log(LogMaster.ANIM_DEBUG, "Static Phase added: "
                    + animPhase);
        }
        getStaticPhases().add(animPhase);
    }

    public void addParallelPhase(AnimPhase animPhase, int index) {
        MapMaster.addToListMap(getParallelPhases(), index, animPhase);

    }

    public void addPhase(AnimPhase animPhase) {
        addPhase(animPhase, phases.size());
    }

    public void addPhaseArgs(PHASE_TYPE type, Object... args) {
        addPhaseArgs(false, type, args);

    }

    public void addPhaseArgs(boolean ifNotNull, PHASE_TYPE type, Object... args) {
        AnimPhase PHASE = getPhase(type);
        if (PHASE == null) {
            if (ifNotNull) {
                return;
            }
            addPhase(new AnimPhase(type, args));
        } else {
            PHASE.addArgs(args);
        }
    }

    public void addPhase(AnimPhase animPhase, int index) {
        if (animPhase.getType().isSubPhase()) {
            // AnimPhase enclosing =
            // getPhase(animPhase.getType().getEnclosingPhase());
            addSubPhase(animPhase);
        } else {
            phases.add(index, animPhase);
        }
        if (isLogged()) {
            if (isLogged()) {
                LogMaster.log(LogMaster.ANIM_DEBUG, animPhase
                        + " phase added to " + this);
            }
        }
    }

    public void addSubPhase(AnimPhase animPhase) {
        subPhases.add(animPhase);
    }

    public AnimPhase getPhase(PHASE_TYPE type) {
        for (AnimPhase phase : phases) {
            if (phase.getType() == type) {
                return phase;
            }
        }
        for (AnimPhase phase : subPhases) {
            if (phase.getType() == type) {
                return phase;
            }
        }
        return null;
    }

    public boolean checkDone() {
        return (phase == null);
    }

    public void playSound() {
    }

    protected boolean drawGenerics() {
        if (isGhostDrawn(true)) //
            // TODO draw on origin cell for move anims
        {
            if (getTarget() instanceof DC_HeroObj) {
                if (!getTarget().getCoordinates().equals(getTargetCoordinates())
                        || getTarget().isDead()) {
                    drawGhost((DC_HeroObj) getTarget(), getTargetCoordinates());
                }
            }
        }
        if (isGhostDrawn(false)) {
            if (!getSource().getCoordinates().equals(getSourceCoordinates())
                    || getSource().isDead()) {
                drawGhost(getSource(), getSourceCoordinates());
            }
        }

        return true;
    }

    protected boolean isGhostDrawn(Boolean target) {
        // return isReplay();
        return true;
    }

    protected void drawGhost(DC_HeroObj unit, Coordinates c) {
        Image image = ImageTransformer.getTransparentImage(ImageManager
                .getBufferedImage(ImageManager.getSizedVersion(unit.getImagePath(),
                        GuiManager.getCellHeight()).getImage()), 0.33);

        // TODO real x y
        Point p = game.getBattleField().getGrid().getGridComp().getPointForCoordinateWithOffset(c);

        drawImage(g, image, p.x + (GuiManager.getCellWidth() - GuiManager.getCellHeight()) / 2, p.y);

    }

    public Point getPointForCoordinate(Coordinates coordinates) {
        return game.getBattleField().getGrid().getGridComp().getPointForCoordinateWithOffset(
                coordinates);
    }

    protected boolean isReplay() {
        return replay;
    }

    public void setReplay(boolean replay) {
        this.replay = replay;
    }

    protected void drawFinalGenerics() {
        offsetX += offsetGenericsX;
        offsetY += offsetGenericsY;
        if (genericsAbove) {
            offsetY -= h;
        }
        if (!flippingDisabled) {
            drawControls();
        }
        drawLabel();
        if (!flippingDisabled) {
            drawPhaseNumbers();
        }
    }

    public boolean isGenericsAbove() {
        return genericsAbove;
    }

    public void setGenericsAbove(boolean genericsAbove) {
        this.genericsAbove = genericsAbove;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    protected void drawLabel() {
        Font font = FontMaster.getFont(FONT.MAIN, 18, Font.PLAIN);
        String text = getPhaseLabel();
        int x = MigMaster.getCenteredTextPosition(text, font, w);
        int y = h;
        if (isGenericsDrawnOnSource()) {
            drawTextOnSource(text, font, x, y);
        } else {
            drawTextOnTarget(text, font, x, y);
        }

    }

    protected PHASE_TYPE getSubPhaseTypeForKey(String string) {
        return getSubPhaseTypeForKey(new EnumMaster<MOD_IDENTIFIER>().retrieveEnumConst(
                MOD_IDENTIFIER.class, string));
    }

    protected void setDrawOnTarget(boolean flag) {
        if (flag) {
            drawOnTargetOrSource = true;
        } else {
            drawOnTargetOrSource = null;
        }
    }

    protected void setDrawOnSource(boolean flag) {
        if (flag) {
            drawOnTargetOrSource = false;
        } else {
            drawOnTargetOrSource = null;
        }
    }

    public void setAlignmentX(ALIGNMENT al) {
        alignmentX = al;
    }

    public void setAlignmentY(ALIGNMENT al) {
        alignmentY = al;
    }

    public void resetDrawingParams() {
        alignmentY = null;
        alignmentX = null;
        drawOnTargetOrSource = null;
    }

    protected PHASE_TYPE getSubPhaseTypeForKey(MOD_IDENTIFIER id) {
        if (id != null) {
            switch (id) {
                case ATK_DEF:
                    return PHASE_TYPE.ATTACK_DEFENSE;
                case WEAPON:
                    return PHASE_TYPE.ATTACK_WEAPON_MODS;
                case POS:
                    return PHASE_TYPE.ATTACK_POSITION_MODS;
                case ACTION:
                    return PHASE_TYPE.ATTACK_ACTION_MODS;
                case EXTRA_ATTACK:
                    return PHASE_TYPE.ATTACK_EXTRA_MODS;
                case RANDOM:
                    return PHASE_TYPE.DICE_ROLL;
                case CRIT:
                    return PHASE_TYPE.ATTACK_CRITICAL;
            }
        }
        if (isLogged()) {
            LogMaster
                    .log(LogMaster.ANIM_DEBUG, "NO PHASE TYPE FOR ID: " + id);
        }
        return null;
    }

    protected String getPhaseLabel() {
        if (phase != null) {
            if (phase.getType() == PHASE_TYPE.PRE_ATTACK) {
                Attack attack = (Attack) phase.getArgs()[0];
                return attack.getAction().getName();
            }
            return phase.getType().getLabelName(getArg()); // StringMaster.getWellFormattedString(phase.getType().getLabelName());
        }
        return StringMaster.getWellFormattedString(type.toString());
    }

    protected void initArea() {
        areaSource = new Rectangle(sourcePoint.x + getFullOffset(true, true), sourcePoint.y
                + getFullOffset(true, false), w, h);
        areaTarget = new Rectangle(targetPoint.x + getFullOffset(false, true), targetPoint.y
                + getFullOffset(false, false), w, h);

        if (isGenericsDrawnOnSource()) {
            area = areaSource;
        } else {
            area = areaTarget;
        }
    }

    protected void drawControls() {
        Point p1 = new Point(-getControlImage(true).getWidth(null), (h - getControlImage(true)
                .getHeight(null) / 2));
        Point p2 = new Point(w, (h - getControlImage(true).getHeight(null) / 2));
        int width = getControlImage(false).getWidth(null);
        int height = getControlImage(false).getHeight(null);
        addMouseItem(false, p1.x, p1.y, width, height, MOUSE_ITEM.CONTROL_BACK);
        addMouseItem(false, p2.x, p2.y, width, height, MOUSE_ITEM.CONTROL_FORWARD);

        drawOnTarget(getControlImage(false), p1.x, p1.y);
        drawOnTarget(getControlImage(true), p2.x, p2.y);

    }

    protected void drawPhaseNumbers() {
        // golden circle
        String str = (1 + index) + "/" + (phases.size());
        int x = w + getControlImage(true).getWidth(null);
        int y = h + fontHeight / 3;
        str += StringMaster.getStringXTimes(getBufferedPhases().size(), "*");
        if (isGenericsDrawnOnSource()) {
            drawTextOnSource(str, FontMaster.getFont(FONT.MAIN, 18, Font.PLAIN), x, y);
        } else {
            drawTextOnTarget(str, FontMaster.getFont(FONT.MAIN, 18, Font.PLAIN), x, y);
        }

    }

    protected boolean isGenericsDrawnOnSource() {
        return false;
    }

    public Map<Rectangle, TextItem> getTooltipMap() {
        if (tooltipMap == null) {
            tooltipMap = new HashMap<>();
        }
        return tooltipMap;
    }

    protected void addToolTip(boolean source, Image image, int x, int y, String text) {
        Rectangle rect = new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
        addToolTip(source, rect, x, y, text);
    }

    protected void addToolTip(boolean source, Rectangle rect, int x, int y, Object... lines) {
        Point point = new Point(x + (source ? sourcePoint.x : targetPoint.x), y
                + (source ? sourcePoint.y : targetPoint.y));
        tooltipMap.put(rect, new TextItem(point, TEXT_TYPE.ANIMATION, lines));
    }

    protected void addMouseItem(boolean source, int x, int y, int width, int height, MouseItem item) {
        Point point = new Point(0, 0);// source ? sourcePoint : targetPoint;
        setDrawOnTargetOrSource(!source);
        x = adjustX(x, w);
        y = adjustY(y, h);
        x -= staticOffsetX;
        y -= staticOffsetY;
        point = new Point(point.x + x, point.y + y);
        Rectangle rectangle = new Rectangle(point.x, point.y, width, height);

        item.setPoint(point);
        item.setRectangle(rectangle);
        mouseMap.put(rectangle, item);
        // resetDrawingParams();
    }

    protected void drawOn(boolean source, Image image, int x, int y) {
        if (source) {
            drawOnSource(image, x, y);
        } else {
            drawOnTarget(image, x, y);
        }
    }

    protected void drawOnTarget(Image image, int x, int y) {
        setDrawOnTarget(true);
        drawImage(g, image, x, y);
    }

    protected int drawTextOnSource(String str, Font font, int x, int y, Color c) {
        setDrawOnSource(true);
        return drawText(str, font, x, y, c);
    }

    protected int drawTextOnTarget(String str, int x, int y, Color c) {
        return drawTextOnTarget(str, font, x, y, c);
    }

    protected void drawTextOnTarget(String string, int x, int y) {
        setDrawOnTarget(true);
        drawText(string, font, x, y);
    }

    protected int drawTextOnTarget(String str, Font font, int x, int y, Color c) {
        setDrawOnTarget(true);
        return drawText(str, font, x, y, c);

    }

    protected int drawText(String str, Font font, int x, int y) {
        return drawText(str, font, x, y, getDefaultTextColor());
    }

    protected int drawText(String str, Font font, int x, int y, Color c) {
        setFont(font);
        // TODO refactor drawOn()
        x = adjustX(x, FontMaster.getStringWidth(font, str));
        y = adjustY(y, FontMaster.getFontHeight(font));
        // TODO min/max?
        if (isAutoResetDrawParam()) {
            resetDrawingParams();
        }

        if (getDrawMode() == SOURCE_ONLY) {
            if (areaTarget.contains(new Point(x, y))) {
                return 0;
            }
        }
        if (getDrawMode() == TARGET_ONLY) {
            if (areaSource.contains(new Point(x, y))) {
                return 0;
            }
        }
        if (isLogged()) {
            if (isLogged()) {
                LogMaster.log(LogMaster.ANIM_DEBUG,
                        str + " anim text at "
                                + new PointX(x, y));
            }
        }
        if (isDrawTextBackground()) {
            drawTextBackground(str, font, x, y + fontHeight / 2 + 3);
        }
        g.setColor(c);
        g.drawString(str, x, y + fontHeight / 2 + 3);
        return fontHeight + 3;
    }

    protected int adjustX(int x, int width) {
        if (x == CENTERED_X) {
            x = MigMaster.getAlignmentPosition(ALIGNMENT.CENTER, width, w);
        } else if (alignmentX != null) {
            x = MigMaster.getAlignmentPosition(alignmentX, width, w);
        }
        if (drawOnTargetOrSource != null) {
            x += drawOnTargetOrSource ? offsetTargetX : offsetSourceX;
            x += drawOnTargetOrSource ? targetPoint.x : sourcePoint.x;
        }
        x += offsetX;
        return x;
    }

    protected int adjustY(int y, int height) {
        if (y == CENTERED_Y) {
            y = MigMaster.getAlignmentPosition(ALIGNMENT.CENTER, height, w);
        } else if (alignmentY != null) {
            y = MigMaster.getAlignmentPosition(alignmentY, height, h);
        }
        if (drawOnTargetOrSource != null) {
            y += drawOnTargetOrSource ? targetPoint.y : sourcePoint.y;
            y += drawOnTargetOrSource ? offsetTargetY : offsetSourceY;
        }
        y += offsetY;
        return y;
    }

    protected void setFont(Font font) {
        this.font = font;
        g.setFont(font);
        fontHeight = FontMaster.getFontHeight(font);
    }

    protected boolean isDrawTextBackgroundAutomatically() {
        return true;
    }

    protected boolean isDrawTextBackground() {
        return drawTextBackground;
    }

    protected void drawTextBackground(String str, Font font, int x, int y) {
        g.setColor(ColorManager.BACKGROUND_MORE_TRANSPARENT);
        g.fillRect(x - 7, y - FontMaster.getFontHeight(font) / 2 - 4, FontMaster.getStringWidth(
                font, str) + 13, FontMaster.getFontHeight(font) / 2 + 10);
    }

    protected void fillTextRowWithDarkBackground(boolean source, int base, Font font) {

        Point p = source ? sourcePoint : targetPoint;

        g.setColor(ColorManager.BACKGROUND_MORE_TRANSPARENT);

        g.fillRect(p.x, p.y + base - FontMaster.getFontHeight(font) / 2 - 4, w, FontMaster
                .getFontHeight(font) / 2 + 10);

    }

    protected int drawTextColored(boolean source, Boolean negative, String str, Font font, int x,
                                  int y) {
        Color c;
        boolean enemy = ((source ? this.source : getTarget())).getOwner().isEnemy();
        if (negative == null) {
            c = getDefaultTextColor(); // ?
        } else {
            if (negative) {
                c = enemy ? ColorManager.CRIMSON : ColorManager.GREEN;
            } else {
                c = !enemy ? ColorManager.CRIMSON : ColorManager.GREEN;
            }
        }
        if (source) {
            return drawTextOnSource(str, font, x, y, c);
        }
        return drawTextOnTarget(str, font, x, y, c);

    }

    protected int drawTextOnTarget(String str, Font font, int x, int y) {
        return drawTextOnTarget(str, font, x, y, getDefaultTextColor());
    }

    protected Color getDefaultTextColor() {
        return ColorManager.GOLDEN_WHITE;
    }

    protected int drawTextOn(boolean source, String str, Font font, int x, int y, Color c) {
        if (source) {
            return drawTextOnSource(str, font, x, y, c);
        }
        return drawTextOnTarget(str, font, x, y, c);
    }

    protected int drawTextOnSource(String str, Font font, int x, int y) {
        return drawTextOnSource(str, font, x, y, getDefaultTextColor());
    }

    protected Font getFontNegative() {
        return getFontNeutral();
    }

    protected Font getFontNeutral() {
        Font font = FontMaster.getFont(FONT.AVQ, 18, Font.PLAIN);
        return font;
    }

    protected void drawOnTargetCenter(Image image) {
        drawImage(g, image, targetPoint.x + (w - image.getWidth(null)) / 2, targetPoint.y
                + (h - image.getHeight(null)) / 2);
    }

    protected int drawOnTargetCenterX(Image image, int y) {
        int x = (w - image.getWidth(null)) / 2;
        drawImage(g, image, targetPoint.x + x, targetPoint.y + (y));
        return x;
    }

    protected int drawOnTargetCenterY(Image image, int x) {
        int y = targetPoint.y + (h - image.getHeight(null)) / 2;
        drawImage(g, image, targetPoint.x + (x), y);
        return y;
    }

    protected void drawOnSource(Image image, int x, int y) {
        setDrawOnSource(true);
        drawImage(g, image, x, y);
    }

    protected void drawImage(Graphics g, Image image, int x, int y) {
        // TODO

        x = adjustX(x, image.getWidth(null));
        y = adjustY(y, image.getHeight(null));
        if (isAutoResetDrawParam()) {
            resetDrawingParams();
        }
        if (getDrawMode() == SOURCE_ONLY) {
            if (areaTarget != null) {
                if (areaTarget.contains(new Point(x, y))) {
                    return;
                }
            }
        }
        if (getDrawMode() == TARGET_ONLY) {
            if (areaSource != null) {
                if (areaSource.contains(new Point(x, y))) {
                    return;
                }
            }
        }

        if (isLogged()) {
            LogMaster.log(LogMaster.ANIM_DEBUG, image.getWidth(null)
                    + "-wide image text at " + new PointX(x, y));
        }
        g.drawImage(image, x, y, null);
    }

    protected boolean isAutoResetDrawParam() {
        return autoResetDrawParam;
    }

    protected Image getControlImage(boolean forward) {
        // TODO sized!
        return ImageManager.getArrowImage(false, forward, getArrowVersion());
    }

    protected int getArrowVersion() {
        return 5;
    }

    protected DC_Game getGame() {
        if (game == null) {
            game = DC_Game.game;
        }
        return game;
    }

    public void setFlippingDisabled(boolean b) {
        flippingDisabled = b;
    }

    public void pageFlipped(boolean forward) {
        if (flippingDisabled) {
            return;
        }
        if (isSubPhaseOpen()) {
            if (forward) {
                if (index == phases.size() - 1) {
                    SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                    return;
                }
            }
            if (index == 0) {
                SoundMaster.playStandardSound(STD_SOUNDS.CLICK_ERROR);
                return;
            }

        }
        pause();
        moveAnimation(forward);
        if (phase == null) {

            // TODO remove();
            finished();
            SoundMaster.playStandardSound(STD_SOUNDS.SCROLL);
        } else {
            SoundMaster.playStandardSound(STD_SOUNDS.SLING);
        }
    }

    protected boolean isSubPhaseOpen() {
        return !getBufferedPhases().isEmpty();
    }

    protected void repaint() {
        if (CoreEngine.isSwingOn()) {
            getGrid().getGridComp().getPanel().repaint();
        }
    }

    protected void drawDebugInfo() {
        g.setColor(ColorManager.GOLDEN_WHITE);
        for (Rectangle rect : mouseMap.keySet()) {
            Graphics2D g2d = (Graphics2D) g;
            // g2d.setStroke(new BasicStroke(width, cap, join, miterlimit, dash,
            // dash_phase)
            // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            // 0.75f));
            g2d.drawRect(rect.x + staticOffsetX, rect.y + staticOffsetY, rect.width, rect.height);
            // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
            // 1));
        }
    }

    public boolean draw(Graphics g) {
        area = null;
        AnimPhase phase = this.phase;
        mouseMap = new HashMap<>();
        if (phase == null) {
            return false;
        }
        if (phase.getType() == null) {
            return false;
        }
        drawing = true;
        initDrawing(g);
        boolean result = false;
        if (isThumbnail()) {
            int x = getFullOffset(isGenericsDrawnOnSource(), true);
            int y = getFullOffset(isGenericsDrawnOnSource(), false);
            // if (isOnEdge())
            x += thumbnailIndex * 32;
            y += thumbnailIndex * 32;

            Point offsetPoint = new Point(x, y);
            drawThumbnail(isGenericsDrawnOnSource(), offsetPoint);
        } else {
            initArea();
            try {
                if (drawGenerics()) {

                    checkStackedDrawing();

                    result = drawPhase(phase);
                    drawParallelPhases();
                    drawStaticPhases();
                    drawFinalGenerics();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!debugInfoDrawDisabled) {
            drawDebugInfo();
        }
        resetDrawingParams();
        drawing = false;
        drawn = true;
        return result;

    }

    protected void checkStackedDrawing() {
        if (getTarget() instanceof DC_HeroObj) {
            if (!getTarget().equals(getSource())) {
                if (getTargetCoordinates().equals(getSourceCoordinates())) {
                    initStackedDrawing();
                    drawStacked();
                }
            }
        }
    }

    public void drawStacked() {
        drawStackedImage(true);
        drawStackedImage(false);

    }

    private void drawStackedImage(boolean source) {
        Obj obj = source ? this.source : target;
        Image image = ImageManager.getSizedVersion(obj.getImagePath(), getStackedImageSize())
                .getImage();
        int x = 16;
        int y = 16;
        drawOn(source, image, x, y);
        Image img = (source ? BORDER.NEO_ACTIVE_SELECT_HIGHLIGHT
                : BORDER.NEO_ACTIVE_ENEMY_SELECT_HIGHLIGHT).getImage();
        img = ImageManager.getSizedVersion(img, getStackedImageSize() * 100 / 128);

        image = ImageTransformer.getTransparentImage(img, 75);
        int centeredPosition = MigMaster.getCenteredPosition(w, image.getWidth(null)
                // image.getWidth(null),
                // getStackedImageSize()
        ) - 16;
        drawOn(source, image, x + centeredPosition, y + centeredPosition);

        if (!source) {
            drawOn(source, ImageManager.getValueIcon(PARAMS.ACCURACY), MigMaster
                    .getCenteredPosition(getStackedImageSize(), 32), -32);
        }
    }

    public void initStackedDrawing() {
        stackDrawingOn = true;

        DIRECTION d1 = DIRECTION.DOWN_RIGHT;
        DIRECTION d2 = DIRECTION.UP_LEFT;
        // zoom = zoom * factor;
        // consider multiple?

        Point p1 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, getStackedImageSize(),
                getStackedImageSize(), d1);
        Point p2 = GeometryMaster.getFarthestPointInRectangleForImage(w, h, getStackedImageSize(),
                getStackedImageSize(), d2);

        // int maxDisplacement;

        int xOffset = 32;
        int yOffset = 32;
        offsetSourceX = p1.x + xOffset;
        offsetSourceY = p1.y + yOffset;

        offsetTargetX = p2.x - xOffset;
        offsetTargetY = p2.y - yOffset;

        offsetGenericsX = xOffset;
        offsetGenericsY = yOffset * 3;

        staticPhaseOffsetY += -yOffset * 2;
    }

    private int getStackedImageSize() {
        return 96;
    }

    public void setStaticOffsetX(int x) {
        staticOffsetX = x;
    }

    public void setStaticOffsetY(int y) {
        staticOffsetY = y;
    }

    protected void initDrawing(Graphics g) {

        thumbnailIndex = 0;
        offsetX = staticOffsetX;
        offsetY = staticOffsetY;
        offsetTargetX = 0;
        offsetTargetY = 0;
        offsetSourceX = 0;
        offsetSourceY = 0;
        offsetGenericsX = 0;
        offsetGenericsY = 0;
        staticPhaseOffsetX = 0;
        staticPhaseOffsetY = 0;
        stackDrawingOn = false;

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        source.setAnimation(this);
        if (getTarget() instanceof DC_HeroObj) {
            DC_HeroObj unit = (DC_HeroObj) getTarget();
            unit.setAnimation(this);
        }
        this.g = g;
        zoom = getGame().getBattleField().getGrid().getGridComp().getZoom();
        h = GuiManager.getCellHeight() * zoom / 100;
        w = GuiManager.getCellWidth() * zoom / 100;
    }

    protected void drawStaticPhases() {
        for (AnimPhase phase : getStaticPhases()) {
            int y = staticPhaseOffsetY;
            int x = staticPhaseOffsetX;
            try {
                offsetY += y;
                offsetX += x;
                drawPhase(phase);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                offsetY -= y;
                offsetX -= x;
            }

        }
    }

    protected void drawParallelPhases() {
        if (index > -1) {
            if (getParallelPhases().size() > index) {
                for (AnimPhase phase : getParallelPhases().get(index)) {
                    drawPhase(phase);
                }
            }
        }

    }

    public abstract Object getArg();

    public abstract String getArgString();

    public String getIdentifierString() {
        if (idString == null) {
            idString = "[" + type + " ANIMATION] for " + getArgString() + " " + id;
        }
        return idString;

    }

    public Rectangle getArea() {
        return area;
    }

    public boolean subPhaseClosed() {
        if (getBufferedPhases().isEmpty()) {
            return false;
        }

        phase = getBufferedPhases().pop();
        mouseMap.clear();
        repaint();
        return true;
    }

    protected Color getColorForModifier(Integer mod) {
        Color color = getDefaultTextColor();
        if (mod != 0) {
            color = mod > 0 ? ColorManager.GREEN : ColorManager.CRIMSON;
        }
        return color;
    }

    public void subPhaseOpened(AnimPhase subPhase) {
        // if (phase.getType().isSubPhase())
        // subPhaseClosed();
        getBufferedPhases().push(phase);
        phase = subPhase;
        mouseMap.clear();
        repaint();
    }

    @Override
    public abstract ANIM clone();

    @Override
    public ANIM cloneAndAdd() {
        ANIM anim = clone();
        getGame().getAnimationManager().newAnimation((PhaseAnimation) anim);

        return anim;
    }

    @Override
    public String toString() {
        return getIdentifierString() + " with " + phases.size() + " phases, c_time="
                + timeRemaining + ", phase=" + phase;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setThumbnailIndex(Integer thumbnailIndex) {
        this.thumbnailIndex = thumbnailIndex;
    }

    public boolean isStarted() {
        return started;
    }

    public Object generateKey() {
        return type.toString() + id;
    }

    public Map<Integer, List<AnimPhase>> getParallelPhases() {
        if (parallelPhases == null) {
            parallelPhases = new XLinkedMap<>();
        }
        return parallelPhases;
    }

    public List<AnimPhase> getStaticPhases() {
        if (staticPhases == null) {
            staticPhases = new LinkedList<>();
        }
        return staticPhases;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public boolean isVisible() {
        if (!isDrawReady()) {
            return false;
        }
        return area != null;
        // Coordinates c = getGrid().getOffsetCoordinate();
        // // getGrid().getPointForCoordinateWithOffset(new Coordinates (0, 0))
        // ;
        //
        // boolean intersects = new Rectangle(c.x, c.y, getGrid().getWidth(),
        // getGrid().getHeight())
        // .intersects(areaSource);
        // if (intersects)
        // return true; //TODO Fix with real coordinates
        // intersects = new Rectangle(c.x, c.y, getGrid().getWidth(),
        // getGrid().getHeight())
        // .intersects(areaTarget);
        // return intersects;

    }

    protected DC_BattleFieldGrid getGrid() {
        return game.getBattleField().getGrid();
    }

    public boolean isDrawReady() {
        if (isPaused()) {
            return true;
        }
        if (isFinished()) {
            return false;
        }
        if (!isStarted()) {
            return false;
        }
        return phase != null;
    }

    public Map<Rectangle, MouseItem> getMouseMap() {
        return mouseMap;
    }

    public boolean isAutoHandled() {
        return true;
    }

    public boolean isManualFlippingSupported() {
        return true;
    }

    public List<String> getTooltip(PARAMETER param) {
        return ListMaster.toStringList(param.getName());
    }

    public List<String> getTooltip(MOD_IDENTIFIER modId) {
        return ListMaster.toStringList(modId.getName());
    }

    public Map<Image, List<String>> getSubPhaseTooltipMap() {
        if (subPhaseTooltipMap == null) {
            subPhaseTooltipMap = new XLinkedMap<>();
        }
        return subPhaseTooltipMap;
    }

    public boolean isWheelSupported() {
        return true;
    }

    public DC_HeroObj getSource() {
        return source;
    }

    public boolean isCameraPanningOn() {
        return true;
    }

    public List<AnimPhase> getPhases() {
        return phases;
    }

    public AnimPhase getPhase() {
        return phase;
    }

    public boolean isStackDrawingOn() {
        return stackDrawingOn;
    }

    public boolean stopAndRemove() {
        boolean result = running;
        finished();
        game.getAnimationManager().removeAnimation(this);
        return result;
    }

    public Obj getTarget() {
        return target;
    }

    public void setAutoFinish(boolean b) {
        autoFinish = b;
    }

    public void setPhaseFilter(PHASE_TYPE... animPhasesToPlay) {
        phaseFilter = new LinkedList<>(Arrays.asList(animPhasesToPlay));
    }

    public void setPhaseFilter(List<PHASE_TYPE> animPhasesToPlay) {
        phaseFilter = animPhasesToPlay;

    }

    public Stack<AnimPhase> getBufferedPhases() {
        if (bufferedPhases == null) {
            bufferedPhases = new Stack<>();
        }
        return bufferedPhases;
    }

    public void setBufferedPhases(Stack<AnimPhase> bufferedPhases) {
        this.bufferedPhases = bufferedPhases;
    }

    public Boolean getDrawOnTargetOrSource() {
        return drawOnTargetOrSource;
    }

    public void setDrawOnTargetOrSource(Boolean drawOnTargetOrSource) {
        this.drawOnTargetOrSource = drawOnTargetOrSource;
    }

    public Coordinates getTargetCoordinates() {
        return targetCoordinates;
    }

    public void setTargetCoordinates(Coordinates c) {
        this.targetCoordinates = new Coordinates(c.x, c.y);
    }

    public Coordinates getSourceCoordinates() {
        return sourceCoordinates;
    }

    public void setSourceCoordinates(Coordinates c) {
        this.sourceCoordinates = new Coordinates(c.x, c.y);
    }

    public boolean overlapsPartly(PhaseAnimation anim, boolean source, boolean x) {
        return overlaps(false, anim, source, x);
    }

    public boolean overlapsFully(PhaseAnimation anim, boolean source, boolean x) {
        return overlaps(true, anim, source, x);
    }

    public boolean overlaps(boolean fully, PhaseAnimation anim, boolean source, boolean x) {
        initArea();
        anim.initArea();
        Rectangle area1 = source ? getAreaSource() : getAreaTarget();
        Rectangle area2 = source ? anim.getAreaSource() : anim.getAreaTarget();
        return fully ? area1.equals(area2) : area1.intersects(area2);

        // if (!getSourceCoordinates().equals(anim.getSourceCoordinates()))
        // return false;
        // int distance = (anim.getOffset(source, x) + anim.getOffset(null, x))
        // - (getOffset(source, x) + getOffset(null, x));
        // if (distance == 0)
        // return true;
        // return false;
    }

    public int getFullOffset(boolean source, boolean x) {
        return getOffset(source, x) + getOffset(null, x);
    }

    public void addOffset(Boolean source_target_all, boolean x, int offset) {
        if (source_target_all == null) {
            if (x) {
                offsetX += offset;
            } else {
                offsetY += offset;
            }
        } else {
            if (source_target_all) {
                if (x) {
                    offsetSourceX += offset;
                } else {
                    offsetSourceY += offset;
                }
            } else {
                if (x) {
                    offsetTargetX += offset;
                } else {
                    offsetTargetY += offset;
                }
            }
        }
    }

    public int getOffset(Boolean source_target_all, boolean x) {
        if (source_target_all == null) {
            return x ? offsetX : offsetY;
        }
        if (source_target_all) {
            return x ? offsetSourceX : offsetSourceY;
        }
        return x ? offsetTargetX : offsetTargetY;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public Point getSourcePoint() {
        return sourcePoint;
    }

    public Point getTargetPoint() {
        return targetPoint;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getOffsetTargetY() {
        return offsetTargetY;
    }

    public void setOffsetTargetY(int offsetTargetY) {
        this.offsetTargetY = offsetTargetY;
    }

    public int getOffsetSourceX() {
        return offsetSourceX;
    }

    public void setOffsetSourceX(int offsetSourceX) {
        this.offsetSourceX = offsetSourceX;
    }

    public boolean contains(Point point) {
        if (flippingDisabled) {
            point.setLocation(new Point(point.x + staticOffsetX, point.y + staticOffsetY));
        }
        if (areaTarget != null) {
            if (areaTarget.contains(point)) {
                return true;
            }
        }
        if (areaSource != null) {
            if (areaSource.contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ANIM getFilteredClone(PHASE_TYPE... allowedTypes) {
        ANIM clone = clone();
        clone.getPhases().clear();
        for (PHASE_TYPE type : allowedTypes) {
            AnimPhase animPhase = getPhase(type);
            if (animPhase != null) {
                clone.addPhase(animPhase);
            }
        }

        return clone;
    }

    public int getOffsetSourceY() {
        return offsetSourceY;
    }

    public void setOffsetSourceY(int offsetSourceY) {
        this.offsetSourceY = offsetSourceY;
    }

    public int getOffsetTargetX() {
        return offsetTargetX;
    }

    public void setOffsetTargetX(int offsetTargetX) {
        this.offsetTargetX = offsetTargetX;
    }

    public Rectangle getAreaSource() {
        return areaSource;
    }

    public void setAreaSource(Rectangle areaSource) {
        this.areaSource = areaSource;
    }

    public Rectangle getAreaTarget() {
        return areaTarget;
    }

    public void setAreaTarget(Rectangle areaTarget) {
        this.areaTarget = areaTarget;
    }

    public boolean isLogged() {
//        return logged;
        return LogMaster.ANIM_DEBUG_ON;
    }

    public void setLogged(boolean b) {
        logged = b;
    }

    public void setFlipOver(boolean b) {
        flipOver = b;
    }

    public PhaseAnim getPhaseAnim() {
//        if (phaseAnim==null )return new PhaseAnim(this);
        return phaseAnim;
    }

    public void setPhaseAnim(PhaseAnim phaseAnim) {
        this.phaseAnim = phaseAnim;
    }
}
