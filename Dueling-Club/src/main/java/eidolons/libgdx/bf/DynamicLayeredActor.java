package eidolons.libgdx.bf;

/**
 * Created by JustMe on 5/8/2018.
 */
public class DynamicLayeredActor extends LayeredActor {


    protected ACTOR_STATUS status;

    public DynamicLayeredActor(String rootPath) {
        super(rootPath);
    }

    public void setStatus(ACTOR_STATUS status) {
        this.status = status;
        setOverlayImage(getImageVariant(" " + status));
        setUnderlayImage(getImageVariant(" " + status));
    }
    public void clearImage() {
        image.setEmpty();
    }
    public void disable() {
        setStatus(ACTOR_STATUS.DISABLED);
    }

    public void enable() {
        setStatus(ACTOR_STATUS.NORMAL);
    }

    public enum ACTOR_STATUS {
        HOVER, NORMAL, DISABLED, ACTIVE,
    }
}
