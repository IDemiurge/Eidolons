package main.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.entity.Entity;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;

public class InventoryValueContainer extends ValueContainer {
    private CELL_TYPE cellType;
    private InventoryClickHandler handler;
    private Entity entity;

    public InventoryValueContainer(TextureRegion texture, String name, String value) {
        super(texture, name, value);
    }

    public InventoryValueContainer(TextureRegion texture) {
        super(texture);
    }

    public InventoryValueContainer(TextureRegion texture, String value) {
        super(texture, value);
    }

    public InventoryValueContainer(String name, String value) {
        super(name, value);
    }

    public void setCellType(InventoryClickHandler.CELL_TYPE cellType) {
        this.cellType = cellType;
    }

    public void setHandler(InventoryClickHandler handler) {
        this.handler = handler;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, name, value);

        addListener(new ClickListener(-1) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final int tapCount = this.getTapCount();
                final boolean isRightClicked = event.getButton() == Input.Buttons.RIGHT;
                final boolean isAltPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);
                handler.cellClicked(cellType, tapCount, isRightClicked, isAltPressed, entity);
                event.stop();
            }
        });
    }
}
