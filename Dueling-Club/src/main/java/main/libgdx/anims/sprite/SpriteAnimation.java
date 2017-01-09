package main.libgdx.anims.sprite;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * Created by PC on 10.11.2016.
 */
public class SpriteAnimation {
    private ArrayList<Texture> regions;
    private float maxFrameTime;
    private float currentFrameTime;
    private int frameCount;
    private int  frame;
    public SpriteAnimation(ArrayList regions, int frameCount, float cycleTime){
        this.regions  = regions;
       this.frameCount = frameCount;
        maxFrameTime = cycleTime/frameCount;
        frame = 0;

    }
    public void update(float v){
        currentFrameTime +=v;
        if (currentFrameTime > maxFrameTime){
            frame++;
            currentFrameTime = 0;
        }
        if (frame >= frameCount){
            frame = 0;
        }
    }
    public Texture getTexture(){
        return regions.get(frame);
    }
}
