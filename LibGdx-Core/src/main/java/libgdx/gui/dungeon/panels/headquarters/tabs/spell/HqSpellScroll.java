package libgdx.gui.dungeon.panels.headquarters.tabs.spell;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.UiMaster;
import libgdx.gui.dungeon.panels.ScrollPanel;
import libgdx.gui.dungeon.panels.headquarters.HqElement;

/**
 * Created by JustMe on 4/17/2018.
 */
public class HqSpellScroll extends HqElement {

    private final ScrollPanel<HqSpellContainer> scroll;
    private final int displayedRows;
    HqSpellContainer container;

    public HqSpellScroll(HqSpellContainer container, int displayedRows) {
        this.container = container;
//        addActor(
//         scroll = new ScrollPanel<>()
//        );
        scroll = new ScrollPanel<>();
       scroll.setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanelFilled()));
        setFixedSize(true);

        addActor(new ScrollPane(container));

        setSize(container.getWidth(), displayedRows* UiMaster.getHqSpellIconSize());
        scroll.setSize(container.getWidth(),
         displayedRows* UiMaster.getHqSpellIconSize());
//        scroll.addElement(container);
        this.displayedRows = displayedRows;
    }

    @Override
    protected void update(float delta) {
        scroll.pad(1, 10, 1, 10);
        scroll.fill();
        scroll.getInnerScrollContainer().getActor().setHeight(displayedRows* UiMaster.getHqSpellIconSize());

    }
}
