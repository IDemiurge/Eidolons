package main.libgdx.gui.panels.generic.sub;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import main.libgdx.gui.panels.generic.WidgetContainer;

/**
 * Created by JustMe on 1/11/2017.
 */
public class Root extends WidgetGroup implements WidgetContainer {
    @Override
    public void add(WidgetContainer c) {
        super.addActor((Actor) c);
        debug();
    }

    @Override
    public WidgetGroup top() {
        return null;
    }

    @Override
    public void setRound(boolean round) {

    }

    @Override
    public WidgetGroup reverse() {
        return null;
    }

    @Override
    public WidgetGroup reverse(boolean reverse) {
        return null;
    }

    @Override
    public boolean getReverse() {
        return false;
    }

    @Override
    public WidgetGroup space(float space) {
        return null;
    }

    @Override
    public float getSpace() {
        return 0;
    }

    @Override
    public WidgetGroup wrapSpace(float wrapSpace) {
        return null;
    }

    @Override
    public float getWrapSpace() {
        return 0;
    }

    @Override
    public WidgetGroup pad(float pad) {
        return null;
    }

    @Override
    public WidgetGroup pad(float top, float left, float bottom, float right) {
        return null;
    }

    @Override
    public WidgetGroup padTop(float padTop) {
        return null;
    }

    @Override
    public WidgetGroup padLeft(float padLeft) {
        return null;
    }

    @Override
    public WidgetGroup padBottom(float padBottom) {
        return null;
    }

    @Override
    public WidgetGroup padRight(float padRight) {
        return null;
    }

    @Override
    public float getPadTop() {
        return 0;
    }

    @Override
    public float getPadLeft() {
        return 0;
    }

    @Override
    public float getPadBottom() {
        return 0;
    }

    @Override
    public float getPadRight() {
        return 0;
    }

    @Override
    public WidgetGroup align(int align) {
        return null;
    }

    @Override
    public WidgetGroup center() {
        return null;
    }

    @Override
    public WidgetGroup left() {
        return null;
    }

    @Override
    public WidgetGroup bottom() {
        return null;
    }

    @Override
    public WidgetGroup right() {
        return null;
    }

    @Override
    public int getAlign() {
        return 0;
    }

    @Override
    public WidgetGroup fill() {
        return null;
    }

    @Override
    public WidgetGroup fill(float fill) {
        return null;
    }

    @Override
    public float getFill() {
        return 0;
    }

    @Override
    public WidgetGroup wrap() {
        return null;
    }

    @Override
    public WidgetGroup wrap(boolean wrap) {
        return null;
    }

    @Override
    public boolean getWrap() {
        return false;
    }
}
