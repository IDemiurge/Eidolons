package eidolons.system.libgdx.api;

public interface GdxManagerApi {
    void onInputGdx(Runnable r);

    void switchBackScreen();

    String getSpritePath(String s);

    boolean isImage(String s);

    String getVfxImgPath(String vfxPath);
}
