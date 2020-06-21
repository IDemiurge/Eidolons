package main.gui.components.menu;

import main.handlers.AvHandler;
import main.system.auxiliary.StringMaster;
import sun.swing.UIAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMenu<T>  {
    private final AvHandler handler;
    /*
        assign key to each func?
         */
    JMenu menu;

    public HandlerMenu(AvHandler handler, Class<T> clazz) {
        this.handler = handler;
        menu = new JMenu(clazz.getSimpleName());

        Method[] sorted = clazz.getMethods();
        // sorted.sort(getSorter());
        for (Method method : sorted) {
            if (method.getAnnotation(getIgnoreAnnotation()) != null) {
                continue;
            }
            add(method);
        }
    }

    public JMenu getMenu() {
        return menu;
    }

    private Class getIgnoreAnnotation() {
        return null;
    }

    private void add(Method method) {
        String s = StringMaster.format(method.getName());
        Action action= new UIAction(s) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    method.invoke(handler);
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                } catch (InvocationTargetException invocationTargetException) {
                    invocationTargetException.printStackTrace();
                }
            }
        };
        menu.add(new JMenuItem(action));
    }
}
