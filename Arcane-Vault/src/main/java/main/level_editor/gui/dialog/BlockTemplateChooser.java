package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;

import java.util.Collection;

public class BlockTemplateChooser extends ChooserDialog<RoomModel, TileMapView> {

    private RoomModel[] templates;

    public BlockTemplateChooser() {
        super(4, 12);
    }

    @Override
    protected boolean isScrolled() {
        return true;
    }

    @Override
    protected TileMapView createElement_(RoomModel datum) {
        return new TileMapView(TileMapper.getLinesFromCells(datum.getCells()));
    }

    @Override
    protected boolean isInstaOk() {
        return true;
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(110, 160);
    }

    @Override
    public void setUserObject(Object userObject) {
        Collection<RoomModel> c = (Collection<RoomModel>) userObject;
        templates = c.toArray(new RoomModel[0]);
        initSize(12, templates.length);
        super.setUserObject(userObject);
    }

    @Override
    protected TileMapView[] initActorArray() {
        return new TileMapView[templates.length];
    }

    @Override
    protected RoomModel[] initDataArray() {
        return templates;
    }
}
