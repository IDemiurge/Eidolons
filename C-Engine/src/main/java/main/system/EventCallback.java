package main.system;

import com.badlogic.gdx.Screen;

/**
 * Created with IntelliJ IDEA.
 * Date: 04.11.2016
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public interface EventCallback {
    void call(EventCallbackParam obj);

    default Screen getScreen(){ return null;}
}
