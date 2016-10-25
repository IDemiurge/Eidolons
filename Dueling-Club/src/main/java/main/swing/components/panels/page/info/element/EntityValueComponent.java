package main.swing.components.panels.page.info.element;

import main.entity.Entity;

import java.awt.*;

public interface EntityValueComponent {

    Component getComponent();

    public Entity getEntity();

    public void setEntity(Entity entity);

    void refresh();
}
