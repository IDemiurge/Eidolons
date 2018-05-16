package eidolons.libgdx.gui.panels.headquarters.party;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqPartyMembers extends HqPartyElement {
    private final HqPanel panel;
    boolean vertical;

    public HqPartyMembers(HqPanel panel, boolean vertical) {
        this.panel = panel;
        this.vertical = vertical;
    }

    @Override
    protected void update(float delta) {
        clear();
        if (dataSource.size() <= 1)
            return;
//        Group group = (vertical) ? new VerticalGroup() : new HorizontalGroup();
        for (HqHeroDataSource hero : dataSource) {
            HqHeroPreview preview = new HqHeroPreview(hero);
             add(preview);
             if (vertical)
                 row();
            preview.addListener(getListener(preview, hero));
            preview.setHighlight(panel.getSelectedHero().equals(hero));
        }

    }

    private EventListener getListener(HqHeroPreview preview, HqHeroDataSource hero) {
        return new SmartClickListener(preview) {
            @Override
            protected void onTouchDown(InputEvent event, float x, float y) {
                panel.memberSelected(hero);
                super.onTouchDown(event, x, y);
            }
        };
    }
}
