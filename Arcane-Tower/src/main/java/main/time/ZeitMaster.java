package main.time;

import main.ArcaneTower;
import main.content.OBJ_TYPE;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.entity.Entity;
import main.entity.type.ObjType;
import main.logic.AT_OBJ_TYPE;
import main.logic.AT_PARAMS;
import main.logic.AT_PROPS;
import main.logic.ArcaneEntity;
import main.swing.generic.services.dialog.DialogMaster;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.TimeMaster;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ZeitMaster {

    public static final String MARK_SEPARATOR = ": ";

    public static int getTotalSecondsForEntity(ArcaneEntity e, AT_PARAMS timeParam) {
        long time = TimeMaster.getTime() - new Long(e.getParam(timeParam));
        int seconds = (int) (time / 1000);
        return seconds;
    }

    public static String getTimeStamp() {
        return TimeMaster.getFormattedTime(false, true);
    }

    public static boolean isToday(int time) {
        return TimeMaster.isToday(time);
    }

    public static void checkCreateTimeTypes() {
        // getOrCreate last day, week, month...
        Entity era = getLatest(DataManager.getTypes(AT_OBJ_TYPE.ERA));
        boolean create = era == null;
        if (!create) {
            create = era.checkProperty(AT_PROPS.ERA_STATUS, "Concluded");
        }
        if (create) {
            int n = DataManager.getTypes(AT_OBJ_TYPE.ERA).size() + 1;
            String name = DialogMaster.inputText("New Era's Name?", "The " + n
                    + StringMaster.getOrdinalEnding(n) + " Era");
            era = new ObjType(name, AT_OBJ_TYPE.ERA);
            DataManager.addType((ObjType) era);
        }
        createPeriod(era, AT_OBJ_TYPE.DAY);
        createPeriod(era, AT_OBJ_TYPE.WEEK);
        createPeriod(era, AT_OBJ_TYPE.MONTH);

        ArcaneTower.saveAll();
        // for (t t : DataManager.getTypes(AT_OBJ_TYPE.DAY)){
        // }

    }

	/*
     * markTime()
	 * paused()
	 * getTotalTime()
	 */

    private static void createPeriod(Entity era, AT_OBJ_TYPE T) {
        boolean create;
        List<? extends Entity> list = DataManager.getTypes(T);
        Entity last = getLatest(list);
        create = last == null;
        if (!create) {
            create = checkCreateNeeded(last, T);
        }
        if (create) {
            ObjType type = new ObjType(getNameForPeriod(era, T), T);
            initTimeOfCreation(type);
            DataManager.addType(type);
        }
    }

    public static void initTimeOfCreation(Entity type) {
        long time = TimeMaster.getTime();
        type.setParam(AT_PARAMS.TIME_CREATED, time + "");
        type.setParam(AT_PARAMS.TIME_LAST_MODIFIED, time + "");
    }

    private static boolean checkCreateNeeded(Entity last, AT_OBJ_TYPE T) {
        switch (T) {
            case DAY:
                return !isToday(last.getIntParam(AT_PARAMS.TIME_CREATED));
            case WEEK:
                return !TimeMaster.isThisWeek(last.getIntParam(AT_PARAMS.TIME_CREATED));
            case MONTH:
                return !TimeMaster.isThisMonth(last.getIntParam(AT_PARAMS.TIME_CREATED));
        }
        return false;
    }

    public static int getTotalTimeFromTimeMarks(String string, String key) {
        // STATE_TIMEMARKS
        boolean open = false;
        int time = 0;
        long mark = 0;
        for (String substring : StringMaster.openContainer(string)) {
            if (!substring.contains(key)) {
                if (open) {
                    time += getTimeFromMark(substring) - mark;
                    open = !open;
                }
            }
            if (substring.contains(key)) {
                mark = getTimeFromMark(substring);
                open = true;
            }

        }

        return time;
    }

    public static String getTimeStringDays(long time) {
        return TimeMaster.getDays(time) + " ago";
    }

    public static long getTimeFromMark(String substring) {
        return Long.parseLong(substring.substring(substring.indexOf(MARK_SEPARATOR) + 2));
    }

    public static String getNameForPeriod(AT_OBJ_TYPE T) {
        Entity era = getLatest(AT_OBJ_TYPE.ERA, AT_PARAMS.TIME_LAST_MODIFIED);
        return getNameForPeriod(era, T);
    }

    public static String getNameForPeriod(Entity era, AT_OBJ_TYPE T) {
        switch (T) {
            case DAY:
                return "The " + TimeMaster.getDayText() + ", " + era;
            case WEEK:
                return TimeMaster.getWeekString() + ", " + era;
            case MONTH:
                return TimeMaster.getMonthName() + ", " + era;
        }
        return "";
    }

    public static Entity getLatest(AT_OBJ_TYPE session, boolean latestModified) {
        if (!latestModified) {
            return getLatest(session, null);
        }
        return getLatest(session, AT_PARAMS.TIME_LAST_MODIFIED);
    }

    public static ObjType getLatest(OBJ_TYPE TYPE, PARAMETER p) {
        return (ObjType) getLatest(DataManager.getTypes(TYPE), p);
    }

    public static Entity getLatest(List<? extends Entity> list) {
        return getLatest(list, null);
    }

    public static Entity getLatest(List<? extends Entity> list, final PARAMETER customParam) {
        list = new LinkedList<>(list);
        Collections.sort(list, new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                PARAMETER param = AT_PARAMS.TIME_CREATED;
                if (customParam != null) {
                    if (o1.checkParam(customParam) || o2.checkParam(customParam)) {
                        param = customParam;
                    }
                }

                return SortMaster.compare(o1, o2, param);
                // return SortMaster.compare(o1, o2,
                // AT_PARAMS.TIME_LAST_MODIFIED);
            }
        });
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static void finished(ArcaneEntity entity) {
        Integer current = getTime();
        Integer passed = (current - entity.getIntParam(AT_PARAMS.TIME_STARTED));
        Integer total_paused = entity.getIntParam(AT_PARAMS.TIME_TOTAL_PAUSED);
        Integer time = (passed - total_paused);
        entity.setParameter(AT_PARAMS.TIME_SPENT, time);
        entity.setParameter(AT_PARAMS.TIME_FINISHED, current);
        entity.addProperty(AT_PROPS.TIME_MARKS, STATUS_MARK.FINISHED + ": " + time);
        // TODO count task/goal/session/... getParent()
        // getDay() ...

        // AT_OBJ_TYPE TYPE = (AT_OBJ_TYPE) entity.getOBJ_TYPE_ENUM();
        // ArcaneEntity obj = entity;
        // PARAMETER param = TYPE.getCountParam();
        // while (true) {
        // obj = obj.getParent();
        // if (obj == null)
        // break;
        // if (ContentManager.isValueForOBJ_TYPE(obj.getOBJ_TYPE_ENUM(), param))
        // obj.incrementParam(param);
        // }
        ArcaneTower.checkAutosave(entity);

    }

    public static void started(ArcaneEntity entity) {
        long time = TimeMaster.getTime();
        entity.setParam(AT_PARAMS.TIME_STARTED, "" + time);
        entity.setParam(AT_PARAMS.TIME_LAST_MODIFIED, "" + time);
        entity.addProperty(AT_PROPS.TIME_MARKS, STATUS_MARK.STARTED + ": " + time);
    }

    public static void paused(ArcaneEntity entity) {
        Integer current = getTime();
        entity.setParameter(AT_PARAMS.TIME_PAUSED, current);
        entity.setParam(AT_PARAMS.TIME_LAST_MODIFIED, "" + current);
        entity.addProperty(AT_PROPS.TIME_MARKS, STATUS_MARK.PAUSED + MARK_SEPARATOR + current);

    }

    public static Integer getTime() {
        return (int) TimeMaster.getTime();
    }

    public static void resumed(ArcaneEntity entity) {

        Integer current = getTime();
        Integer time = (current - entity.getIntParam(AT_PARAMS.TIME_PAUSED));
        entity.modifyParameter(AT_PARAMS.TIME_TOTAL_PAUSED, time);
        entity.setParameter(AT_PARAMS.TIME_RESUMED, current);
        entity.setParam(AT_PARAMS.TIME_LAST_MODIFIED, "" + current);
        entity.addProperty(AT_PROPS.TIME_MARKS, STATUS_MARK.STARTED + ": " + time);

    }

    public enum STATUS_MARK {
        STARTED, PAUSED, FINISHED,
    }

}
