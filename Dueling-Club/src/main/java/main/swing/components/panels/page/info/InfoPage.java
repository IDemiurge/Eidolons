package main.swing.components.panels.page.info;

import main.entity.Entity;
import main.swing.generic.components.G_Panel;

public class InfoPage extends G_Panel {

    protected Entity entity;

    public InfoPage(VISUALS V) {
        super(V);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
