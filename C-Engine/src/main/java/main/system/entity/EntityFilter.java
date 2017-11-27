package main.system.entity;

import main.content.OBJ_TYPE;
import main.entity.Entity;

import java.util.Collection;
import java.util.ArrayList;

public class EntityFilter<E extends Entity> {
    Collection<E> list;
    Collection<E> filteredList;
    String valueName;
    String value;
    OBJ_TYPE TYPE;
    boolean prop;
    boolean filterOut;
    Boolean strict_or_greater_less_equal;

    public EntityFilter() {

    }

    public EntityFilter(Collection<E> list, String valueName, String value, OBJ_TYPE TYPE,
                        boolean prop) {
        this(list, valueName, value, TYPE, prop, false, null);
    }

    public EntityFilter(Collection<E> list, String valueName, String value, OBJ_TYPE TYPE,
                        boolean prop, boolean filterOut, Boolean strict_or_greater_less_equal) {
        this.list = list;
        this.TYPE = TYPE;
        this.value = value;
        this.valueName = valueName;
        this.prop = prop;
        this.filterOut = filterOut;
        this.strict_or_greater_less_equal = strict_or_greater_less_equal;
    }

    public Collection<E> filter() {
        filteredList = (Collection<E>) FilterMaster.filter(new ArrayList<>(list), valueName,
                value, TYPE, prop, filterOut, strict_or_greater_less_equal);
        return filteredList;
    }

    public Collection<E> getList() {
        return list;
    }

    public void setList(Collection<E> list) {
        this.list = list;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OBJ_TYPE getTYPE() {
        return TYPE;
    }

    public void setTYPE(OBJ_TYPE tYPE) {
        TYPE = tYPE;
    }

    public boolean isProp() {
        return prop;
    }

    public void setProp(boolean prop) {
        this.prop = prop;
    }

    public boolean isFilterOut() {
        return filterOut;
    }

    public void setFilterOut(boolean filterOut) {
        this.filterOut = filterOut;
    }

    public Boolean getStrict_or_greater_less_equal() {
        return strict_or_greater_less_equal;
    }

    public void setStrict_or_greater_less_equal(Boolean strict_or_greater_less_equal) {
        this.strict_or_greater_less_equal = strict_or_greater_less_equal;
    }

    public Collection<E> getFilteredList() {
        return filteredList;
    }

}
