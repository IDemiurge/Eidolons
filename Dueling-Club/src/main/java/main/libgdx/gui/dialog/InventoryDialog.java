package main.libgdx.gui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import main.game.core.Eidolons;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.panels.dc.inventory.InventoryClickHandler;
import main.libgdx.gui.panels.dc.inventory.InventoryPanel;
import main.libgdx.gui.panels.dc.inventory.datasource.InventoryDataSource;

/**
 * Created by JustMe on 1/6/2017.
 */
public class InventoryDialog extends TablePanel  {

    InventoryPanel inventoryPanel;
    InventoryClickHandler handler;
    //TODO extract all below this into *DIALOG*, the rest is same for HC inventory
    private Cell<Actor> actionPointsText;
    private Cell<Actor> doneButton;
    private Cell<Actor> cancelButton;
    private Cell<Actor> undoButton;

    public InventoryDialog(InventoryPanel inventoryPanel, InventoryClickHandler handler) {
        this.inventoryPanel = inventoryPanel;
        this.handler = handler;

        addElement(inventoryPanel).pad(0, 20, 20, 20);
        final TablePanel<Actor> lower = new TablePanel<>();
        addElement(lower).pad(0, 20, 20, 20);

        actionPointsText = lower.addElement(null).left();
        undoButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 0, 20, 0).size(50, 50);
        cancelButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 10, 20, 0).size(50, 50);
        doneButton = lower.addElement(null).fill(false).expand(0, 0)
         .right().pad(20, 10, 20, 10).size(50, 50);
        initButtons();
        initButtonListeners(handler);

    }

    public void initButtonListeners(InventoryClickHandler handler) {
//        undoButton.getActor().removeListener()
        if (undoButton.getActor() instanceof Button) {
//            ((Button) undoButton.getActor()).
            undoButton.getActor().addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    handler.undoClicked();
                }
            });
        }
        doneButton.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                handler.doneClicked();
            }
        });
        cancelButton.getActor().addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                handler.cancelClicked();
            }
        });
    }

    @Override
    public void afterUpdateAct(float delta) {
       if (!updateRequired)
           return ;
        actionPointsText.setActor(new ValueContainer("Actions available:", getOperationsString()) {
            @Override
            public void afterUpdateAct(float delta) {
                valueContainer.setActor(new Label(getOperationsString(),
                 StyleHolder.getDefaultLabelStyle()));
                super.afterUpdateAct(delta);
            }
        });
       initButtons();
    }

    private void initButtons() {
        doneButton.setActor(new Button(StyleHolder.getCustomButtonStyle
                             ("UI/components/small/ok.png")) {
                                public boolean isDisabled() {
                                    Object obj = getUserObject();
                                    if (obj instanceof InventoryDataSource) {
                                        return !((InventoryDataSource) obj).
                                         getHandler().isDoneEnabled();
                                    }
                                    return super.isDisabled();

                                }
                            }
        );
        cancelButton.setActor(new Button(StyleHolder.getCustomButtonStyle
         ("UI/components/small/no.png")){
            public boolean isDisabled() {
                Object obj = getUserObject();
                if (obj instanceof InventoryDataSource) {
                    return !((InventoryDataSource) obj).
                     getHandler().isCancelEnabled();
                }
                return super.isDisabled();

            }
        });

        undoButton.setActor(new Button(StyleHolder.getCustomButtonStyle
         ("UI/components/small/back2.png")){
            public boolean isDisabled() {
                Object obj = getUserObject();
                if (obj instanceof InventoryDataSource) {
                    return !((InventoryDataSource) obj).
                     getHandler().isUndoEnabled();
                }
                return super.isDisabled();

            }
        });
    }

    public   String getOperationsString() {
        return Eidolons.game.getInventoryManager().getOperationsLeft() + "/" +
         Eidolons.game.getInventoryManager().getOperationsPool();
    }


}
