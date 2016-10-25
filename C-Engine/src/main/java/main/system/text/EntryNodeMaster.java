package main.system.text;

import main.data.ability.construct.VariableManager;
import main.game.event.Event.EVENT_TYPE;
import main.game.event.Event.STANDARD_EVENT_TYPE;
import main.system.auxiliary.FontMaster;
import main.system.auxiliary.FontMaster.FONT;

import java.awt.*;
import java.util.List;

public class EntryNodeMaster {

    public static final int INNER_HEIGHT = 200;
    public static final int INNER_WIDTH = 235; // +5 offset
    public static final int SIZE = 16;
    public static final int ROW_GAP = 4;
    public static final String CROP_SUFFIX = "...";

    public static int getMaxLinesPerHeader() {
        return 2;
    }

    // start at index 1 !

    public static ENTRY_TYPE getEntryTypeForEvent(EVENT_TYPE eventType) {
        if (eventType instanceof STANDARD_EVENT_TYPE) {
            STANDARD_EVENT_TYPE type = (STANDARD_EVENT_TYPE) eventType;
            switch (type) {
                case ROUND_ENDS:
                    return ENTRY_TYPE.ROUND_ENDS;
                case NEW_ROUND:
                    return ENTRY_TYPE.NEW_ROUND;
            }
        }
        return null;
    }

    public static String getHeader(ENTRY_TYPE type, String... args) {
        return VariableManager.getVarText(type.getHeader(), true, true, args);
        // return TextParser.parse(type.getHeader(), args);
    }

    public static List<String> getStringsFromSubNode(LogEntryNode node) {
        // List<String> strings = new LinkedList<>();
        // return strings;
        return TextWrapper.wrap(node.getHeader(), getWrapLength(false));
    }

    public static int getRowHeight(boolean top) {
        int fontHeight = FontMaster.getFontHeight(top ? getTopFont() : getSubNodeFont());
        if (top)
            fontHeight = fontHeight - 2;
        return fontHeight;
    }

    public static int getWrapLength(boolean top) {
        return FontMaster.getStringLengthForWidth(top ? getTopFont() : getSubNodeFont(),
                INNER_WIDTH);
    }

    public static Font getTopFont() {
        return FontMaster.getFont(FONT.AVQ, 18, Font.PLAIN);
    }

    public static Font getFont() {
        return FontMaster.getDefaultFont(SIZE);
    }

    public static Font getSubNodeFont() {
        return getFont();
    }

    public static Font getFont(boolean top) {
        return top ? getTopFont() : getSubNodeFont();
    }

    public enum ENTRY_TYPE {
        // TODO longer versions? 2 lines? why not?
        ATTACK(true, "{1} attacks {2} ({3})", "UI\\BF\\log icons\\attack.png"),
        COUNTER_ATTACK(true, "{1} counter-attacks {2} ({3})", "UI\\BF\\log icons\\attack.png"),
        INSTANT_ATTACK(true, "{1} - Instant Attack on {2} ({3})", "UI\\BF\\log icons\\attack.png"),
        ATTACK_OF_OPPORTUNITY(true, "{1} - Attack of Opportunity on  {2} ({3})", "UI\\BF\\log icons\\attack.png"),

        SPELL_DAMAGE(true, "{1} deals {2} damage {3}", "UI\\BF\\log icons\\damage.png"),
        DAMAGE(false, "{1} takes {2} damage {3}", "UI\\BF\\log icons\\damage.png") {
            @Override
            public String getHeader() {
                return super.getHeader();
            }
        },
        DISPLACEMENT,

        SELF_DAMAGE(true, "{1} inflicts {2} damage on self", "UI\\BF\\log icons\\damage.png") {
        },
        PARRY(true, "{1} parries {2} ({3})", "UI\\BF\\log icons\\attack.png"),
        // TODO DAMAGE_ENDURANCE ("loses {1} Endurance and {2} Toughness)
        ROLL_WON("{3} wins {1} roll", "UI\\BF\\log icons\\attack.png"),
        ROLL_LOST("{3} loses {1} roll", "UI\\BF\\log icons\\attack.png"),
        UNCONSCIOUS(true, "{1} falls unconscious", "UI\\BF\\log icons\\FALL.png"),
        KNOCKDOWN(true, "{1} is knocked down", "UI\\BF\\log icons\\FALL.png"),
        CONSCIOUS(true, "{1} regains consciousness", "UI\\BF\\log icons\\Rise.png"),
        DEATH(true, "{1} dies", "UI\\BF\\log icons\\DEATH.png"),
        FALL_UNCONSCIOUS(true, "dies", "UI\\BF\\log icons\\attack.png"),

        CHANNELING_SPELL(true, "{1} resolves", "UI\\BF\\log icons\\attack.png"),
        ACTION(true, "{1} activates {2}", "UI\\BF\\log icons\\ACTION.png"),
        MOVE(true, "{1} activates {2}", "UI\\BF\\log icons\\MOVE.png"),

        NEW_ROUND(true, "Round {1} begins", "UI\\BF\\log icons\\NEW_ROUND.png"),
        ROUND_ENDS(true, "Round {1} ends", "UI\\BF\\log icons\\ROUND_ENDS.png"),
        TRIGGERS("{1} triggers", "UI\\BF\\log icons\\attack.png"),

        ZONE_EFFECT(true, "{1} effect resolves", "UI\\BF\\log icons\\zone.png"),
        BLEEDING_RULE(false, "", ""),
        FOCUS_RULE,
        MORALE_RULE,
        MORALE_KILL_RULE,
        STAMINA_RULE,
        WEIGHT_RULE,
        WOUNDS_RULE,

        // ATTACK_OF_OPPORTUNITY,
        // COUNTER_MODIFIED,
        // BUFF_EXPIRES,

        ;
        private String header;
        private String buttonImagePath;
        private boolean top;

        ENTRY_TYPE() {
        }

        ENTRY_TYPE(String header, String buttonImagePath) {
            this(false, header, buttonImagePath);
        }

        ENTRY_TYPE(boolean top, String header, String buttonImagePath) {
            this.header = header;
            this.buttonImagePath = buttonImagePath;
            this.top = top;
        }

        public String getButtonImagePath() {
            return buttonImagePath;
        }

        public String getHeader() {
            return header;
        }

        public boolean isWriteToTop() {
            return this.top;
        }

    }

}
