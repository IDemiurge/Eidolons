package eidolons.libgdx.gui.panels.headquarters.party;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqPartyMembers extends HqPartyElement {
    private final HqPanel panel;
    private final Image bg;
    boolean vertical;

    public HqPartyMembers(HqPanel panel, boolean vertical) {
        this.panel = panel;
        this.vertical = vertical;
        bg = new Image(TextureCache.getOrCreate(Images.PARTY_BACKGROUND_COLS));
    }

    @Override
    protected void update(float delta) {
        clear();
        if (!vertical)
            addActor(bg);
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
