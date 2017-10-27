package main.libgdx.bf;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import main.libgdx.StyleHolder;
import main.libgdx.bf.datasource.GraveyardDataSource;
import main.libgdx.bf.mouse.BattleClickListener;
import main.libgdx.gui.NinePathFactory;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.gui.panels.dc.ValueContainer;
import main.libgdx.gui.tooltips.ValueTooltip;
import main.libgdx.texture.TextureCache;

import java.util.Arrays;
import java.util.List;

public class GraveyardView extends TablePanel {
    private static final int SIZE = 4;
    private static final int ROW_SIZE = 2;
    private Cell<UnitView>[] graves;

    private TablePanel<UnitView> graveTables;

    private Button graveyardButton;
    private int graveCount=0;

    public GraveyardView() {
        graveyardButton = new Button(new Image(
         TextureCache.getOrCreate(
         "UI/components/small/skulls_32x32.png")),
//                StyleHolder.getCustomButtonStyle("UI/components/small/skulls_32x32.png")
         StyleHolder.getDefaultTextButtonStyle()
        );

        graveyardButton.setChecked(true);
        add(graveyardButton).left().bottom();
        row();
        graveTables = new TablePanel<>();
        graveTables.setBackground(new NinePatchDrawable(NinePathFactory.get3pxBorder()));
        graves = new Cell[SIZE];
        for (int i = 0; i < SIZE; i++) {
            if (i % ROW_SIZE == 0)
                graveTables.row();
            graves[i] = graveTables.add().expand().fill();
        }
        add(graveTables).expand().fill();
        ValueTooltip tooltip = new ValueTooltip();
        tooltip.setUserObject(Arrays.asList(
                new ValueContainer("\"Death smiles at us all,", ""),
                new ValueContainer("all a man can do is smile back.\"", "")));
        graveyardButton.addListener(tooltip.getController());

        graveyardButton.addListener(new BattleClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                graveTables.setVisible(graveyardButton.isChecked());
            }
        });

        graveTables.setVisible(false);
        setVisible(false);
        graveyardButton.setChecked(false);
    }

    public void addCorpse(BaseView unitView) {
        addAt(unitView, 0);
    }

    private void addAt(BaseView unitView, int pos) {
        if (pos >= graves.length) return;

        final BaseView current = graves[pos].getActor();
        graves[pos].setActor(unitView);
        if (current != null) {
            addAt(current, ++pos);
        }
        graveCount++;
    }

    public int getGraveCount() {
        return graveCount;
    }

    public void updateGraveyard() {
        updateRequired = true;
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        Arrays.stream(graves).forEach(el -> el.setActor(null));

        final List<BaseView> userObject = ((GraveyardDataSource) getUserObject()).getGraveyard();

        userObject.forEach(this::addCorpse);

        setVisible(userObject.size() > 0);
    }
}
