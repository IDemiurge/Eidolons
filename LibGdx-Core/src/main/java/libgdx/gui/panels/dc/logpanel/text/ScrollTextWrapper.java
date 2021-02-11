package libgdx.gui.panels.dc.logpanel.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import libgdx.GdxMaster;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.ScrollPanel;
import libgdx.gui.panels.TablePanelX;
import libgdx.texture.TextureCache;
import libgdx.GdxMaster;
import libgdx.texture.TextureCache;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.Strings;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/29/2017.
 */
public class ScrollTextWrapper extends TablePanelX {
    protected boolean updatePos = false;
    protected float offsetX = 20;
    protected ScrollPanel  scrollPanel;
    private float defaultHeight;
    private float defaultWidth;
    private int fontSize;
    private FONT fontStyle;
    private Image bg;
    private boolean scrollToBottom;
    private int timer;

    public ScrollTextWrapper(float defaultHeight, float defaultWidth) {
        this.defaultHeight = defaultHeight;
        this.defaultWidth = defaultWidth;
        init();
    }

    public ScrollTextWrapper() {
        init();
    }


    public void init() {
        if (getDefaultWidth() != 0)
            setWidth(GdxMaster.adjustSize(getDefaultWidth(), 0.42f));
        if (getDefaultHeight() != 0)
            setHeight(GdxMaster.adjustSize(getDefaultHeight(), 0.42f));

        initScrollPanel();
        initBg();
        if (bg != null)
            bg.setZIndex(0);
        updatePos = true;
    }

    protected void initScrollPanel() {
        if (scrollPanel != null) {
            scrollPanel.remove();
        }
        scrollPanel = createScrollPanel();
        //        scrollPanel.setMaxTableElements()
        scrollPanel.pad(1, 10, 1, 10);
        scrollPanel.fill();

        addActor(scrollPanel);
    }

    protected ScrollPanel  createScrollPanel() {
        return new TextScroll();
    }

    protected void initAlignmentScroll(ScrollPanel scrollPanel) {
        scrollPanel.left().top();
    }

    protected void padScroll(ScrollPanel scrollPanel) {
    }

    public void initBg() {
        if (getBgPath() != null) {
            bg = new Image(TextureCache.getOrCreateR(getBgPath()));

            if (defaultWidth == 0)
                defaultWidth = bg.getImageWidth();
            if (defaultHeight == 0)
                defaultHeight = bg.getHeight();
            bg.setFillParent(true);
            addActor(bg);
        } else {
            setBackground(getNinePatch());
        }
    }

    protected Drawable getNinePatch() {
        return NinePatchFactory.getLightDecorPanelFilledDrawable();
    }

    protected int getFontSize() {
        if (fontSize != 0) {
            return fontSize;
        }
        return 16;
    }

    protected FONT getFontStyle() {
        if (fontStyle != null) {
            return fontStyle;
        }
        return FONT.MAIN;
    }

    protected float getDefaultWidth() {
        if (defaultWidth != 0) {
            return defaultWidth;
        }
        return 400;
    }

    protected float getDefaultHeight() {
        if (defaultHeight != 0) {
            return defaultHeight;
        }
        return 400;
    }

    protected String getBgPath() {
        return null;
    }

    public void setText(String text) {

        scrollPanel.getInnerScrollContainer().getActor().clear();
        //TODO split?!
        for (String substring : ContainerUtils.openContainer(text, Strings.NEW_LINE)) {
            TextBuilder builder = getTextBuilder();
            Message message = builder.addString(substring).build(getTextLineWidth() * 0.92f);
            scrollPanel.addElement(message).width(getTextLineWidth());
        }

    }

    protected float getTextLineWidth() {
        return getWidth();
    }

    protected TextBuilder getTextBuilder() {
        return new TextBuilder() {
            @Override
            protected FONT getFontStyle() {
                return
                 ScrollTextWrapper.this.getFontStyle();
            }

            @Override
            protected int getFontSize() {
                return ScrollTextWrapper.this.getFontSize();
            }

            @Override
            protected float getAdjustCoef() {
                return GdxMaster.fontSizeAdjustCoef;
            }
        };
    }

    protected int getInitialYOffset() {
        return -200;
    }

    protected boolean isScrolledAlways() {
        return false;
    }

    @Override
    public void act(float delta) {
        if (scrollToBottom) {
//            scroll.scrollPane.setScrollY(scroll.scrollPane.getActor().getHeight() + 1100);
            scrollPanel.getScroll().setScrollPercentY(1);
            if (scrollPanel.getScroll(). getVisualScrollY() >= scrollPanel.getScroll().getMaxY()) {
                timer++;
                if (timer >= 10) {
                    scrollToBottom = false;
                    timer = 0;
                }
            }
        }
        super.act(delta);
        if (updatePos) {
            updateAct();
            updatePos = false;
            scrollToBottom = true;
        }
    }

    protected void updateAct() {
        scrollPanel.setBounds(10, 10, getWidth() - 20, getHeight() - 20);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor actor = super.hit(x, y, touchable);
        if (actor == null) {
            return null;
        }

        if (actor instanceof Image) {
            return null;
        }
        return actor;
    }

    public class TextScroll extends ScrollPanel {
        @Override
        protected void initAlignment() {
            initAlignmentScroll(this);
        }

        protected int getInnerTableAlignment() {
            return Align.top;
        }

        @Override
        protected void pad(ScrollPanel scrollPanel) {
            padScroll(scrollPanel);
        }

        @Override
        protected boolean isAlwaysScrolled() {
            return isScrolledAlways();
        }

        @Override
        public int getDefaultOffsetY() {
            return getInitialYOffset();
        }
    }
}
