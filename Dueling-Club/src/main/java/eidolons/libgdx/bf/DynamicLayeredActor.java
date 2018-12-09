package eidolons.libgdx.bf;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import eidolons.libgdx.shaders.DarkShader;
import eidolons.libgdx.shaders.GrayscaleShader;

/**
 * Created by JustMe on 5/8/2018.
 */
public class DynamicLayeredActor extends LayeredActor {


    protected ACTOR_STATUS status;
    private ShaderProgram shader;

    public DynamicLayeredActor(String rootPath) {
        super(rootPath);
    }

    public DynamicLayeredActor(String rootPath, String overlayPath, String underlayPath) {
        super(rootPath, overlayPath, underlayPath);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        ShaderProgram bufferedShader=null;
        if (shader!=null ){
            bufferedShader = batch.getShader();
            batch.setShader(shader);
        }
        super.draw(batch, parentAlpha);

        if (shader!=null ){
            batch.setShader(bufferedShader);
        }
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public void setStatus(ACTOR_STATUS status) {
        this.status = status;
//       TODO  setOverlayImage(getImageVariant(" " + status));
//        setUnderlayImage(getImageVariant(" " + status));
        setShader(null);
    }
    public void clearImage() {
        image.setEmpty();
    }

    public void disable() {
        setStatus(ACTOR_STATUS.DISABLED);
        setShader(DarkShader.getDarkShader());
    }
    public void block() {
        setStatus(ACTOR_STATUS.DISABLED);
        setShader(GrayscaleShader.getGrayscaleShader());
    }

    public void available() {
        setStatus(ACTOR_STATUS.ACTIVE);
    }
    public void enable() {
        setStatus(ACTOR_STATUS.NORMAL);

    }

    public enum ACTOR_STATUS {
        HOVER, NORMAL, DISABLED, ACTIVE,
    }
}
