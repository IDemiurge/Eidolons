package main.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.libgdx.texture.TextureManager;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation {
    public float x;
    public  float y;
    private SpriteBatch spriteBatch;
    private ArrayList<Texture> regions;
    private float maxFrameTime;
    private float currentFrameTime;
    private float stateTime;
    private int frameCount;
    private int frame;
    private float offsetY;
    private float offsetX;
//    final float static defaultFrameDuration=0.025f;
//    public SpriteAnimation(ArrayList regions, int frameCount, float cycleTime) {
//        this.regions = regions;
//        this.frameCount = frameCount;
//        maxFrameTime = cycleTime / frameCount;
//        frame = 0;
//    }

    public SpriteAnimation(String path, int FRAME_ROWS, int FRAME_COLS) {
        super(0.025f, getKeyFrames(path, FRAME_ROWS, FRAME_COLS));
        stateTime = 0;
        spriteBatch = new SpriteBatch();
    }

    public SpriteAnimation(String path) {
        this(path, getRows(path), getColumns(path));
    }


    static Array<TextureRegion> getKeyFrames(String path, int FRAME_COLS, int FRAME_ROWS) {
        Texture sheet = TextureManager.getOrCreate(path);
        TextureRegion[][] tmp = TextureRegion.split(sheet,
         sheet.getWidth() / FRAME_COLS,
         sheet.getHeight() / FRAME_ROWS);

        TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
        int index = 0;
        for (int i = 0; i < FRAME_ROWS; i++) {
            for (int j = 0; j < FRAME_COLS; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        return new Array<>(frames);
    }

    public boolean draw(Batch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = getKeyFrame(stateTime, true);
        if (currentFrame == null) {
            dispose();
            return false;
        }
//        batch.begin();
        batch.draw(currentFrame, x+offsetX, y+offsetY);
//        batch.end();

        return true;
    }

    public void dispose() { // SpriteBatches and Textures must always be disposed
//        spriteBatch.dispose();
//        sheet.dispose();
    }

    public void update(float v) {
        currentFrameTime += v;
        if (currentFrameTime > maxFrameTime) {
            frame++;
            currentFrameTime = 0;
        }
        if (frame >= frameCount) {
            frame = 0;
        }
    }

    public Texture getTexture() {
        return regions.get(frame);
    }

    private static int getColumns(String path) {
        return getDimension(path, false);
    }

    private static int getRows(String path) {
        return getDimension(path, true);

    }

    private static int getDimension(String path, boolean xOrY) {
        for (String part : path.split(" ")) {
            if (part.startsWith(xOrY ? "x" : "y"))
                if (StringMaster.isNumber(part.substring(1), true)) {
                    return StringMaster.getInteger(part.substring(1));
                }
        }
        return 1;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX=offsetX;
    }
    public void setOffsetY(float offsetY) {
        this.offsetY=offsetY;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
