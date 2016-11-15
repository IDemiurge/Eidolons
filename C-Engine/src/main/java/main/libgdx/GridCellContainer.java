package main.libgdx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class GridCellContainer extends GridCell {
    public enum GridCellMode {SINGLE, GROUP, MULTI}

    private final int maxW = 96;
    private final int maxH = 99;
    private final int offsetX = 18;
    private final int offsetY = 7;

    private List<Image> images;

    public GridCellContainer(Texture backTexture, String imagePath, int gridX, int gridY) {
        super(backTexture, imagePath, gridX, gridY);
    }

    @Override
    public GridCellContainer init() {
        super.init();
        images = new ArrayList<>();
        return this;
    }

    public void setObjects(List<Texture> objects) {
        if (objects.size() == 1) {
            initSingleView(objects);
        } else if (objects.size() <= 3) {
            initGroupView(objects);
        } else if (objects.size() > 3) {
            initMultiView(objects);
        }
    }

    private List<Image> createImages(List<Texture> objects) {
        List<Image> images = new ArrayList<>(objects.size());
        for (Texture object : objects) {
            images.add(new Image(object));
        }
        return images;
    }

    private void initSingleView(List<Texture> objects) {
        Texture t = scaleTo(maxW, maxH, objects.get(0));
        Image i = new Image(t);
        i.setX(offsetX);
        i.setY(offsetY);
        images.add(i);
        addActor(i);
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

    private void initGroupView(List<Texture> objects) {
        final int perImageOffsetX = maxW / 2 / objects.size();
        int perImageOffsetY = maxH / 2 / objects.size();
        for (int i = 0; i < objects.size(); i++) {
            Texture t = scaleTo(maxW, maxH, objects.get(i));
            Image im = new Image(t);
            im.setX(offsetX + perImageOffsetX * i);
            addActor(im);
            images.add(im);
        }
    }

    private void initMultiView(List<Texture> objects) {

    }
}
