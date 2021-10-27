package libgdx.gui.dungeon.menu.selection;

import com.badlogic.gdx.graphics.g2d.Batch;
import libgdx.GDX;
import libgdx.GdxMaster;
import libgdx.gui.dungeon.panels.ScrollPanel;
import libgdx.gui.dungeon.panels.dc.logpanel.text.Message;
import libgdx.gui.dungeon.panels.dc.logpanel.text.ScrollTextWrapper;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/30/2017.
 */
public class DescriptionPanel extends ScrollTextWrapper {
    public DescriptionPanel() {
        super();
    }

    @Override
    protected String getBgPath() {
        return null;
    }

    @Override
    public void initBg() {

    }

    @Override
    public void init() {
        super.init();
        //        scrollPanel.top();
        //        scrollPanel.getTable().bottom();
        //        scrollPanel.getTable().padLeft(20);
    }

    @Override
    protected ScrollPanel<Message> createScrollPanel() {
        return new TextScroll() {
            protected float getUpperLimit() {
                return innerScrollContainer.getHeight() * 0.85f;
            }

                        @Override
                        protected void alignInnerScroll() {
                            super.alignInnerScroll();
                        }

                        @Override
                        protected int getInnerTableAlignment() {
                            return super.getInnerTableAlignment();
                        }
//            @Override
//            protected void alignInnerScroll() {
//                innerScrollContainer.left().bottom();
//            }
//
//            @Override
//            protected int getInnerTableAlignment() {
//                return Align.bottom;
//            }
        };
    }

    @Override
    protected int getInitialYOffset() {
        return super.getInitialYOffset();// -15000;
    }
    @Override
    protected void initAlignmentScroll(ScrollPanel scrollPanel) {
        scrollPanel.left().bottom();
        super.initAlignmentScroll(scrollPanel);
    }

    //    protected void initAlignment() {
    //        left().top();
    //    }
    //
    //    protected int getInnerTableAlignment() {
    //        return Align.top;
    //    }
    //
    //    protected void alignInnerScroll() {
    //        innerScrollContainer.left().bottom();
    //    }

    @Override
    public void layout() {
        super.layout();
        scrollPanel.initScrollListener();
    }

    @Override
    protected int getFontSize() {
        return 19;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.MAIN;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    protected float getDefaultHeight() {
        return GDX.height(600);
    }

    @Override
    protected float getTextLineWidth() {
        return getWidth() * 0.69f;
    }

    @Override
    protected float getDefaultWidth() {
        return GdxMaster.adjustSize(415);
    }
}
