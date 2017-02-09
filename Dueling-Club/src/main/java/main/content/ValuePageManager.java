package main.content;

import main.client.dc.Launcher;
import main.client.dc.Launcher.VIEWS;
import main.content.ValuePages.PAGE_NAMES;
import main.content.properties.G_PROPS;
import main.system.auxiliary.ArrayMaster;
import main.system.auxiliary.MapMaster;
import main.system.auxiliary.StringMaster;

import java.util.*;
//UNITS 
//SPELLS 
//CHARS 
//ABILS 
//BF_OBJ

//BUFFS
//ACTIONS
//ARMOR
//WEAPONS
//SKILLS

//ITEMS
//CLASSES

//ALL 

//DEITIES 
//ENCOUNTERS 
//TERRAIN 
public class ValuePageManager {
    public static final VALUE[][][] altPageArray = {ValuePages.ALT_UNIT_PAGES,
            ValuePages.ALT_SPELL_PAGES, ValuePages.ALT_CHAR_PAGES, null

            , ValuePages.ALT_BF_OBJ_PAGES, null, ValuePages.ACTION_PAGES_DC,
            HC_ValuePages.ARMOR_PAGES, HC_ValuePages.WEAPONS_PAGES, ValuePages.SKILL_PAGES,
            ValuePages.ALT_QUICK_ITEM_PAGES, ValuePages.JEWELRY_PAGES, new VALUE[0][0],
            ValuePages.CLASS_PAGES

    };
    public static final VALUE[][][] pageArray = {ValuePages.UNIT_PAGES, ValuePages.SPELL_PAGES,
            ValuePages.CHAR_PAGES, null

            , null, null, ValuePages.ACTION_PAGES, ValuePages.ARMOR_PAGES, ValuePages.WEAPON_PAGES,
            ValuePages.SKILL_PAGES, ValuePages.QUICK_ITEM_PAGES, ValuePages.JEWELRY_PAGES,
            new VALUE[0][0], ValuePages.CLASS_PAGES

    };

    // static arrays worked into maps
    // separate props and params???
    private static final String AV_HEADER = PAGE_NAMES.AV_HEADER.name();
    private static final String DC_HEADER = PAGE_NAMES.HEADER.name();
    private static final String AV_TRAILING_PAGE = PAGE_NAMES.AV_TRAILING_PAGE.name();
    private static final String DC_TRAILING_PAGE = PAGE_NAMES.DC_TRAILING_PAGE.name();
    private static Map<OBJ_TYPE, Map<String, List<VALUE>>> pageMaps;
    private static Map<OBJ_TYPE, Map<String, List<VALUE>>> altPageMaps;
    private static Map<OBJ_TYPE, List<VALUE>> avAdditionalPropPages;
    private static Map<OBJ_TYPE, List<VALUE>> additionalPropPages;
    private static Map<OBJ_TYPE, List<VALUE>> avAdditionalParamPages;
    private static Map<OBJ_TYPE, List<VALUE>> additionalParamPages;

    // ADDITIONAL={
    // CUSTOM_VALUES,
    // }

    // construct page maps to be used for getting page lists
    public static void init() {
        pageMaps = new LinkedHashMap<OBJ_TYPE, Map<String, List<VALUE>>>();
        initMaps(pageMaps, pageArray, ValuePages.PAGE_NAMES, false);
        altPageMaps = new LinkedHashMap<OBJ_TYPE, Map<String, List<VALUE>>>();
        initMaps(altPageMaps, altPageArray, ValuePages.ALT_PAGE_NAMES, true);

    }

    private static void initMaps(Map<OBJ_TYPE, Map<String, List<VALUE>>> pageMaps,
                                 VALUE[][][] pageArray, String[] PAGE_NAMES, boolean alt) {
        for (OBJ_TYPES TYPE : OBJ_TYPES.values()) {
            int i = TYPE.getCode();
            if (pageArray.length <= i) {
                break;
            }
            VALUE[][] pages = pageArray[i];
            if (pages == null) {
                continue;
            }
            List<String> pageNames = StringMaster.openContainer(PAGE_NAMES[i]);

            List<List<VALUE>> pageList = new ArrayMaster<VALUE>().get2dList(pages);
            Map<String, List<VALUE>> map = new MapMaster<String, List<VALUE>>().constructMap(
                    pageNames, pageList);
            if (!alt) {
                map.put(DC_HEADER, Arrays.asList(ValuePages.GENERIC_DC_HEADER));
                map.put(AV_HEADER, Arrays.asList(ValuePages.GENERIC_AV_HEADER));
                map.put(AV_TRAILING_PAGE, Arrays.asList(ValuePages.AV_TRAILING_PAGE));
                map.put(DC_TRAILING_PAGE, Arrays.asList(ValuePages.DC_TRAILING_PAGE));

            }
            pageMaps.put(TYPE, map);
        }
    }

    public static List<List<VALUE>> getGenericValuesForInfoPages() {
        List<List<VALUE>> list = new LinkedList<>();
        list.add(Arrays.asList(ValuePages.GENERIC_DC_HEADER));
        return list;
    }

    public static List<VALUE> getOutlineValues() {
        return new LinkedList<>(Arrays.asList(ValuePages.OUTLINE_VALUES));
    }

    public static List<List<VALUE>> getValuesForHCInfoPages(OBJ_TYPE TYPE) {
        VALUE[][] pages = null;
        if (TYPE == OBJ_TYPES.DEITIES) {
            pages = HC_ValuePages.DEITY_PAGES;
        }
        if (TYPE == OBJ_TYPES.CHARS) {
            if (Launcher.getMainManager().getSequence() == null) {
                pages = HC_ValuePages.CHAR_PAGES;
            } else if (Launcher.getView() == VIEWS.CHOICE
                    && Launcher.getMainManager().getSequence().isActive())
                // Launcher.getMainManager().getSequenceMaster()
            {
                pages = HC_ValuePages.BACKGROUND_CHAR_PAGES;
            } else {
                pages = HC_ValuePages.CHAR_PAGES;
            }
        }
        if (TYPE == OBJ_TYPES.ACTIONS) {
            pages = HC_ValuePages.ACTION_PAGES;
        }
        if (TYPE == OBJ_TYPES.JEWELRY) {
            pages = ValuePages.JEWELRY_PAGES;
        }
        if (TYPE == OBJ_TYPES.WEAPONS) {
            pages = HC_ValuePages.WEAPONS_PAGES;
        }
        if (TYPE == OBJ_TYPES.ARMOR) {
            pages = HC_ValuePages.ARMOR_PAGES;
        }
        if (TYPE == OBJ_TYPES.PARTY) {
            pages = HC_ValuePages.PARTY_PAGES;
        }
        if (TYPE == OBJ_TYPES.SKILLS) {
            pages = HC_ValuePages.SKILL_PAGES;
        }
        if (TYPE == OBJ_TYPES.CLASSES) {
            pages = HC_ValuePages.CLASS_PAGES;
        }
        if (pages == null) {
            return null;
        }
        List<List<VALUE>> lists = new LinkedList<>();
        for (VALUE[] page : pages) {
            LinkedList<VALUE> list = new LinkedList<>(Arrays.asList(page));
            lists.add(list);
        }
        addDynamicPagesHC(lists, TYPE);
        return lists;

    }

    private static void addDynamicPagesHC(List<List<VALUE>> lists, OBJ_TYPE TYPE) {
        lists.addAll(SpecialValuePages.getDynamicPages(TYPE, true));
    }

    private static void addDynamicPagesDC(List<List<VALUE>> lists, OBJ_TYPE TYPE) {
        lists.addAll(SpecialValuePages.getDynamicPages(TYPE, false));

    }

    public static List<List<VALUE>> getValuesForDCInfoPages(OBJ_TYPE TYPE) {
        return getPageLists(true, false, TYPE, getPageNames(TYPE));

    }

    public static List<VALUE> getValuesForDC(OBJ_TYPE TYPE) {
        return getMergedPageList(true, false, TYPE, getPageNames(TYPE, false));

    }

    public static List<VALUE> getValuesForAV(OBJ_TYPE TYPE) {

        return getMergedPageList(true, true, TYPE, getPageNames(TYPE, true));

    }

    // filter here or elsewhere?
    // construct additional page
    // add null elements between pages

    private static List<VALUE> getMergedPageList(boolean filter, boolean av, OBJ_TYPE TYPE,
                                                 List<String> pageNames) {
        List<VALUE> list = new LinkedList<VALUE>();
        List<List<VALUE>> pages = getPageLists(filter, av, TYPE, pageNames);
        pages.add(getAdditionalPage(av, pages, TYPE, true));
        pages.add(getAdditionalPage(av, pages, TYPE, false));
        for (List<VALUE> page : pages) {
            if (filter) {
                page = getFilteredValueList(page, TYPE, av);
            }
            list.addAll(page);
            if (av) {
                list.add(G_PROPS.EMPTY_VALUE);
            }
        }
        return list;
    }

    private static List<VALUE> getMergedPageList(OBJ_TYPE TYPE, List<String> pageNames) {
        return getMergedPageList(true, false, TYPE, pageNames);
    }

    private static List<List<VALUE>> getPageLists(boolean filter, boolean av, OBJ_TYPE TYPE,
                                                  List<String> pageNames) {
        List<List<VALUE>> list = new LinkedList<List<VALUE>>();
        Map<String, List<VALUE>> map = (av) ? pageMaps.get(TYPE) : altPageMaps.get(TYPE);
        if (pageNames == null) {
            return null;
        }
        for (String name : pageNames) {
            List<VALUE> page = map.get(name);
            if (filter) {
                page = getFilteredValueList(page, TYPE, av);
            }
            // dynamic/av filters?
            list.add(page);
        }
        addDynamicPagesDC(list, TYPE);
        return list;
    }

    private static List<VALUE> getAdditionalPage(boolean av, List<List<VALUE>> list, OBJ_TYPE TYPE,
                                                 boolean prop) {
        List<VALUE> page = getAdditionalPages(av, prop).get(TYPE);
        if (page == null) {
            page = new LinkedList<>();
            for (VALUE p : (!prop) ? ContentManager.getParamsForType(TYPE.getName(), !av)
                    : ContentManager.getPropsForType(TYPE.getName(), !av)) {
                if (!ContentManager.isValueForOBJ_TYPE(TYPE, p)) {
                    continue;
                }
                if (checkValueInPages(list, p)) {
                    continue;
                }
                page.add(p);
            }
            getAdditionalPages(av, prop).put(TYPE, page);
        }
        return page;
    }

    public static boolean checkValueInPages(List<List<VALUE>> pages, VALUE v) {
        for (List<VALUE> page : pages) {
            if (page.contains(v)) {
                return true;
            }
        }
        return false;
    }

    private static Map<OBJ_TYPE, List<VALUE>> getAdditionalPages(boolean av, boolean prop) {
        if (prop) {
            if (av) {
                if (avAdditionalPropPages == null) {
                    avAdditionalPropPages = new HashMap<OBJ_TYPE, List<VALUE>>();
                }
                return avAdditionalPropPages;
            } else {
                if (additionalPropPages == null) {
                    additionalPropPages = new HashMap<OBJ_TYPE, List<VALUE>>();
                }
                return additionalPropPages;
            }
        } else {
            if (av) {
                if (avAdditionalParamPages == null) {
                    avAdditionalParamPages = new HashMap<OBJ_TYPE, List<VALUE>>();
                }
                return avAdditionalParamPages;
            } else {
                if (additionalParamPages == null) {
                    additionalParamPages = new HashMap<OBJ_TYPE, List<VALUE>>();
                }
                return additionalParamPages;
            }
        }
    }

    private static List<VALUE> getFilteredValueList(List<VALUE> page, OBJ_TYPE TYPE, boolean av) {
        List<VALUE> list = new LinkedList<>();
        for (VALUE v : page) {
            if (av) {
                if (v.isDynamic() && !v.isWriteToType()) {
                    continue;
                }
            }

            if (ContentManager.isValueForOBJ_TYPE(TYPE, v)) {
                list.add(v);
            }
        }
        return list;
    }

    private static List<String> getPageNames(OBJ_TYPE TYPE) {
        return getPageNames(TYPE, true, false);
    }

    private static List<String> getPageNames(OBJ_TYPE TYPE, boolean av) {
        return getPageNames(TYPE, false, av);
    }

    private static List<String> getPageNames(OBJ_TYPE TYPE, boolean alt, boolean av) {
        String[] pageNames = (alt) ? ValuePages.ALT_PAGE_NAMES : ValuePages.PAGE_NAMES;
        if (TYPE.getCode() >= pageNames.length) {
            return null;
        }
        List<String> stringList = StringMaster.openContainer(pageNames[TYPE.getCode()]);
        if (stringList.isEmpty()) {
            return null;
        }
        if (!alt) {
            stringList.add(0, (av) ? AV_HEADER : DC_HEADER);
        }
        return stringList;
    }

    public static List<VALUE> getValuePage(String key, OBJ_TYPE TYPE) {
        return new LinkedList<>(pageMaps.get(TYPE).get(key));
    }

    public static List<VALUE> getValuePage(int index, OBJ_TYPE TYPE) {
        return new LinkedList<VALUE>(Arrays
                .asList((VALUE[]) pageMaps.get(TYPE).keySet().toArray()[index]));
    }

    private static String getAvPageName(OBJ_TYPE TYPE) {
        return AV_HEADER;
    }

    public static List<List<VALUE>> getValueLists(VALUE[][] pages) {
        List<List<VALUE>> list = new LinkedList<List<VALUE>>();
        for (VALUE[] p : pages) {
            list.add(Arrays.asList(p));
        }
        return list;
    }

}
