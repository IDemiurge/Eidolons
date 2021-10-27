package libgdx.gui.editor.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.headquarters.ValueTable;
import eidolons.content.consts.Images;
import main.system.threading.WaitMaster;

import java.util.Arrays;
import java.util.Collection;

public abstract class ChooserDialog<T, T1 extends Actor> extends ValueTable<T, T1> {

    protected T selected;
    protected TablePanel scrolledTable;
    protected ScrollPane scroll;

    public ChooserDialog(int wrap, int size) {
        super(wrap, size);
        setVisible(false);
        setBackground(NinePatchFactory.getLightPanelFilledDrawable());
    }

    @Override
    protected T1 createElement(T datum) {
        T1 e = createElement_(datum);
        e.addListener(createItemSelectListener(e, datum));
        return e;
    }

    @Override
    protected TablePanel getContentTable() {
        if (isScrolled()) {
            return getScrolledTable();
        }
        return super.getContentTable();
    }

    protected boolean isScrolled() {
        return false;
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        if (scroll != null) {
            add(scroll);
        }
    }

    @Override
    public void init() {
        super.init();

        if (isInstaOk()) {
            return;
        }
        row();

        addNormalSize(new ImageContainer(Images.SEPARATOR_NARROW)).center().colspan(2).padBottom(10);
        row();
        addNormalSize(new SymbolButton(ButtonStyled.STD_BUTTON.OK, () -> ok())).left();
        addNormalSize(new SymbolButton(ButtonStyled.STD_BUTTON.CANCEL, () -> cancel())).right();
    }

    protected abstract T1 createElement_(T datum);

    public void cancel() {
        chosen(null);
        close();
    }

    public void ok() {
        chosen(selected);
        close();
    }

    protected void chosen(T selected) {
        WaitMaster.receiveInput(getSelectionOperation(), selected);
    }

    protected WaitMaster.WAIT_OPERATIONS getSelectionOperation() {
        return WaitMaster.WAIT_OPERATIONS.DIALOG_SELECTION;
    }


    public T choose(T[] from) {
        return choose(Arrays.asList(from));
    }

    public T choose(Collection<T> from) {
        show();
        setUserObject(from);
        T res = (T) WaitMaster.waitForInputAnew(getSelectionOperation());
//        close(); via ok()
        return res;
    }

    protected EventListener createItemSelectListener(T1 actor, T item) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                selected = item;
                if (isInstaOk()) {
                    ActionMasterGdx.addScaleAction(actor, 0, 1f);
                    ok();
                }
            }
        };
    }

    protected boolean isInstaOk() {
        return false;
    }

    protected void show() {
        if (getColor().a != 1) {
            fadeIn();
        } else {
            setVisible(true);
        }

    }

    protected void close() {
//        if (getColor().a == 1)
            fadeOut();
    }

    public TablePanel getScrolledTable() {
        if (scrolledTable == null) {
            scrolledTable = new TablePanelX();
            scroll = new ScrollPane(scrolledTable);
            add(scroll);
        }
        return scrolledTable;
    }

}
