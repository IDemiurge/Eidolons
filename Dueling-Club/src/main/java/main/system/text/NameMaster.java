package main.system.text;

import main.content.OBJ_TYPE;
import main.content.enums.entity.HeroEnums;
import main.content.enums.entity.HeroEnums.BACKGROUND;
import main.content.enums.entity.HeroEnums.RACE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.EntityCheckMaster;
import main.entity.type.ObjType;
import main.swing.generic.components.editors.lists.ListChooser;
import main.swing.generic.components.editors.lists.ListChooser.SELECTION_MODE;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

public class NameMaster {
    /*
    Feren Dauril
	Edrail Neref Raurim 
	
	
	Sileeve
	Eerive
	Yvrieec
	Uelveec
	Visphy
	Loie'Tar
	Tor'Loiel
	Ylm'Theron
	Lith'Ygol
	Orleeve
	Enreil Delian 
	Melin Aduvir 
	Venroim Tharguin 
	Gideon Antar
	Abaddon Gleth 
	David Mendel 
	Genvil Dalion 
	Ervilon Xolmir 
	Domil Anoch 
	Izrin Minchev 
	
	// Eledari
	// Sel'Mauri
	// Ald'Vemdor
	// Irf'Aenor
	// Erd'Mingul
	// Saer'Vaenfel
	// Huir'Lagmoth
	// Raeph'Haduin
	// Thae'Vaol
	
	
	
	 */

    public static final String NO_NAME = "No name";
    public static final String VERSION = " v";
    public static final String ravenNames = "Draco Dorian Jack Pike Quentin Rick Dilrin Denrix Melrix Lidvis Emrin Lervin Carpel Malkin Severard Rinfer Garsiv Virxis Fidvin Mirvin Delvar "
            + "Delven Idlem Vuron Pirkust Esvin Kuigin Nirgus Islog Sarnas Genlyn Avaldur Vatiar Ectis Osmer Pealet Lotard Nertum Kesrin";
    public static final String ravenNamesFemale = "Jenisse Sheilin Imlia Lilian Mesbeth Viola Biantha Gioress Sabina Enesse Norette Vosane Fiona";
    public static final String ravenNamesSecond = "Dilrin Norux Denrix Melrix Girsis Lidvis Emrin Lervin Carpel Malkin Rinfer Garsiv Virxis Fidvin Mirvin Kaurim Delnur Delvar Delnesse"
            + " Kurifar Nirligus Exerion Lethmer Raegsul Erming Loerg Gidlau Volmevin Molfran Korbulo";
    public static final String wolfNames = "Dortan Gauren Girmut Azulf Sivlid Vardel Dersen Alvid Galvir Gimnur Thonril Tholmir Lirmog Gleifnir Wedrhod Gwynreth Rhaglef Cestil Genlun Golrar "
            + "Gargi Vartu Valsteg Higstam Conrik "
            + "Fogmed Tilbruk Brukthel Ogsit Huntok Arfost Hadden Utmer Isfior Ginwad Tair Sefiad Skalg";
    public static final String wolfNamesFemale = "Irsida Emrida Ithglen Fidra Lagatha Aituli Gelnifer Igrith Gwynedil Imdaleg Storbia Rhagla";
    public static final String wolfNamesSecond = "Valherim Dirhelor Sivlid Visrith Pauren Edlen Igles Moltav Delgrav Dersen Gimnur Mirthud Thonril Tholmir Glerson Dimroth Dortan Gauren Girmut Dirhold Therving Kinrikkl Hamhelik Segervid Tilsinod Vilkannet";
    public static final String eagleNames = "Erithec Benihath Elmareth Delfenor Martis Augled Raurim Mauglir Ulraim Geldior Imgisar Inrif Farnil Iffdor Imliad Anbinath Irsenon Kolneru Elsier "
            + "Nethlur Orodrim Irddel Celthin Aemris Urfediv Irlath Laruon Kineil Iseard";
    public static final String eagleNamesFemale = "Elsira Asmin Ervina Tulani Aruna Eriana Silda Olvira Nithlara Istra Eilida Niumi Elbira Elnifer Ulrina Silvia ";
    public static final String eagleNamesSecond = "Erithec Benihath Elmareth Delfenor Raurim Caerphis Chimrefel Mauglir Lefredol Ulraim Geldior Imgisar Anurphis Menloris Medlorim Eraglen Erniath Athrem";
    public static final String kingNames = "John Adam Alnoir Erles Gwahir Bendaf Galdion Gorthiod Rolwain Limrod Sidlur Aupheir Nathlim Demeril Genthel Aldain Brestir Irgvil "
            + "Giflas Ester Menres Eumol Rolwim Adelwyn Rimlod Arnelion Lorwim Wolrim"
            + " Cenhed Adeilar Denuid Gwynros ";
    public static final String kingNamesFemale = "Menfa Juthira "
            + "Elari Thinea Eonri Blauri Athna Lydia" + "Aithlin Mirona Nimore Seina Feluna Feina "
            + "Ansila Thide Haitha Lia Amaltha Theola";
    public static final String kingNamesSecond = "Bendaf Gorthiod Rolwain Limrod Irsilod Kalroin Ultath Kinter Elsen Drolem Galniod Irtheniod Adlarim Ginlung Arthenon Vaktiar Pirstag Aedainur Emrianor Naellid Esteniad Althair Murwaith Giothrim Maelsiur Firdlain Estariath Lundaine Merphim Barthair Aensulat Phermithas Leinleith Mardalu Gidseol Soamdath Diplard Evengil Fobbled Kaergraf Delnian Nelaigo";
    public static final String griffNames = "Jowaine Jurgon Severon Rutheos Raomir Raemir Sinarc Jemid Juraine Onsard Godric Theon Crigol Dagros Simdal "
            + "Gaeglin Gedaeth Belved Nevid Gasglid Ibrin Ordeb Baleg Hadrod "
            + "Haddel Kurdeag Deirid Daereir Thainith Thaemil Tilead Aldvan Cearat Daecar Adimach Angfuil "
            + "Sestian Rodviol Feilod Gaerad";
    public static final String griffNamesFemale = "Jenfis Remira Sinara Jemira Arfida Sornila Sanura Amrin Olmra Eola";
    public static final String griffNamesSecond = "Edexis Bailen Imsidor Galdion Feincer Rebfil Atephari Jowaine Godleon Rutheos Raomir Ramrid Raemird Sinarc Gotua Arverno Nolvio Rhufeon Juraine ";
    public static final String ulduinNames = "Limrog Olavus Grinresar Smidlar Burtug Fargrad "
            + "Grildur Ormund Stryvolur Riddrog "
            + "Trondurin Tormethil Scadmur Miseud Otsiom Relgon Uthlediad Algorn "
            + "Strolar Urtfran Adeladur Anselud";
    public static final String ulduinNamesFemale = "Misanna Relga Uthlena Selda Somlesta Valdelen ";
    public static final String ulduinNamesSecond = "";
    public static final String elvenNames = "Avelar Haevril Sethelon Eniloth Goriad Nirolim Valrith Entelion Raelur Egvilas Eithas Anelur Adrathas Ginleth Diular Ulsair Doluith Daenar Faoir Inriuth Sauir Faoim Maedlon Naoin Nilthuin Nimlod Gondulim Deriath Vendradir Belvenon Surmagil "
            + "EithNaglur MaerhValar Quedlin Sianor Delgalad Orthaelion Teregroth "
            + "Dulvin BaelarAnuir Olvah Mithlur Gilthar Maedroth Elidor Galdir Echfaelion Dorlumin "
            + "Daurim LirthDelion Bellah Salfeon Ingean Milvor Enudeg Daenlehir Atelaeth";
    public static final String elvenNamesFemale = "Belia Nelia Anfina Insila Mivlia Iola Aylin Siora Quelthia Quesia Lumina Dirna Tairi Luwien Adulai Ilureth ";
    public static final String elvenNamesSecond = "Ilarfis Arveliad Haevril Sedvelon Caelnorith Eniloth AtalunRee Tulanir Goriad Nimrovil Valrith Uthuniel BlithSaeg Faoi";
    public static final String dwarvenNames = "Drogon Rhughad Ligrod Gefnod Nogvir Naugrith Thodrim Balrim Droln Drofnod Noru Bosnir Dusmar Golgar Uxi Dunbor Mugrol "
            + "Talsen Donkel Ingins Desger Tugindir "
            + "Ustuk Hambrud Sageli Eprug Norkol Ungron Ysgrithur Ynvigr Gachtarg Drolif Tagair "
            + "Skeltar Braeham Mangif Maersenif Kenthad Deilar Lerthaein Ulmith Utvald Ogvart Aproret Jargsil "
            + "Utanar Ulkmar Baglarod Haglir Tilfram Birenor Tharagur";
    public static final String dwarvenNamesFemale = "";
    public static final String dwarvenNamesSecond = "Tirledag Semerog Rhughad Brifich Faurhir Ligrod Gefnod Nogvir Naugrith Thodrim Gausod Grenchest Bansiad Forgneth Acurag Olpust Lurraldi Baketar Batasur Viragost Vuthlin Trovarin Kogel";
    public static final String easterlingNames = "";
    public static final String easterlingNamesFemale = "";
    public static final String easterlingNamesSecond = "";
    public static final String demonNames = "Irxithor ";
    public static final String demonNamesFemale = "";
    public static final String pirateNames = "Forent Saeros Neilag Harlen ";
    public static final String pirateNamesFemale = "";
    public static final String knightNames = "Lanvil Uriel Marcus Flavius Anthony Rubius Victor David Davon Marius Mandos";
    public static final String knightNamesSecond = "Bold Brave Strong Swift Valiant Galant Pious Righteous Good Stout Feared Dire Red White Blessed Mighty";
    /*
     Anaig

       De'Lorkai
    Giellad Mukami Lohkan Malead Ailad Obea'Wee
     */
    public static final String NAME_PREFIX_ELVEN = "";
    public static final String NAME_PREFIX_NORSE = "";
    public static final String NAME_PREFIX_DARK = "";
    public static final String NAME_PREFIX_HUMAN = "";
    public static final String NAMES_WOLF_MALE = "Vulthren;Horstwynn;";
    public static final String SECOND_NAMES_WOLF_MALE = "Vezmorg;Arverus;Horgron;Dergven;Helvindr;Molandir;Dolgmor";
    public static final String NAMES_RAVEN_MALE = "Azmar;Dalreth;Argmann;Ordvil;Movander;DOrlain;"
            + "Morkain;Irvynn;Godric;Ismar;Heldmar";
    public static final String SECOND_NAMES_RAVEN_MALE = "Coulon;Calvenir;Carthenn;Woodlard;Oltouron;Pfadeau;Harkis;Chermagne;Lerkou;Verdogne;Taulaise";
    public static final String NAMES_EAGLE_MALE = "Ixis;Ezuviel;Thydrod;Quorix;Vezmut;Ezdred;Dalvion;Arodris;Xalvir;Astera;Lumina;Kalthed;Vengor;Mavilor;AedAgor;Tarlos;Kerebor;Ponfinor;Tolmaril;Sixis;DorthArex;Zandimar;Ulviden;Aerthoniel;Arcturus;";
    public static final String NAMES_DWARF_MALE = "Mugradon;Duvagor;Odrin;Udrim;Padgor;Twergmar;Gramadan;";
    public static final String SECOND_NAMES_DWARF_MALE =

            "Uffly;Kuelgh;Oldrich;Boulegon;Barmegad;Kilverin;Sigremon;Throrrus";
    static String ravenNameGroups = "Raven Pirate Wolf Eagle King Common Easterling";
    static String wolfNameGroups = "wolf Ulduin Raven King Common Eagle";
    static String eagleNameGroups = "Eagle King Ulduin Raven Wolf Griff Common";
    static String griffNameGroups = "griff King Common Eagle Easterling";
    static String kingNameGroups = "king Common Eagle Griff Ulduin Wolf Pirate";
    private static final String DEFAULT_NAME_GROUP = kingNameGroups;
    static String genericNameGroups = "Common Griff Pirate king Eagle Wolf";
    private static List<String> usedNames = new ArrayList<>();
    private static String pirateNamesSecond;
    private static String demonNamesSecond;
    String orcNames = "Ormog;Nuglog;Usbrol;Buvlud;Xlirg;Xlard;Xmun;Yach-Yach;Guchruk;Durbul;Mubruk;Zurdun;Drufsin;";

    public static String generateNewHeroName(ObjType type) {
        BACKGROUND bg = getBg(type);
        return getFullNameForBackground(bg);
    }

    public static String getFullNameForBackground(BACKGROUND bg) {
        String name = getRandomName(bg) + " " + getRandomSecondName(bg);
        return name;
    }

    private static String getRandomSecondName(BACKGROUND bg) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String getRandomName(BACKGROUND bg) {
        switch (bg) {

        }
        return null;
    }

    private static String generateName(Entity hero, String backgroundNameGroups) {
        return generateName(hero, backgroundNameGroups, true);
    }

    private static String generateName(Entity hero, String backgroundNameGroups,
                                       boolean secondIncluded) {
        boolean female = EntityCheckMaster.getGender(hero) == HeroEnums.GENDER.FEMALE;
        String name = null;

        for (String nameGroup : StringMaster.open(backgroundNameGroups, " ")) {
            while (true) {
                if (name != null) {
                    break;
                }
                name = getRandomHeroName(hero, getNamesForGroup(nameGroup, female));

            }
            while (true) {
                String secondName = getRandomHeroName(hero, getNamesForGroup(nameGroup, null));

                if (secondName != null) {
                    if (!secondName.equals(name)) {
                        return name + " " + secondName;
                    }
                }
            }
        }
        return "no name";
    }

    public static String getNamesForGroup(String nameGroup, Boolean female_male_second) {

        if (nameGroup.toString().equalsIgnoreCase("Raven")) {
            if (female_male_second == null) {
                return ravenNamesSecond;
            } else {
                return !female_male_second ? ravenNames : ravenNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Griff")) {
            if (female_male_second == null) {
                return griffNamesSecond;
            } else {
                return !female_male_second ? griffNames : griffNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Eagle")) {
            if (female_male_second == null) {
                return eagleNamesSecond;
            } else {
                return !female_male_second ? eagleNames : eagleNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Easterling")) {
            if (female_male_second == null) {
                return easterlingNamesSecond;
            } else {
                return !female_male_second ? easterlingNames : easterlingNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("King")) {
            if (female_male_second == null) {
                return kingNamesSecond;
            } else {
                return !female_male_second ? kingNames : kingNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Wolf")) {
            if (female_male_second == null) {
                return wolfNamesSecond;
            } else {
                return !female_male_second ? wolfNames : wolfNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Dwarven")) {
            if (female_male_second == null) {
                return dwarvenNamesSecond;
            } else {
                return !female_male_second ? dwarvenNames : dwarvenNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Elven")) {
            if (female_male_second == null) {
                return elvenNamesSecond;
            } else {
                return !female_male_second ? elvenNames : elvenNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Ulduin")) {
            if (female_male_second == null) {
                return ulduinNamesSecond;
            } else {
                return !female_male_second ? ulduinNames : ulduinNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Pirate")) {
            if (female_male_second == null) {
                return pirateNamesSecond;
            } else {
                return !female_male_second ? pirateNames : pirateNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Knight")) {
            if (female_male_second == null) {
                return knightNamesSecond;
            } else {
                return !female_male_second ? knightNames : kingNamesFemale;
            }
        }
        if (nameGroup.toString().equalsIgnoreCase("Demon")) {
            if (female_male_second == null) {
                return demonNamesSecond;
            } else {
                return !female_male_second ? demonNames : demonNamesFemale;
            }
        }
        return nameGroup;
    }

    public static String getNamesGroups(BACKGROUND bg) {
        if (bg.toString().contains("Raven")) {
            return (ravenNameGroups);
        }
        if (bg.toString().contains("Griff")) {
            return (griffNameGroups);
        }
        if (bg.toString().contains("Eagle")) {
            return (eagleNameGroups);
        }
        // if (bg.toString().contains("Easterling"))
        // return ( easterlingNameGroups);
        if (bg.toString().contains("King")) {
            return (kingNameGroups);
        }
        if (bg.toString().contains("Wolf")) {
            return (wolfNameGroups);
        }
        if (bg.toString().contains("Ulduin")) {
            return (ravenNameGroups);
        }
        return (ravenNameGroups);
    }

    public static String generateName(Entity hero) {
        RACE race = EntityCheckMaster.getRace(hero);
        BACKGROUND bg = EntityCheckMaster.getBackground(hero);
        switch (race) {
            case HUMAN:
                if (bg.toString().contains("Raven")) {
                    return generateName(hero, ravenNameGroups);
                }
                if (bg.toString().contains("Griff")) {
                    return generateName(hero, griffNameGroups);
                }
                if (bg.toString().contains("Eagle")) {
                    return generateName(hero, eagleNameGroups);
                }
                // if (bg.toString().contains("Easterling"))
                // return generateName(hero, easterlingNameGroups);
                if (bg.toString().contains("King")) {
                    return generateName(hero, kingNameGroups);
                }
                if (bg.toString().contains("Wolf")) {
                    return generateName(hero, wolfNameGroups);
                }
                if (bg.toString().contains("Ulduin")) {
                    return generateName(hero, ravenNameGroups);
                }

                return generateName(hero, DEFAULT_NAME_GROUP);
            // TODO
            case DEMON:
                return generateName(hero, "Demon");
            case DWARF:
                return generateName(hero, "Dwarven");
            case ELF:
                return generateName(hero, "Elven");
            case GOBLINOID:
                return generateName(hero, "Dwarven");
            case VAMPIRE:
                return generateName(hero, ravenNameGroups);
        }
        return null;
    }

    private static String getRandomHeroName(Entity hero, String namePool) {
        List<String> pool = StringMaster.openContainer(namePool);
        if (pool.isEmpty()) {
            return NO_NAME;
        }
        List<String> names = new ArrayList<>();
        for (String s : pool) {
            for (String newname : StringMaster.open(s, " ")) {
                if (!newname.isEmpty()) {
                    if (!usedNames.contains(newname)) {
                        names.add(newname);
                    }
                }
            }
        }
        if (names.isEmpty()) {
            usedNames.clear();
            return getRandomHeroName(hero, namePool);
        }

        String name = names.get(RandomWizard.getRandomListIndex(names));

        usedNames.add(name);
        return name;
    }

    public static String pickName(Entity hero) {
        List<String> nameGroups = StringMaster.openContainer(getNamesGroups(getBg(hero)), " ");
        List<String> list = new ArrayList<>();
        for (String nameGroup : nameGroups) {
            list.addAll(StringMaster.openContainer(getNamesForGroup(nameGroup, EntityCheckMaster
                    .getGender(hero) == HeroEnums.GENDER.FEMALE), " "));
        }
        String name = new ListChooser(SELECTION_MODE.MULTIPLE, list, false).choose();
        if (name != null) {
            return StringMaster.joinStringList(StringMaster.openContainer(name), " ", true);
        }
        return null;
    }

    public static void clearNames() {
        usedNames.clear();
    }

    private static BACKGROUND getBg(Entity hero) {
        return new EnumMaster<BACKGROUND>().retrieveEnumConst(BACKGROUND.class, hero
                .getProperty(G_PROPS.BACKGROUND));
    }

    public static String appendVersionToName(String name) {
        return appendVersionToName(name, null );
    }
        public static String appendVersionToName(String name, Integer i) {
        if (name.contains(VERSION)) {
            if (StringMaster.isInteger("" + name.charAt(name.length() - 1))) {
                name = name.substring(0, name.lastIndexOf(" "));
            }
        }
        Loop.startLoop(1000);
            if (i == null) {
                i = 2;
        while (!Loop.loopEnded()) {
            String newName = name + VERSION + i;
            if (!DataManager.isTypeName(newName)) {
                break;
            }
            i++;
        }
            }
        return name + VERSION + i;
    }

    public static String getUniqueVersionedName(String name, OBJ_TYPE T) {
        return getUniqueVersionedName(DataManager.getTypes(T), name);
    }

    public static String getUniqueVersionedName(List<ObjType> types, String name) {
        int i = 0;
        String newName;
        loop:
        while (true) {
            i++;
            newName = name + StringMaster.VERSION_SEPARATOR + i;
            for (ObjType t : types) {
                if (t.getName().equals(newName)) {
                    continue loop;
                }
            }
            break;
        }
        return newName;
    }

    public enum HERO_NAME_GROUPS {
        ELVEN, NORSE, DARK, HUMAN
    }

}
