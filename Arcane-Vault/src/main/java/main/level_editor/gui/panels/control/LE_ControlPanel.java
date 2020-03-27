package main.level_editor.gui.panels.control;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.level_editor.LevelEditor;
import main.level_editor.backend.LE_Manager;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public  abstract class LE_ControlPanel<T>  extends TablePanelX {

    private boolean initialized;

    public LE_ControlPanel(){
        super(300, 800);
    }

    public void init(Class<T> clazz, T handler) {
        int j=0;
        List<Method> sorted = Arrays.asList(clazz.getMethods());
        sorted.sort(getSorter());
        for (Method method : sorted) {
//            if (method.getAnnotation(getIgnoreAnnotation()) != null) {
//                continue;
//            }
            Cell cell = addNormalSize( createButton(method, handler)).top().space(getSpace());
            j++;
            if (j >= getWrap()) {
                row();
                j = 0;
            }
        }
        initialized = true;
    }

    protected LE_Manager getManager() {
        return LevelEditor.getCurrent().getManager();
    }

    private Comparator<? super Method> getSorter() {
        return (Comparator<Method>) (o1, o2
        ) -> SortMaster.compareAlphabetically(o1.getName(), o2.getName());
    }

    @Override
    public void act(float delta) {
        if (!initialized){
            init(getClazz(), getHandler());
        }
        super.act(delta);
    }

    protected abstract T getHandler();

    protected abstract Class<T> getClazz();

    protected    int getWrap(){return 5;}

    protected abstract float getSpace() ;

    private Class<? extends Annotation> getIgnoreAnnotation() {
        return IgnoredCtrlMethod.class;
    }

    protected SmartButton createButton(Method method, T handler) {
        String name = StringMaster.getWellFormattedString(method.getName(), true);
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
        return ButtonStyled.STD_BUTTON.TAB_HIGHLIGHT_COLUMN;
    }

    protected TextButton.TextButtonStyle getButtonTextStyle() {
        return StyleHolder.getHqTextButtonStyle(14);
    }
}
