package main.level_editor.gui.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import libgdx.StyleHolder;
import libgdx.bf.Hoverable;
import libgdx.bf.grid.cell.BaseView;
import libgdx.gui.tooltips.DynamicTooltip;
import eidolons.content.consts.Images;
import libgdx.texture.TextureCache;
import main.entity.obj.Obj;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class LE_GridCellHighlighter extends DynamicTooltip {
    Hoverable cell;
    public LE_GridCellHighlighter(Hoverable cell) {
        super(null);
        text = ()-> {
            Obj bfObj= cell.getUserObject();
            return LE_UnitViewFactory.getTooltipText(bfObj);
        };
        this.cell = cell;
    }
    @Override
    protected void entered() {
        showing = true;
        mouseHasMoved = false;
        cell.setHovered(true);
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || cell instanceof BaseView) {
            GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, this);
        }
    }

    @Override
    protected void exited() {
        showing = false;
        mouseHasMoved = false;
        cell.setHovered(false);
        GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
    }

    @Override
    protected Drawable getDefaultBackground() {
        return new TextureRegionDrawable(TextureCache.getOrCreateR(Images.ZARK_TITLE));
    }

    @Override
    protected Label.LabelStyle getDefaultLabelStyle() {
        return StyleHolder.getHqLabelStyle(16);
    }

    @Override
    protected boolean checkGuiStageBlocking() {
        return false;
    }
}
