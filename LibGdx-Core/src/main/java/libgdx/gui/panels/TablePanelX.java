package libgdx.gui.panels;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import libgdx.GdxMaster;
import main.system.auxiliary.ClassMaster;

/**
 * Created by JustMe on 2/11/2018.
 */
public class TablePanelX<T extends Actor> extends TablePanel<T> {
    public TablePanelX() {
    }

    public TablePanelX(float width, float height) {
        super();
        setFixedSize(true);
        setSize(width, height);
    }

    public TablePanelX(Actor actor) {
        this(actor.getWidth(), actor.getHeight());
        addActor(actor);
    }

    protected void initResolutionScaling() {
        float coef = (float) Math.pow(GdxMaster.getFontSizeMod(), 0.3f);
        setScale(coef, coef);
    }

    protected TablePanelX createInnerTable() {
        return new TablePanelX<>(getWidth(), getHeight());
    }

    @Override
    public void setSize(float width, float height) {
        setFixedSize(true);
        super.setSize(width, height);
    }

    public void  addBackgroundActor(Actor actor) {
        addActor(actor);
        setSize(actor.getWidth(), actor.getHeight());
    }

    @Override
    public void setUserObject(Object userObject) {
        if (userObject != null)
            if (getUserObjectClass() != null)
                if (!ClassMaster.isInstanceOf(userObject, getUserObjectClass()))
                    return;
        super.setUserObject(userObject);
    }

    protected Class<?> getUserObjectClass() {
        return null;
    }

    @Override
    public <T extends Actor> Cell<T> add(T actor) {
        return super.add(actor);
    }

    public void update() {
    }

    public void toggleFade() {
        if (isVisible()) {
            fadeOut();
        } else {
            fadeIn();
        }
    }
}
