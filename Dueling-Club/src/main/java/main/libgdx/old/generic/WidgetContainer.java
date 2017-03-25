package main.libgdx.old.generic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * Created by JustMe on 1/11/2017.
 */
public interface WidgetContainer extends Layout {
    public void add(WidgetContainer c);

    public void addActor(Actor actor);

    public WidgetGroup top();

    void invalidate();

    void layout();

    float getPrefWidth();

    float getPrefHeight();

    void setRound(boolean round);

    WidgetGroup reverse();

    WidgetGroup reverse(boolean reverse);

    boolean getReverse();

    WidgetGroup space(float space);

    float getSpace();

    WidgetGroup wrapSpace(float wrapSpace);

    float getWrapSpace();

    WidgetGroup pad(float pad);

    WidgetGroup pad(float top, float left, float bottom, float right);

    WidgetGroup padTop(float padTop);

    WidgetGroup padLeft(float padLeft);

    WidgetGroup padBottom(float padBottom);

    WidgetGroup padRight(float padRight);

    float getPadTop();

    float getPadLeft();

    float getPadBottom();

    float getPadRight();

    WidgetGroup align(int align);

    WidgetGroup center();

    WidgetGroup left();

    WidgetGroup bottom();

    WidgetGroup right();

    int getAlign();

    WidgetGroup fill();

    WidgetGroup fill(float fill);

    float getFill();

    WidgetGroup wrap();

    WidgetGroup wrap(boolean wrap);

    boolean getWrap();

//    void setPosition(int i, float height);

    float getHeight();

    void setHeight(float n);

    float getWidth();

    void setWidth(float n);

    float getX();

    void setX(float n);

    float getY();

    void setY(float n);

    boolean addListener(EventListener listener);

    void clear();
}
