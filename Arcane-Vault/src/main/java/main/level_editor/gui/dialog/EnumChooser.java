package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.SmartButton;

import java.util.Collection;

public class EnumChooser extends ChooserDialog<Object, SmartButton> {
    private Object[] consts;

    public EnumChooser() {
        super(4, 120);
    }

    @Override
    protected SmartButton createElement_(Object datum) {
        return new SmartButton(datum.toString(), StyleHolder.getHqTextButtonStyle(14));
    }

    @Override
    protected boolean isInstaOk() {
        return true;
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
        return new Vector2(120, 40);
    }
    public <T> T choose(T[] from, Class<T> c) {
        return (T) super.choose(from);
    }

    @Override
    protected SmartButton[] initActorArray() {
        return new SmartButton[consts.length];
    }

    @Override
    protected Object[] initDataArray() {
        return consts;
    }
}
