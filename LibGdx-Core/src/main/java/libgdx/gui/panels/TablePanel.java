package libgdx.gui.panels;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import libgdx.GdxMaster;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.FlipDrawable;
import libgdx.TiledNinePatchGenerator;
import main.system.launch.Flags;

import java.util.function.Supplier;

public class TablePanel<T extends Actor> extends Table {

    private boolean updateRequired;
    private boolean fixedSize;
    private boolean fixedMinSize;
    private boolean fixedMaxSize;

    public TablePanel() {
    }

    @Override
    public String toString() {
        return getUserObject()+" -\n"+super.toString();
    }

    public void removeBackground() {
        background(new EmptyDrawable());
    }

    @Override
    public <T extends Actor> Cell<T> getCell(T actor) {
        Cell<T> cell = super.getCell(actor);
        if (cell == null) {
            for (Actor actor1 : getChildren()) {
                if (actor1 instanceof Table) {
                    cell = ((Table) actor1).getCell(actor);
                    if (cell != null) {
                        return cell;
                    }
                }
            }
        }
        return cell;
    }

    @Override
    public Table debugAll() {
        if (!Flags.isIDE()) {
            return this;
        }
        return super.debugAll();
    }

    @Override
    public Table debug() {
        if (!Flags.isIDE()) {
            return this;
        }
        return super.debug();
    }

    @Override
    public float getPrefWidth() {
        if (isFixedSize())
            return getWidth();
        return super.getPrefWidth();
    }
    @Override
    public float getPrefHeight() {
        if (isFixedSize())
            return getHeight();
        return super.getPrefHeight();
    }

    @Override
    public float getMinHeight() {
        if (fixedMinSize) {
            return getHeight();
        }
        return super.getMinHeight();
    }

    @Override
    public float getMinWidth() {
        if (fixedMinSize) {
            return getWidth();
        }
        return super.getMinWidth();
    }
    @Override
    public float getMaxHeight() {
        if (fixedMaxSize) {
            return getHeight();
        }
        return super.getMaxHeight();
    }

    @Override
    public float getMaxWidth() {
        if (fixedMaxSize) {
            return getWidth();
        }
        return super.getMaxWidth();
    }

    public boolean isFixedMaxSize() {
        return fixedMaxSize;
    }

    public void setFixedMaxSize(boolean fixedMaxSize) {
        this.fixedMaxSize = fixedMaxSize;
    }
    public boolean isFixedMinSize() {
        return fixedMinSize;
    }

    public void setFixedMinSize(boolean fixedMinSize) {
        this.fixedMinSize = fixedMinSize;
    }

    public Cell<T> addNormalSize(T el) {
        return add(el).size(el.getWidth(), el.getHeight());
    }

    public Cell<T> addNoGrow(T el) {
        return add(el);
    }

    public Cell<T> addElement(T el) {
        return add(el).grow();
    }

    public void addEmptyRow(int w, int h) {
        addEmpty(w, h);
        row();
    }

    public void addEmpty(int w, int h) {
        Actor actor = new Actor();
        actor.setSize(w, h);
        add(actor);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void setBackground(Drawable background) {
        if (!(background instanceof NinePatchDrawable))
            if (background instanceof TextureRegionDrawable) {
                final TextureRegionDrawable drawable = ((TextureRegionDrawable) background);
                final TextureRegion region = drawable.getRegion();
                if (region != null)
                    setSize(region.getRegionWidth(), region.getRegionHeight());
            } else {
                if (background instanceof FlipDrawable) {
                    setSize(
                            background.getMinWidth(),
                            background.getMinHeight());
                }
            }
        super.setBackground(background);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isUpdateRequired() && isVisibleEffectively()) {
            updateAllOnAct(delta);
        }
    }

    protected void updateAllOnAct(float delta) {
        updateAct(delta);
        if (invalidateOnUpdate())
            invalidate();
        afterUpdateAct(delta);
        setUpdateRequired(false);
    }

    protected boolean invalidateOnUpdate() {
        return true;
    }

    protected boolean isVisibleEffectively() {
        return GdxMaster.isVisibleEffectively(this);

    }

    public void afterUpdateAct(float delta) {

    }

    protected void pad(TiledNinePatchGenerator.NINE_PATCH_PADDING patchPadding) {
        pad(patchPadding.top, patchPadding.left, patchPadding.bottom, patchPadding.right);
    }

    public void updateAct(float delta) {

    }

    @Override
    public Object getUserObject() {
        final Object userObject = super.getUserObject();
        if (userObject instanceof Supplier) {
            return ((Supplier) userObject).get();
        } else {
            return userObject;
        }
    }

    @Override
    public void setUserObject(Object userObject) {
        super.setUserObject(userObject);
        setUserObjectForChildren(userObject);
        setUpdateRequired(true);
    }

    protected void setUserObjectForChildren(Object userObject) {
        getChildren().forEach(ch -> ch.setUserObject(userObject));
    }

    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    public TablePanel<T> initDefaultBackground() {
        if (getDefaultBackground() != null)
            setBackground(getDefaultBackground());
        return this;
    }

    protected Drawable getDefaultBackground() {
        return new NinePatchDrawable(NinePatchFactory.getLightPanelFilled());
    }

    public void fadeOut() {
        clearActions();
        ActionMasterGdx.addFadeOutAction(this, 0.25f);
        ActionMasterGdx.addHideAfter(this);
    }

    public void fadeIn() {
        clearActions();
        setVisible(true);
        ActionMasterGdx.addFadeInAction(this, 0.25f);
    }

    public boolean isFixedSize() {
        return fixedSize;
    }

    public void setFixedSize(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public boolean isUpdateRequired() {
        return updateRequired;
    }
}
