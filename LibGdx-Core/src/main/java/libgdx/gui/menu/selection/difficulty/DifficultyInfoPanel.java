package libgdx.gui.menu.selection.difficulty;

import com.badlogic.gdx.scenes.scene2d.Actor;
import libgdx.GDX;
import libgdx.gui.menu.selection.ItemInfoPanel;
import libgdx.gui.menu.selection.ItemListPanel.SelectableItemData;
import libgdx.gui.panels.TablePanel;
import libgdx.TiledNinePatchGenerator;

/**
 * Created by JustMe on 2/8/2018.
 */
public class DifficultyInfoPanel extends ItemInfoPanel {
    public DifficultyInfoPanel(SelectableItemData difficulty) {
        super(difficulty);
    }

    @Override
    protected void afterLayout() {
        super.afterLayout();
        if (startButton != null) {
                startButton.setPosition(GDX.centerWidth(startButton),
                 TiledNinePatchGenerator.NINE_PATCH_PADDING.SAURON.bottom);
        }
    }

    @Override
    protected void initBg() {
        super.initBg();
    }

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    protected void initHeader(TablePanel<Actor> header) {
        super.initHeader(header);
    }

    protected String getDefaultTitle() {
        if (isRandomDefault())
            return "Random";
        return "No item selected";
    }
}
