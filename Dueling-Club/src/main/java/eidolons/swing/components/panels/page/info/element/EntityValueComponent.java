package eidolons.swing.components.panels.page.info.element;

import main.entity.Entity;

import java.awt.*;

public interface EntityValueComponent {

    Component getComponent();

    Entity getEntity();

    void setEntity(Entity entity);

    void refresh();
}
