package libgdx.gui.dungeon.panels.headquarters.party;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import libgdx.gui.dungeon.panels.headquarters.HqPanel;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;
import libgdx.gui.dungeon.tooltips.SmartClickListener;
import eidolons.content.consts.Images;
import libgdx.assets.texture.TextureCache;

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
        bg = new Image(TextureCache.getOrCreateR(Images.PARTY_BACKGROUND_COLS));
        setFixedMinSize(true);
    }

    @Override
    protected void update(float delta) {
        clear();

        if (!vertical)
        {
            addActor(bg);
            setSize(bg.getImageWidth(), bg.getImageHeight());
        }
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
