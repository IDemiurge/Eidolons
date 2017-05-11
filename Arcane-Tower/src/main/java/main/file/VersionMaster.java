package main.file;

import main.ArcaneTower;
import main.content.OBJ_TYPE;
import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.data.XLinkedMap;
import main.data.xml.XML_Reader;
import main.data.xml.XML_Writer;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PROPS;
import main.logic.ArcaneEntity;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;

import java.util.List;
import java.util.Map;

public class VersionMaster {

    private static final String periodSeparator = "-";
    private static final String MILESTONE = "BC Alpha";
    private static final String VERSIONS = "Versions";
    public static int version_session;
    static Map<String, Map<String, ObjType>> map;
    static String version_day;
    static String version_week;
    static String version_month;

    static {
        version_session = 1;
        version_day = "Day " + TimeMaster.getDay();
        version_week = "Week " + ((TimeMaster.getDay() / 7) + 1);
        version_month = TimeMaster.getMonthName();
    }

    public static String compareVersions(ArcaneEntity e, VALUE v, VERSION_PERIOD period) {
        ObjType type = getPreviousVersion(e, period);
        type.getValue(v);
        if (v instanceof PARAMETER) {
            PARAMETER parameter = (PARAMETER) v;

        }
        return null;
    }

    private static String getAlteredVersionStamp(String stamp, VERSION_PERIOD period, int diff) {
        // string to stamp, stamp to string
        Version version = new Version(stamp);
        String[] array = stamp.split(periodSeparator);
        for (String s : array) {
            if (!s.contains(StringMaster.getWellFormattedString(period.name()))) {
                continue;
            }
            Integer v = StringMaster.getInteger(StringMaster.getSegment(1, s.trim(), " ")) + diff;
            v = getPeriodInteger(v, period, stamp);
            // oct 1 -> sept 31
            while (v < 0) {
                // getOrCreate one level down
                Integer month = version.map.get(VERSION_PERIOD.MONTH);
                Integer daysInMonth = TimeMaster.getDaysInMonth(month);
                switch (period) {
                    case DAY:
                        v = daysInMonth + v;
                        MapMaster.addToIntegerMap(version.map, VERSION_PERIOD.MONTH, -1);
                        break;
                    case WEEK:
                        break; // recalculate based on days?
                    // reduce month also...
                    // int remainingDays;

                    case MONTH:
                        // year?
                        // MILESTONES
                        break;
                    case MILESTONE:
                        break;
                    case SESSION:
                        break;// crawl the version folders?...
                    default:
                        break;

                }

            }
            // support period type alteration via -1 return?

            // TODO what if it goes below 0?!
            int n = 0;
            String period_segment = StringMaster.getSegment(n, s, " ");
            stamp.replace(s, period_segment + " " + v);
        }
        return null;
    }

    private static Integer getPeriodInteger(Integer v, VERSION_PERIOD period, String version) {
        switch (period) {
            case DAY:
                if (v < 28) {
                    return v;
                }
//				if (getMonth(version).getDays() < v)
//					return Integer.MIN_VALUE;

            case MILESTONE:
                break;
            case MONTH:
                break;
            case SESSION:
                break;
            case WEEK:
                break;
            default:
                break;

        }
        return null;
    }

    public static String compareVersions(VERSION_PERIOD period) {
        // getDate();
        // session number? Folder type name format:
        // Milestone-weekN-dayN-sessionN
        switch (period) {
        }
        return "";
    }

    public static Map<VALUE, String> getVersionDifferenceMap(VERSION_PERIOD period, int n,
                                                             ArcaneEntity type) {
        Map<VALUE, String> map = new XLinkedMap<>();
        ObjType oldType = getOlderVersion(type, period, n);
        for (PARAMETER p : oldType.getParamMap().keySet()) {
            int diff = type.getIntParam(p) - oldType.getIntParam(p);
            map.put(p, diff + "");
        }
        // for (PROPERTY portrait : oldType.getPropMap().keySet()) {
        // String diff = StringMaster.getChanges( type .getProperty(portrait) ,
        // oldType.getProperty(portrait));
        // map.put(portrait, diff );
        // }
        // oldType.compareValues(type);

        return map;
    }

    // what find() funcs do I really need?
    /*
     * x days before (discard session n)
	 * x weeks - what if not all days are present? Create anyway, if only empty 
	 * x months - uneven 
	 * x sessions - crawl versions to go beyond 1 day difference?
	 * 
	 *  compare 'progress' somehow? 
	 *  meta values 
	 *  
	 *  
	 */

    public static String getVersion() {
        // TODO era?
        return MILESTONE + periodSeparator + version_month + periodSeparator + version_week
                + periodSeparator + version_day + periodSeparator + " Session " + version_session;
    }

    private static String getVersionFolderPath(OBJ_TYPE t, String version) {
        return getPath() + version + "\\" + t.getName() + ".xml";
    }

    private static String getPath() {
        return ArcaneTower.getTypesPath() + VERSIONS + "\\";
    }

    private static String getVersion(Entity e) {
        return e.getProperty(AT_PROPS.AT_VERSION, true);
    }

    public static void dayEnds() {
        // version_day
        saveVersionFolder();
    }

    public static void saveVersionFolder() {
        saveVersionFolder(false);
    }

    public static void saveVersionFolder(boolean dirtyOnly) {
        for (AT_OBJ_TYPE T : AT_OBJ_TYPE.values()) {
            if (!T.isVersioned()) {
                continue;
            }
            XML_Writer.setCustomPath(getPath() + getVersion());
            XML_Writer.setDirtyOnly(dirtyOnly);
            try {
                XML_Writer.writeXML_ForTypeGroup(T);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                XML_Writer.setCustomPath(null);
                XML_Writer.setDirtyOnly(false);
            }
        }

    }

    public static void saveVersion(ArcaneEntity entity) {
        XML_Writer.setCustomPath(getPath() + getVersion());
        try {
            XML_Writer.writeXML_ForType(entity.getType());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            XML_Writer.setCustomPath(null);
        }

    }

    public static void setVersionToCurrent(ObjType type) {
        type.setProperty(AT_PROPS.AT_VERSION, getVersion());

    }

    private static ObjType getPreviousVersion(ArcaneEntity e, VERSION_PERIOD period) {
        return getOlderVersion(e, period, 1);
    }

    private static ObjType getOlderVersion(ArcaneEntity e, VERSION_PERIOD period, int n) {
        // previous meaning when, exactly?
        if (isTestMode()) {
            setVersionToCurrent(e.getType());
        }
        String version = getVersion(e);
        String prevVersion = getAlteredVersionStamp(version, period, -n);
        if (!map.containsKey(prevVersion)) {
            readVersionFile(e.getOBJ_TYPE_ENUM(), prevVersion);
        }
        Map<String, ObjType> idMap = map.get(prevVersion);

        ObjType type = idMap.get(e.getUniqueId());
        return type;
    }

    private static boolean isTestMode() {
        return true;
    }

    private static void readVersionFile(OBJ_TYPE t, String version) {
        String xml = FileManager.readFile(getVersionFolderPath(t, version));
        List<ObjType> types = XML_Reader.createCustomTypeList(xml, t, ArcaneTower.getSimulation(),
                false, false, false);

        Map<String, ObjType> idMap = new XLinkedMap<>();
        for (ObjType type : types) {
            String id = type.getUniqueId();
            idMap.put(id, type);
        }

        map.put(version, idMap);
    }

    public enum VERSION_PERIOD {
        MILESTONE, MONTH, WEEK, DAY, SESSION,
    }

    public enum MILESTONES {
        BC_ALPHA, BC_BETA, BATTLECRAFT, DC_ALPHA, DC_BETA, DUNGEONCRAFT, PROTO_MACRO,

    }

    public static class Version {
        public Map<VERSION_PERIOD, Integer> map;

        public Version(String version) {
            // return MILESTONE + periodSeparator + version_month +
            // periodSeparator + version_week
            // + periodSeparator + version_day + periodSeparator + " Session " +
            // version_session;
            int i = 0;
            for (String sub : version.split(periodSeparator)) {
                if (i != 0) {
                    map.put(VERSION_PERIOD.values()[i], StringMaster.getInteger(sub));
                }
                i++;
            }
        }
        // int DAY;
        // String MILESTONE;
        // int MONTH;
        // int SESSION;
        // int WEEK;
    }

}
