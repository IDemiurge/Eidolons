package main.level_editor.gui.dialog;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import eidolons.game.module.dungeoncrawl.generator.model.RoomModel;
import eidolons.game.module.dungeoncrawl.generator.tilemap.TileMapper;
import eidolons.libgdx.gui.NinePatchFactory;
import main.level_editor.LevelEditor;

import java.util.Collection;

public class BlockTemplateChooser extends ChooserDialog<RoomModel, TileMapView> {

    private RoomModel[] templates;
    private boolean palette;

    public BlockTemplateChooser(boolean palette) {
        this();
        this.palette = palette;

    }
    public BlockTemplateChooser() {
        super(getWrap(), 12);
    }

    private static int getWrap() {
        return 2;
    }

    @Override
    protected EventListener createItemSelectListener(TileMapView actor, RoomModel item) {
        if (palette)
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                for (TileMapView tileMapView : actors) {
                    tileMapView.setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
                }
                actor.setBackground(NinePatchFactory.getHighlightDrawable());
                LevelEditor.getManager().getModelManager().getModel().getPaletteSelection().setTemplate(item);
            }
        };
        return super.createItemSelectListener(actor, item);
    }

    @Override
    protected boolean isScrolled() {
        return false;
    }

    @Override
    protected Vector2 getElementSize(RoomModel sub) {
        Vector2 v = super.getElementSize(sub);
        v.y = v.y*sub.getHeight()/9;
        return v;
    }

    @Override
    protected TileMapView createElement_(RoomModel datum) {
        return new TileMapView(size, TileMapper.getLinesFromCells(datum.getCells()));
    }

    @Override
    protected boolean isSquare() {
        return !palette;
    }

    @Override
    protected boolean isInstaOk() {
        return true;
    }

    @Override
    protected Vector2 getElementSize() {
        return new Vector2(75*getSizeCoef(size), 180*getSizeCoef(size));
    }

    public static float getSizeCoef(int size) {
        return 1 + 2/(float) Math.sqrt(size);
    }

    @Override
    public void setUserObject(Object userObject) {
        Collection<RoomModel> c = (Collection<RoomModel>) userObject;
        templates = c.toArray(new RoomModel[0]);
        initSize(getWrap(), templates.length);
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
