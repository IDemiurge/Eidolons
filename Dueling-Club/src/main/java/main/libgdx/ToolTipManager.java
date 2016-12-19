package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import main.system.TempEventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static main.libgdx.ToolTipManager.ToolTip.getCurMaxVal;

public class ToolTipManager extends Group {

    private final TextureRegion top;
    private final TextureRegion middle;
    private final TextureRegion bot;
    private final TextureRegion single;
    private List<ToolTip> toolTips;

    public ToolTipManager(TextureCache textureCache) {
        Texture imageTexture = textureCache.getOrCreate("UI\\components\\VALUE_BOX_BIG111.png");
        single = new TextureRegion(imageTexture, 0, 0, 240, 45);
        top = new TextureRegion(imageTexture, 0, 45, 240, 45);
        middle = new TextureRegion(imageTexture, 0, 90, 240, 45);
        bot = new TextureRegion(imageTexture, 0, 135, 240, 45);

        toolTips = new ArrayList<>();

        TempEventManager.bind("show-tooltip", (event) -> {
            ToolTipOption options = (ToolTipOption) event.get();
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

    private void init(ToolTipOption option) {
        ToolTipRecordOption recordOption = option.recordOptions.get(0);
        if (toolTips.size() == option.recordOptions.size() && isEquals(recordOption.name)) {
            ToolTip toolTip = toolTips.get(0);
            toolTip.updateVal(getCurMaxVal(recordOption.curVal, recordOption.maxVal));
        } else {
            toolTips.forEach(this::removeActor);
            recordOption = option.recordOptions.get(0);
            int offsetY = (option.recordOptions.size() - 1) * 45;
            if (option.recordOptions.size() == 1) {
                offsetY = addToolTipOffset(single, offsetY, recordOption);
            } else {
                offsetY = addToolTipOffset(top, offsetY, recordOption);

                if (option.recordOptions.size() > 2) {
                    for (int i = 1; i < option.recordOptions.size() - 1; i++) {
                        recordOption = option.recordOptions.get(i);
                        offsetY = addToolTipOffset(middle, offsetY, recordOption);
                    }
                }

                recordOption = option.recordOptions.get(option.recordOptions.size() - 1);
                offsetY = addToolTipOffset(bot, offsetY, recordOption);
            }
        }
        setX(option.x);
        setY(option.y);
    }

    private int addToolTipOffset(TextureRegion region, int offset, ToolTipRecordOption option) {
        ToolTip toolTip = new ToolTip(region, option.recordImage, option.name, option.curVal, option.maxVal);
        toolTip.setY(offset);
        toolTips.add(toolTip);
        addActor(toolTip);
        offset -= 45;
        return offset;
    }

    private void initMany(ToolTipOption options) {

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

        @Override
        public int hashCode() {
            return Objects.hashCode(name.getText().toString());
        }

        public void updateVal(String sval) {
            val = new Label(sval, StyleHolder.getDefaultLabelStyle());
        }

        public static String getCurMaxVal(int curVal, int maxVal) {
            StringBuilder sb = new StringBuilder();
            sb.append(curVal);
            if (maxVal != -1) {
                sb.append("\\");
                sb.append(maxVal);
            }

            return sb.toString();
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
        public int curVal;
        public int maxVal = -1;
    }
}
