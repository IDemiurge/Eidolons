package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.StringMaster;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public  abstract class LE_ControlPanel<T>  extends TablePanelX {

    public LE_ControlPanel(Class<T> clazz, T handler){
        super(300, 800);
        int j=0;
        for (Method method : clazz.getMethods()) {
//            if (method.getAnnotation(getIgnoreAnnotation()) != null) {
//                continue;
//            }
            Cell cell = addElement( createButton(method, handler)).top().space(getSpace());
            j++;
            if (j >= getWrap()) {
                row();
                j = 0;
            }
        }
    }

    protected  abstract int getWrap();

    protected abstract float getSpace() ;

    private Class<? extends Annotation> getIgnoreAnnotation() {
        return IgnoredCtrlMethod.class;
    }

    protected SmartButton createButton(Method method, T handler) {
        String name = StringMaster.getWellFormattedString(method.getName());
        return new SmartButton(name, getButtonTextStyle(), () -> {
            Eidolons.onNonGdxThread(() -> {
                try {
                    method.invoke(handler);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }, getButtonStyle());
    }

    protected ButtonStyled.STD_BUTTON getButtonStyle() {
        return ButtonStyled.STD_BUTTON.MENU;
    }

    protected TextButton.TextButtonStyle getButtonTextStyle() {
        return StyleHolder.getDefaultTextButtonStyle();
    }
}
