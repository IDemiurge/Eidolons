package main.libgdx.anims.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import main.libgdx.texture.TextureManager;

import java.util.ArrayList;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation extends Animation {
    private   SpriteBatch spriteBatch;
    private ArrayList<Texture> regions;
    private float maxFrameTime;
    private float currentFrameTime;
    private float stateTime;
    private int frameCount;
    private int frame;
    float x;
    float y;
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
        spriteBatch=         new SpriteBatch();
    }
    public boolean draw(Batch batch){
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame =  getKeyFrame(stateTime, true);
        if (currentFrame==null ){
            dispose();
            return false;
        }
        batch.begin();
        batch.draw(currentFrame, x, y);
        batch.end();

        return true;
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
}
