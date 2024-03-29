package libgdx.bf.grid.cell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import libgdx.StyleHolder;
import libgdx.bf.datasource.GraveyardDataSource;
import libgdx.bf.mouse.BattleClickListener;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.dungeon.panels.TablePanel;
import libgdx.gui.dungeon.tooltips.DynamicTooltip;
import libgdx.assets.texture.TextureCache;

import java.util.Arrays;
import java.util.List;

public class GraveyardView extends TablePanel {
    private static final int SIZE = 4;
    private static final int ROW_SIZE = 2;
    private final Cell<UnitView>[] graves;

    private final TablePanel<UnitView> graveTables;

    final Button graveyardButton;
    private int graveCount = 0;

    //Gdx Review
    public GraveyardView() {
        graveyardButton = new Button(new Image(
                TextureCache.getRegionUV(
                        "ui/content/dc_icons/skulls_32x32.png")),
                //                StyleHolder.getCustomButtonStyle("ui/components/small/skulls_32x32.png")
                StyleHolder.getDefaultTextButtonStyle()
        );

        graveyardButton.setChecked(true);
        add(graveyardButton).left().bottom();
        row();
        graveTables = new TablePanel<>();
        graveTables.setBackground(new NinePatchDrawable(NinePatchFactory.get3pxBorder()));
        graves = new Cell[SIZE];
        for (int i = 0; i < SIZE; i++) {
            if (i % ROW_SIZE == 0)
                graveTables.row();
            graves[i] = graveTables.add().expand().fill();
        }
        add(graveTables).expand().fill();
        DynamicTooltip tooltip = new DynamicTooltip(this::getTooltipText);
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

    private String getTooltipText() {
        return graveCount + " corpses\n" +
                (isAlt() ? "Alt-" : "") +
                "Click to view";
    }

    private boolean isAlt() {
        return getParent().getUnitViewsVisible().size() > 0;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Actor hit = super.hit(x, y, touchable);
        if (hit != null)
            if (isAlt()) {
                if (!Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                    return null;
                }
            }
        return hit;
    }

    @Override
    public GridCellContainer getParent() {
        return (GridCellContainer) super.getParent();
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
        setUpdateRequired(true);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);

        Arrays.stream(graves).forEach(el -> el.setActor(null));

        final List<BaseView> userObject = ((GraveyardDataSource) getUserObject()).getGraveyard();

        userObject.forEach(this::addCorpse);

        setVisible(userObject.size() > 0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected boolean isVisibleEffectively() {
        return true;
    }
}
