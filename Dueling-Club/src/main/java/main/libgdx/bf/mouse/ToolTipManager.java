package main.libgdx.bf.mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.libgdx.StyleHolder;
import main.libgdx.texture.TextureManager;
import main.system.GuiEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static main.libgdx.bf.mouse.ToolTipManager.ToolTip.getCurMaxVal;
import static main.system.GuiEventType.SHOW_TOOLTIP;

public class ToolTipManager extends Group {

    private final TextureRegion top;
    private final TextureRegion middle;
    private final TextureRegion bot;
    private final TextureRegion single;
    private List<ToolTip> toolTips;

    public ToolTipManager() {
        Texture imageTexture = TextureManager.getOrCreate("UI\\components\\VALUE_BOX_BIG111.png");
        single = new TextureRegion(imageTexture, 0, 0, 240, 45);
        top = new TextureRegion(imageTexture, 0, 45, 240, 45);
        middle = new TextureRegion(imageTexture, 0, 90, 240, 45);
        bot = new TextureRegion(imageTexture, 0, 135, 240, 45);

        toolTips = new ArrayList<>();

        GuiEventManager.bind(SHOW_TOOLTIP, (event) -> {
            List<ToolTipRecordOption> options = (List<ToolTipRecordOption>) event.get();
            if (options == null) {
                toolTips.forEach(this::removeActor);
            } else {
                init(options);
            }
        });
    }

    private boolean isEquals(String... names) {
        int hashCode = Objects.hash((Object[]) names);
        int curHashCode = Objects.hash(toolTips.toArray());
        return hashCode == curHashCode;
    }

    private void init(List<ToolTipRecordOption> options) {
        ToolTipRecordOption recordOption = options.get(0);
        if (toolTips.size() == options.size() && isEquals(recordOption.name)) {
            ToolTip toolTip = toolTips.get(0);
            toolTip.updateVal(getCurMaxVal(recordOption.curVal, recordOption.maxVal));
        } else {
            toolTips.forEach(this::removeActor);
            recordOption = options.get(0);
            int offsetY = (options.size() - 1) * 45;
            if (options.size() == 1) {
                offsetY = addToolTipOffset(single, offsetY, recordOption);
            } else {
                offsetY = addToolTipOffset(top, offsetY, recordOption);

                if (options.size() > 2) {
                    for (int i = 1; i < options.size() - 1; i++) {
                        recordOption = options.get(i);
                        offsetY = addToolTipOffset(middle, offsetY, recordOption);
                    }
                }

                recordOption = options.get(options.size() - 1);
                offsetY = addToolTipOffset(bot, offsetY, recordOption);
            }
        }

        Vector2 v2 = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v2 = getStage().screenToStageCoordinates(v2);
        setPosition(v2.x,v2.y);
    }

    private int addToolTipOffset(TextureRegion region, int offset, ToolTipRecordOption option) {
        ToolTip toolTip = new ToolTip(region, option.recordImage, option.name, option.curVal, option.maxVal);
        toolTip.setY(offset);
        toolTips.add(toolTip);
        addActor(toolTip);
        offset -= 45;
        return offset;
    }

    public static class ToolTip extends Group {
        private Image image = null;
        private Label name;
        private Label val;
        private int offsetX = 10;
        private Image back;

        public ToolTip(TextureRegion backTexture, Texture imageTex, String nameVal, int curVal, int maxVal) {
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

    public static class ToolTipOption {
        public int x;
        public int y;
        public List<ToolTipRecordOption> recordOptions = new ArrayList<>();
    }

    public static class ToolTipRecordOption {
        public Texture recordImage;
        public String name;
        public int curVal = -1;
        public int maxVal = -1;
    }
}
