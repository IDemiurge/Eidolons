package libgdx.gui.panels.headquarters.hero;

import libgdx.gui.LabelX;
import libgdx.gui.NinePatchFactory;
import libgdx.gui.panels.headquarters.HqElement;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroHeader extends HqElement{

    private final LabelX name;
//    private final LabelX race;
//    private final LabelX level;

    public HqHeroHeader() {
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        setSize(330, 85);
        add(name = new LabelX("", 21)).center().row();
//        add(race = new LabelX("", 18)).center().row();
//        add(level = new LabelX("", 18)).center();
    }

    @Override
    protected void update(float delta) {
        name.setText(dataSource.getName());
//        race.setText(dataSource.getProperty(G_PROPS.RACE));
//        level.setText("Level " +dataSource.getLevel());
    }
}
