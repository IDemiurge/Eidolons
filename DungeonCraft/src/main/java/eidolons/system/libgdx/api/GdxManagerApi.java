package eidolons.system.libgdx.api;

import eidolons.system.libgdx.wrapper.Vector2;
import main.entity.Entity;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.logic.event.Event;

public interface GdxManagerApi {
    void onInputGdx(Runnable r);

    void switchBackScreen();

    String getSpritePath(String s);

    boolean isImage(String s);

    String getVfxImgPath(String vfxPath);

    void checkHpBarReset(Obj sourceObj);

    boolean isEventDisplayable(Event event);

    boolean isEventAnimated(Event event);


}
