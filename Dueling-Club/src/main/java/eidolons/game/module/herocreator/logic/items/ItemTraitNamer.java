package eidolons.game.module.herocreator.logic.items;

import eidolons.content.DC_CONSTS.ITEM_LEVEL;
import eidolons.game.battlecraft.DC_Engine;
import eidolons.game.module.nethergate.apholon.eidola.Eidolon;
import eidolons.game.module.nethergate.apholon.eidola.EidolonImbuer;
import main.content.C_OBJ_TYPE;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Localization;
import main.system.auxiliary.StringMaster;
import org.junit.Test;

import java.util.*;

/**
 * Created by JustMe on 12/7/2018.
 */
public class ItemTraitNamer {
    private static final String ITEM_NAME_TRAITS_SEPARATOR = " with traits ";

    private static void testNaming(DC_TYPE TYPE) {
        String names = "";
        for (int i = 0; i < 5; i++) {
            ObjType type = DataManager.getRandomType(TYPE);
            Set<ItemTrait> traits = new EidolonImbuer().getTraits(
             new Eidolon(DataManager.getRandomType(DC_TYPE.UNITS)), type);
            names += "\n" +
             getName(type, new ArrayList<>(traits));
        }
        main.system.auxiliary.log.LogMaster.log(1, "Result: \n " + names);
    }

    static String getName(ObjType type, List<ItemTrait> traits) {
        //TODO certain combo's should be mapped to CUSTOM SINGLE NAMES, e.g. Reaper, Carnifex etc
        String base = type.getProperty(G_PROPS.BASE_TYPE);
        if (base.isEmpty()) {
            base = type.getName();
        }
        String prefix = "";
        String suffix = "";
        boolean materialName;
        boolean qualityName;

        //traits are sorted by power... first is the most powerful
        int n = EnumMaster.getEnumConstIndex(ITEM_LEVEL.class, traits.get(0).getLevel());
        //naming priority?
        String[] adjectives = toAdjectives(traits);
        String[] nouns = toNouns(traits);
        //            String[] adjectives= toAdjectives(traits);

        switch (traits.size()) {
            case 1:
                boolean preferAdjective = traits.get(0).template.preferAdjective;
                if (preferAdjective) {
                    prefix = adjectives[0];
                } else {
                    suffix = Localization.of(nouns[0]);
                }
                break;
            case 2:
                int first = traits.get(0).getTemplate().preferAdjective ? 0 : 1;
                int second = !traits.get(0).getTemplate().preferAdjective ? 0 : 1;

                //suf + pre Blackened Spear of Valor
                prefix = adjectives[first];
                suffix = Localization.of(nouns[second]);

                break;
            case 3:
                //sort by level? ; no random - so we can get by type name always!

                int n1 = (0);
                int n2 = (1);
                int n3 = (2);
                if (traits.get(2).getTemplate().preferAdjective) {
                    n1 = 2;
                    n3 = 0;
                }
                prefix = adjectives[n1];
                suffix = Localization.of(
                 adjectives[n2] + " " + nouns[n3]);
                // pref for suf! e.g. Horrid Axe of Ravenous Glory
                break;
            case 4:
                //find custom name!
                break;
        }
        return StringMaster.getWellFormattedString(prefix + " " + base + " " + suffix).trim()
         .replace("Of", "of");
    }

    private static String[] toNouns(List<ItemTrait> traits) {
        return to(false, traits);
    }

    private static String[] toAdjectives(List<ItemTrait> traits) {
        return to(true, traits);
    }

    private static String[] to(boolean adjectives, List<ItemTrait> traits) {
        String[] array = new String[traits.size()];

        for (int a = 0; a < traits.size(); a++) {
            int i = a;
            String[] from = adjectives ? traits.get(i).getTemplate().adjectives
             : traits.get(i).getTemplate().nouns;
            if (from.length == 0) {
                continue;
            }
            if (i >= from.length)
                i = from.length - 1;
            String adj = from[i];
            if (!isValid(adj)) {
                int offset = 1;//always forward! save name must be consistent, not random...
                if (i + offset >= from.length)
                    offset = -1;
                adj = from[i + offset];
            }
            array[i] = adj;
        }
        return array;
    }

    private static boolean isValid(String noun) {
        if (noun.isEmpty()) {
            return false;
        }
        return !noun.equals(".");
    }

    public static ObjType restoreByName(String name) {
        String typeName = name.split(ITEM_NAME_TRAITS_SEPARATOR)[0];
        String traitsString = name.split(ITEM_NAME_TRAITS_SEPARATOR)[1];
        ObjType type = DataManager.getType(typeName, C_OBJ_TYPE.ITEMS);

        Set<ItemTrait> traits = new LinkedHashSet<>();
        for (String substring : ContainerUtils.openContainer(traitsString, ", ")) {
            traits.add(new ItemTrait(substring));
        }
        ItemTraitParser.applyTraits(type, traits);
        return type;
    }

    public static String getDescriptor(Entity item, Collection<ItemTrait> traits) {
        String suffix = ITEM_NAME_TRAITS_SEPARATOR;
        for (ItemTrait trait : traits) {
            suffix += trait.toString() + ", ";
        }
        return item.getDisplayedName() + suffix.substring(0, suffix.length() - 2);
    }

    //TODO LOCALIZATION?!
    @Test
    public void test() {
        DC_Engine.mainMenuInit();
        testNaming(DC_TYPE.WEAPONS);
        testNaming(DC_TYPE.ARMOR);

    }

    public enum CUSTOM_ITEM_ADJECTIVE {
        HOLLOW,
        FORMLESS,
        HAUNTING,
        //MOSTLY USE NORMAL ADJECTIVES?
    }

    public enum CUSTOM_ITEM_NAME {
        CARNIFEX,

        RIPPER,

        SOUL_REAVER,

        CLEAVER,


    }
}
