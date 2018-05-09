package eidolons.libgdx.bf;

import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 5/8/2018.
 */
public class LayeredActor extends GroupX {
    protected String rootPath;
    protected FadeImageContainer underlay;
    protected FadeImageContainer overlay;
    protected FadeImageContainer image;

    public LayeredActor(String rootPath) {
        this.rootPath = rootPath;

        addActor(underlay = new FadeImageContainer());
        addActor(image = new FadeImageContainer(rootPath));
        addActor(overlay = new FadeImageContainer());
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

    protected String getImageVariant(String suffix) {
        return StringMaster.getAppendedImageFile(rootPath, suffix);
    }
}
