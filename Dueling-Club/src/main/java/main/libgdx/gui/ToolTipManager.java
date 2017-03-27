package main.libgdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import main.libgdx.StyleHolder;
import main.libgdx.gui.dialog.ToolTip;
import main.libgdx.gui.dialog.ToolTipBackgroundHolder;
import main.system.GuiEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static main.libgdx.gui.ToolTipManager.InnerToolTip.getCurMaxVal;
import static main.libgdx.gui.dialog.ToolTipBackgroundHolder.*;
import static main.system.GuiEventType.SHOW_TOOLTIP;

public class ToolTipManager extends Container {
    private List<InnerToolTip> innerToolTips;

    public ToolTipManager() {

        innerToolTips = new ArrayList<>();
        fill().top().left();
        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {
            Object object = event.get();
            if (object == null) {
                innerToolTips.clear();
                setActor(null);
            } else {
                if (object instanceof ToolTip) {
                    init((ToolTip) object);
                } else {
                    init((List<ToolTipRecordOption>) object);
                }
            }
        });
    }

    private boolean isEquals(String... names) {
        int hashCode = Objects.hash((Object[]) names);
        int curHashCode = Objects.hash(innerToolTips.toArray());
        return hashCode == curHashCode;
    }

    private void init(ToolTip toolTip) {
        toolTip.top().left();
        final float toolTipWidth = toolTip.getWidth();
        setActor(toolTip);
        if (toolTipWidth != 0) {
            width(toolTipWidth);
        }
        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x + 10, v2.y);
    }

    private void init(List<ToolTipRecordOption> options) {
        ToolTipRecordOption recordOption = options.get(0);
        //todo make working cache check before remove this ugly hack
        if (true == false && innerToolTips.size() == options.size() && isEquals(recordOption.name)) {
            InnerToolTip innerToolTip = innerToolTips.get(0);
            innerToolTip.updateVal(getCurMaxVal(recordOption.curVal, recordOption.maxVal));
        } else {
            innerToolTips.forEach(this::removeActor);
            innerToolTips.clear();
            recordOption = options.get(0);
            int offsetY = (options.size() - 1) * 45;
            if (options.size() == 1) {
                offsetY = addToolTipOffset(getSingle(), offsetY, recordOption);
            } else {
                offsetY = addToolTipOffset(ToolTipBackgroundHolder.getTop(), offsetY, recordOption);

                if (options.size() > 2) {
                    for (int i = 1; i < options.size() - 1; i++) {
                        recordOption = options.get(i);
                        offsetY = addToolTipOffset(getMid(), offsetY, recordOption);
                    }
                }

                recordOption = options.get(options.size() - 1);
                offsetY = addToolTipOffset(getBot(), offsetY, recordOption);
            }
        }

        Table table = new Table();
        innerToolTips.forEach(el -> {
            table.row().fill().left().bottom();
            table.add(el);
        });

        setActor(table);

        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x, v2.y);
    }

    private int addToolTipOffset(TextureRegion region, int offset, ToolTipRecordOption option) {
        InnerToolTip innerToolTip = new InnerToolTip(region, option.recordImage, option.name, option.curVal, option.maxVal);
        innerToolTip.setHeight(45);
        innerToolTips.add(innerToolTip);
        return offset;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;//this is untouchable element
    }

    public static class InnerToolTip extends Group {
        private Image image = null;
        private Label name;
        private Label val;
        private int offsetX = 10;
        private Image back;

        public InnerToolTip(TextureRegion backTexture, Texture imageTex, String nameVal, int curVal, int maxVal) {
            back = new Image(backTexture);
            setHeight(back.getHeight());
            setWidth(back.getWidth());
            addActor(back);
            if (imageTex != null) {
                image = new Image(imageTex);
                image.setX(offsetX);
                image.setY(back.getHeight() / 2 - image.getHeight() / 2);
                offsetX += image.getWidth() + 10;
                addActor(image);
            }
            name = new Label(nameVal, StyleHolder.getDefaultLabelStyle());
            name.setX(offsetX);
            name.setY(back.getHeight() / 2 - name.getHeight() / 2);
            offsetX += name.getWidth() + 10;
            addActor(name);

            updateVal(getCurMaxVal(curVal, maxVal));
            val.setX(offsetX);
            val.setY(back.getHeight() / 2 - val.getHeight() / 2);
            offsetX += val.getWidth() + 10;
            addActor(val);
        }

        public static String getCurMaxVal(int curVal, int maxVal) {
            StringBuilder sb = new StringBuilder();
            if (curVal != -1) {
                sb.append(curVal);

                if (maxVal != -1) {
                    sb.append("\\");
                    sb.append(maxVal);
                }
            }

            return sb.toString();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name.getText().toString());
        }

        public void updateVal(String sval) {
            val = new Label(sval, StyleHolder.getDefaultLabelStyle());
        }
    }

    public static class ToolTipRecordOption {
        public Texture recordImage;
        public String name;
        public int curVal = -1;
        public int maxVal = -1;
    }
}
