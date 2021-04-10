package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
import libgdx.StyleHolder;
import libgdx.gui.generic.btn.SmartTextButton;
import main.system.threading.WaitMaster;

import java.util.Collection;

public class EnumChooser extends ChooserDialog<Object, SmartTextButton> {

    public static final WaitMaster.WAIT_OPERATIONS OPERATION = WaitMaster.WAIT_OPERATIONS.
            DIALOG_SELECTION_ENUM;

    @Override
    protected WaitMaster.WAIT_OPERATIONS getSelectionOperation() {
        return OPERATION;
    }
    private Object[] consts;

    public EnumChooser() {
        super(4, 120);
    }

    @Override
    protected SmartTextButton createElement_(Object datum) {
        return new SmartTextButton(datum.toString(), StyleHolder.getHqTextButtonStyle(14));
    }

    @Override
    protected void initSize(int wrap, int size) {
        super.initSize(wrap, size);
        setWidth(Math.max(getWidth(), 700));
        setHeight(Math.max(getHeight(), 400));
    }

    @Override
    protected boolean isInstaOk() {
        return true;
    }

    @Override
    protected boolean isSquare() {
        return size > 8;
    }

    @Override
    public void setUserObject(Object userObject) {
        Collection c= (Collection) userObject;
        consts = c.toArray( );
        initSize(4, consts.length);
        super.setUserObject(userObject);
    }
    @Override
    protected Vector2 getElementSize() {
        return new Vector2(160, 70);
    }

    public <T extends Enum> T chooseEnum(Class<T> c) {
        return (T) choose(c.getEnumConstants());
    }

    @Override
    protected SmartTextButton[] initActorArray() {
        return new SmartTextButton[consts.length];
    }

    @Override
    protected Object[] initDataArray() {
        return consts;
    }
}
