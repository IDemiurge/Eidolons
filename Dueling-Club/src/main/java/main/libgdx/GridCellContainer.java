package main.libgdx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridCellContainer extends GridCell {
    public enum GridCellMode {SINGLE, GROUP, MULTI}

    private final int maxW = 96;
    private final int maxH = 99;
    private final int offsetX = 18;
    private final int offsetY = 7;
    private Map<UnitView, Runnable> images;
    private GridCellMode cellMode;

    public GridCellContainer(Texture backTexture, String imagePath, int gridX, int gridY) {
        super(backTexture, imagePath, gridX, gridY);
    }

    @Override
    public GridCellContainer init() {
        super.init();
        images = new HashMap<>();

        return this;
    }

    public void setObjects(List<UnitViewOptions> objects) {
        if (objects.size() == 1) {
            cellMode = GridCellMode.SINGLE;
            initSingleView(objects);
        } else if (objects.size() <= 3) {
            cellMode = GridCellMode.GROUP;
            initGroupView(objects);
        } else if (objects.size() > 3) {
            cellMode = GridCellMode.MULTI;
            initMultiView(objects);
        }
    }

    private void initSingleView(List<UnitViewOptions> objects) {
        //Texture t = scaleTo(maxW, maxH, objects.get(0).getRight());
        UnitView uv = new UnitView(objects.get(0));
        uv.setX(offsetX);
        uv.setY(offsetY);
        uv.setWidth(maxW);
        uv.setHeight(maxH);
        images.put(uv, objects.get(0).getRunnable());
        addActor(uv);
    }

    private Texture scaleTo(int w, int h, Texture t) {
        TextureData data = t.getTextureData();
        data.prepare();
        Pixmap pixmapOr = data.consumePixmap();
        Pixmap pixmapTar = new Pixmap(w, h, pixmapOr.getFormat());
        pixmapTar.drawPixmap(pixmapOr, 0, 0, pixmapOr.getWidth(), pixmapOr.getHeight(), 0, 0, pixmapTar.getWidth(), pixmapTar.getHeight());
        t = new Texture(pixmapTar);
        pixmapOr.dispose();
        pixmapTar.dispose();
        return t;
    }

    private void initGroupView(List<UnitViewOptions> objects) {
        final int perImageOffsetX = maxW / 2 / objects.size();
        int perImageOffsetY = maxH / 2 / objects.size();
        for (int i = 0; i < objects.size(); i++) {
            //Texture t = scaleTo(maxW, maxH, objects.get(i).getRight());
            UnitView im = new UnitView(objects.get(i));
            im.setScale(3f / 4f);
            im.setX(offsetX + perImageOffsetX * i);
            addActor(im);
            images.put(im, objects.get(i).getRunnable());
        }
    }

    private void initMultiView(List<UnitViewOptions> objects) {

    }

    private void recalcImagesPos() {
        int i = 0;
        int perImageOffsetY = maxH / 2 / images.size();
        final int perImageOffsetX = maxW / 2 / images.size();
        for (Actor actor : getChildren()) {
            if (actor instanceof UnitView) {
                actor.setX(offsetX + perImageOffsetX * i++);
            }
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        Vector2 v = new Vector2(x, y);
        v = getParent().parentToLocalCoordinates(v);
        Actor a = super.hitChilds(v.x, v.y, touchable);
        if (a != null && cellMode != GridCellMode.SINGLE && a instanceof UnitView) {
            this.removeActor(a);
            this.addActor(a);
            recalcImagesPos();
        }

        return a != null ? this : null;
    }
}
