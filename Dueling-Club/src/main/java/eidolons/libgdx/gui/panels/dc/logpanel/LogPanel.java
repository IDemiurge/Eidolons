package eidolons.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.ai.explore.AggroMaster;
import eidolons.game.core.Eidolons;
import eidolons.game.netherflame.additional.IGG_Demo;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import eidolons.libgdx.gui.panels.headquarters.HqPanel;
import eidolons.libgdx.texture.Images;
import eidolons.libgdx.texture.TextureCache;
import eidolons.system.options.GameplayOptions.GAMEPLAY_OPTION;
import eidolons.system.options.OptionsMaster;
import eidolons.system.text.DC_LogManager;
import main.content.enums.GenericEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.graphics.ColorManager;
import main.system.launch.CoreEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static main.system.GuiEventType.LOG_ENTRY_ADDED;

/**
 * Created by JustMe on 1/5/2017.
 */
public class LogPanel extends ScrollTextWrapper {

    private static boolean colorText;

    public LogPanel() {
        super(310, 500);
        if (getCallbackEvent() != null)
            bind();
    }

    public static void setColorText(boolean colorText) {
        LogPanel.colorText = colorText;
    }


    protected ScrollPanel<Message> createScrollPanel() {
        return new TextScroll() {

            public int getMaxTableElements() {
                if (!OptionsMaster.getGameplayOptions().getBooleanValue(GAMEPLAY_OPTION.LIMIT_LOG_LENGTH)) {
                    return 0;
                }
                return OptionsMaster.getGameplayOptions().getIntValue(GAMEPLAY_OPTION.LOG_LENGTH_LIMIT);
            }

            @Override
            protected void initAlignment() {
                left().bottom();
            }

            @Override
            protected void pad(ScrollPanel scrollPanel) {
                padScroll(scrollPanel);
            }

            @Override
            protected boolean isAlwaysScrolled() {
                return isScrolledAlways();
            }

            @Override
            public int getDefaultOffsetY() {
                return getInitialYOffset();
            }
        };
    }

    protected boolean isScrolledAlways() {
        return true;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if (DungeonScreen.getInstance().getController().isWithinCamera(this)) {
//            return;
//        }  if it worked, could boost performance a bit when rolled out...
        if (HqPanel.getActiveInstance()!=null) {
            return;
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected float getDefaultHeight() {
        return 310;
    }

    @Override
    protected float getDefaultWidth() {
        return 500;
    }

    public void bind() {
        GuiEventManager.bind(getCallbackEvent(), p -> {
            Actor toAdd = null;
            if (p.get() == Images.SEPARATOR_NARROW) {
                toAdd = new ImageContainer(Images.SEPARATOR_NARROW);
                scrollPanel.addElement(toAdd).center();
                return;
            } else if (p.get() == null) {
                toAdd = new ImageContainer(Images.SEPARATOR);
                scrollPanel.addElement(toAdd).center();
                return;
            }


            LogMessageBuilder builder = LogMessageBuilder.createNew();
            String text = p.get().toString();

            int align = Align.left;
            if (text.contains(DC_LogManager.ALIGN_CENTER)) {
                text = text.replace(DC_LogManager.ALIGN_CENTER, "");
                align = Align.center;
            }
            String image = null;
            if (text.contains(DC_LogManager.IMAGE_SEPARATOR)) {
                String[] parts = text.split(Pattern.quote(DC_LogManager.IMAGE_SEPARATOR));
                image = parts[0];
                text = parts[1];
            }
            if (!isColoredTextOn()) {
                builder.addString(text, ColorManager.toStringForLog(ColorManager.GOLDEN_WHITE));
            } else {
                List<Pair<String, Color>> pairs = getTextColorPairs(text);
                for (Pair<String, Color> pair : pairs) {
                    builder.addString(pair.getKey(), GdxColorMaster.toStringForLog(pair.getValue()));
                }
            }

            float imageOffset = 0;
            Image img = null;
            if (image != null) {
                img = new Image(TextureCache.getOrCreate(image));
                img.setSize(Math.min(img.getWidth(), 32),
                        Math.min(img.getHeight(), 32));
                imageOffset = img.getWidth();
            }
            LogMessage message = builder.build(getWidth() - offsetX - imageOffset);
            message.setFillParent(false);
            toAdd = message;
            if (image != null) {
                TablePanelX<Actor> table = new TablePanelX<>(
//                        getWidth() - offsetX, Math.max(message.getHeight(), img.getHeight())
                );
                table.defaults().space(2).pad(5).padLeft(7);
                table.add(img);
                table.add(message);
                table.pack();
                toAdd = table;
            }

            if (image != null) {
                scrollPanel.addElement(toAdd).padLeft(16).center();
            } else if (align == Align.center) {
                scrollPanel.addElement(toAdd).center().padLeft(28);
            } else {
                scrollPanel.addElement(toAdd).align(align).padLeft(28);
            }
        });
    }

    private boolean isColoredTextOn() {
        return colorText;
    }

    private List<Pair<String, Color>> getTextColorPairs(String text) {
        List<Pair<String, Color>> list = new ArrayList<>();

        //damage types
        /**
         * critical
         * enemy / ally
         * player-color
         */
        String[] words = text.split(" ");
        String previous = null;
        for (String word : words) {

            Color c = getColor(previous, word);
            Pair<String, Color> pair = new ImmutablePair<>(word+" ", c);
            list.add(pair);
            previous = word;
        }
        return list;
    }

    private Color getColor(String previous, String word) {
        switch (word) {
            case "Glory":
                return Color.ORANGE;
            case "Torment":
                return Color.PURPLE;
        }
        word = word.replace(".", "");
        if (previous == null) {
            previous = word;
        } else
        previous = previous.replace(".", "");
        if (Eidolons.MAIN_HERO != null)
        if (StringMaster.containsWord( Eidolons.MAIN_HERO.getName(), word)
        ) {
            if (CoreEngine.isIggDemoRunning()) {
                return GdxColorMaster.lighter((IGG_Demo.getHeroColor(Eidolons.getMainHero().getName())));
            }
            return GdxColorMaster.lighter(GdxColorMaster.getColor(Eidolons.getMainHero().getOwner().getFlagColor().getColor()));
        }
        for (Unit unit : AggroMaster.getLastAggroGroup()) {
            if (StringMaster.containsWord(unit.getName(), (word))) {
                return GdxColorMaster.lighter(GdxColorMaster.getColor(unit.getOwner().getFlagColor().getColor()));
            }
            //chck names
        }
        for (GenericEnums.DAMAGE_TYPE damage_type : GenericEnums.DAMAGE_TYPE.values()) {
            if (word.startsWith("("))
            if (word.endsWith(")"))
            if (StringMaster.cropParenthesises(word).equalsIgnoreCase(damage_type.getName())){
                return GdxColorMaster.getDamageTypeColor(damage_type);
            }
        }
        if (previous.equalsIgnoreCase("something") || word.equalsIgnoreCase("something")) {
            return GdxColorMaster.GREY;
        }
        return GdxColorMaster.PALE_GOLD;
    }

    protected GuiEventType getCallbackEvent() {
        return LOG_ENTRY_ADDED;
    }


    public void initBg() {
        if (isTiledBg()) {

        } else {
            super.initBg();
        }
    }

    private boolean isTiledBg() {
        return false;
    }

    protected String getBgPath() {
//        return new StrPathBuilder().build_("ui",
//         "components",
//         "dc",
//         "dialog",
//         "log"
//         , "log background.png");
        return null;
    }


}
