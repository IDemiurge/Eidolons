package eidolons.libgdx.bf;

/**
 * Created by JustMe on 5/8/2018.
 */
public class DynamicLayeredActor extends LayeredActor {

    private static final String DISABLED = " disabled";
    private static final String ACTIVE = " active";
    private static final String HOVER = " hover";

    protected ACTOR_STATUS status;

    public DynamicLayeredActor(String rootPath) {
        super(rootPath);
    }

    public void setStatus(ACTOR_STATUS status) {
        this.status = status;
        setOverlayImage(getImageVariant(" " + status));
        setUnderlayImage(getImageVariant(" " + status));
        setImage(getImageVariant(" " + status));
    }

    public void disable() {
        setStatus(ACTOR_STATUS.DISABLED);
        setImage(getImageVariant(DISABLED));
    }

    public void enable() {
        setStatus(ACTOR_STATUS.NORMAL);
        setImage(getImageVariant(DISABLED));
    }

    public enum ACTOR_STATUS {
        HOVER, NORMAL, DISABLED, ACTIVE,
    }
}
