package eidolons.game.netherflame.dungeons.model.assembly;

import eidolons.game.netherflame.dungeons.model.QD_Module;
import main.system.auxiliary.EnumMaster;
import main.system.data.DataUnit;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import static eidolons.game.netherflame.dungeons.QD_Enums.*;

public class QD_ReqsFetcher {
    private final QD_Picker picker;

    public QD_ReqsFetcher(QD_Picker picker) {
        this.picker = picker;
    }

    public <T> Set<String> getSet(Class<T> tClass, Predicate<T> predicate) {
        Set<String> set = new LinkedHashSet<>();
        for (T t : new EnumMaster<T>().getEnumList(tClass)) {
            if (predicate.test(t))
                set.add(t.toString());
        }
        return set;
    }

    public Set<String> getPermittedSizes() {
        return getSet(ModuleSize.class, t -> checkSize(t, picker.data, picker.i, picker.max, picker.attempt,
                picker.previous, picker.next));
    }

    public Set<String> getPermittedElevation() {
        return getSet(ElevationLevel.class, t -> checkElevation(t));
    }

    public Set<String> getPermittedEntrances() {
        return getSet(EntranceType.class, t -> checkEntrance(t));
    }


    public Set<String> getPermittedLocations() {
        return getSet(QD_LOCATION.class, t -> checkLocation(t));
    }

    public Set<String> getPermittedType() {
        return getSet(ModuleType.class, t -> checkType(t));
    }

    public Set<String> getPermittedTags() {
        return getSet(ModuleTags.class, t -> checkTag(t));
    }

    private boolean checkType(ModuleType value) {
        if (value== ModuleType.normal) {
            return true;
        }
        if (!QD_Checker.moduleType(picker.previous, value)) {
            return false;
        } if (!QD_Checker.moduleType(picker.next, value)) {
            return false;
        }
        return getValueType(value)>0;

    }

    private int getValueType(ModuleType value) {
        switch (value) {
            case boss:
                break;
            case explore:
                break;
            case normal:
                break;
            case hard:
                break;
        }
        return 0;
    }

    private boolean checkTag(ModuleTags value) {
        switch (value) {

        }
        return false;
    }

    private boolean checkSize(ModuleSize value, DataUnit<FloorProperty> data, int i, int max, int attempt, QD_Module previous, QD_Module next) {
        switch (value) {

        }
        return false;
    }

    private boolean checkElevation(ElevationLevel value) {
        switch (value) {

        }
        return false;
    }

    private boolean checkEntrance(EntranceType value) {
        switch (value) {

        }
        return false;
    }

    private boolean checkLocation(QD_LOCATION value) {
        switch (value) {

        }
        return false;
    }

}
