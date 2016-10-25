package main.game.logic.macro.entity;

import main.content.MACRO_OBJ_TYPES;
import main.content.VALUE;
import main.content.ValuePageManager;
import main.content.parameters.MACRO_PARAMS;
import main.content.properties.G_PROPS;
import main.content.properties.MACRO_PROPS;

import java.util.List;

public class MacroValuePages {

    public static final VALUE[] PLACE_HEADER = {MACRO_PROPS.AREA,
            MACRO_PROPS.REGION, MACRO_PROPS.CONCEALMENT_LEVEL,
            G_PROPS.DESCRIPTION, G_PROPS.LORE,};
    public static final VALUE[] ROUTE_HEADER = {MACRO_PROPS.REGION,
            MACRO_PROPS.DESTINATION, MACRO_PROPS.ORIGIN,
            MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE, MACRO_PARAMS.ROUTE_LENGTH,
            MACRO_PARAMS.ROUTE_LENGTH_REMAINING, MACRO_PROPS.CONCEALMENT_LEVEL,
            G_PROPS.DESCRIPTION, G_PROPS.LORE,};
    public static final VALUE[] MACRO_CHAR_HEADER = {MACRO_PROPS.REGION,
            MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE,};
    public static final VALUE[] TOWN_HEADER = {MACRO_PROPS.REGION,
            MACRO_PARAMS.ROUTE_PROGRESS_PERCENTAGE,};
    public static final VALUE[][] PLACE_PAGES = {PLACE_HEADER};
    public static final VALUE[][] MACRO_CHAR_PAGES = {MACRO_CHAR_HEADER

    };
    public static final VALUE[][] TOWN_PAGES = {TOWN_HEADER

    };
    public static final VALUE[][] ROUTE_PAGES = {ROUTE_HEADER};

    public static List<List<VALUE>> getPageLists(MACRO_OBJ_TYPES TYPE) {
        VALUE[][] pages = MACRO_CHAR_PAGES;
        switch (TYPE) {
            case PLACE:
                pages = PLACE_PAGES;
                break;
            case ROUTE:
                pages = ROUTE_PAGES;
                break;
            default:
                break;

        }
        return ValuePageManager.getValueLists(pages);

    }
    // ++ SHOPS, FACTIONS, ...
}
