package eidolons.libgdx.gui.panels.headquarters.hero;

import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import main.content.values.properties.G_PROPS;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroHeader extends HqElement{

    private final LabelX name;
    private final LabelX race;
    private final LabelX level;

    public HqHeroHeader() {
        add(name = new LabelX("", 20)).center().row();
        add(race = new LabelX("", 18)).center().row();
        add(level = new LabelX("", 18)).center();
    }

    @Override
    protected void update(float delta) {
        name.setText(dataSource.getName());
        race.setText(dataSource.getProperty(G_PROPS.RACE));
        level.setText("Level " +dataSource.getLevel());
    }
}
