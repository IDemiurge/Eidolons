package eidolons.libgdx.gui.panels.dc.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.dc.inventory.InventoryClickHandler.CELL_TYPE;
import eidolons.libgdx.gui.panels.dc.inventory.container.ContainerPanel;
import main.entity.Entity;

public class InventoryValueContainer extends ValueContainer {
    private CELL_TYPE cellType;
    private InventoryClickHandler handler;
    private Entity entity;
    private ClickListener listener;


    public InventoryValueContainer(TextureRegion texture, String name) {
        super(texture, name, "");
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

    public ClickListener getListener() {
        return listener;
    }

    @Override
    protected void init(TextureRegion texture, String name, String value) {
        super.init(texture, "", "");
        addListener(
         listener =
          new ClickListener(-1) {
              @Override
              public void touchDragged(InputEvent event, float x, float y, int pointer) {
                  super.touchDragged(event, x, y, pointer);
                  if (handler.getDragged() == null)
                      handler.singleClick(cellType, entity);
              }

              @Override
              public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                  if (handler.getDragged() != null) {
                      Vector2 v = localToStageCoordinates(new Vector2(x, y));

                      Group panel = GdxMaster.getFirstParentOfClass(
                       InventoryValueContainer.this,
                       InventoryPanel.class);
                      if (panel == null) {
                          panel = GdxMaster.getFirstParentOfClass(
                           InventoryValueContainer.this,
                           ContainerPanel.class);
                      }
                      if (panel == null) {
                          super.touchUp(event, x, y, pointer, button);
                          return;
                      }
                      v = panel.stageToLocalCoordinates(v);
                      Actor cell = panel.hit(v.x, v.y, true);
                      if (cell != null)
                          if (!(cell instanceof InventoryValueContainer))
                              cell = GdxMaster.getFirstParentOfClass(cell, InventoryValueContainer.class);
                      if (cell != null) {
                          ((InventoryValueContainer) cell).getListener().clicked(event, x, y);
                          return;
                      }
//                    Gdx.input.getInputProcessor().touchDown((int) v.x, (int) v.y, pointer, button);
//                    Gdx.input.getInputProcessor().touchUp((int) v.x, (int) v.y, pointer, button);
                      super.touchUp(event, x, y, pointer, button);
                      handler.setDragged(null);
                  } else
                      super.touchUp(event, x, y, pointer, button);
              }

              @Override
              public void clicked(InputEvent event, float x, float y) {
                  final int tapCount = this.getTapCount();
                  final boolean isRightClicked = event.getButton() == Input.Buttons.RIGHT;
                  final boolean isAltPressed = Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT);

//                new Thread(() -> handler.cellClicked(cellType, tapCount, isRightClicked,
//                 isAltPressed, entity), "cell clicked thread").start();

                  Eidolons.onNonGdxThread(() ->
                   handler.cellClicked(cellType, tapCount, isRightClicked,
                    isAltPressed, entity));

                  event.stop();
              }
          });
    }


}
