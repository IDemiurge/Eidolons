package libgdx.bf;

import libgdx.GdxMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.GroupX;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/8/2018.
 */
public class LayeredActor extends GroupX {
    private final String originalPath;
    protected String rootPath;
    protected FadeImageContainer underlay;
    protected FadeImageContainer overlay;
    protected FadeImageContainer image;

    public LayeredActor(String rootPath, String overlayPath, String underlayPath) {
        this.rootPath = rootPath;
        this.originalPath = rootPath;
        addActor(underlay = new FadeImageContainer(underlayPath));
        addActor(image = new FadeImageContainer(rootPath));
        addActor(overlay = new FadeImageContainer(overlayPath));
    }
    public LayeredActor(String rootPath) {
        this(rootPath, "", "");
    }

    protected void init() {
        setSize(getDefaultWidth(), getDefaultHeight() );
        GdxMaster.center(underlay);
        GdxMaster.center(image);
        GdxMaster.center(overlay);
    }

    @Override
    public void setSize(float width, float height) {
        image.getContent().setSize(width, height);
        super.setSize(width, height);
    }

    protected float getDefaultWidth() {
        return 64;
    }
    protected float getDefaultHeight() {
        return 64;
    }

    public void setUnderlayImage(String path) {
        underlay.setImage(path);
    }
    public void setOverlayImage(String path) {
        overlay.setImage(path);
    }
    public void setImage(String path) {
        image.setImage(path);
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
        image.setImage(rootPath);
    }
    public void resetToOriginal() {
        setRootPath(originalPath);
    }
    protected String getImageVariant(String suffix) {
        return StringMaster.getAppendedImageFile(rootPath, suffix, true);
    }
}
