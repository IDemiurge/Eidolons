package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;

import java.util.Arrays;

public class GraveyardView extends TablePanel {
    private static final int SIZE = 4;
    private static final int ROW_SIZE = 2;
    private Cell<UnitView>[] graves;

    private TablePanel<UnitView> graveTables;

    private Button graveyardButton;

    public GraveyardView() {
        graveyardButton = new Button(
                StyleHolder.getCustomButtonStyle("UI/components/small/skulls_32x32.png"));

        graveyardButton.setChecked(true);
        add(graveyardButton).left().bottom();
        row();
        graveTables = new TablePanel<>();
        graves = new Cell[SIZE];
        for (int i = 0; i < SIZE; i++) {
            if (i % ROW_SIZE == 0) row();
            graves[i] = graveTables.add().expand().fill();
        }
        add(graveTables).expand().fill();
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(
                new ValueContainer("\"Death smiles at us all", ""),
                new ValueContainer("all we can do is smile back.\"", "")));
        graveyardButton.addListener(tooltip.getController());
    }

    public void AddCorpse(UnitView unitView) {
        addAt(unitView, 0);
    }

    private void addAt(UnitView unitView, int pos) {
        if (pos >= graves.length) return;

        final UnitView current = graves[pos].getActor();
        graves[pos].setActor(unitView);
        if (current != null) {
            addAt(current, ++pos);
        }
    }
}
