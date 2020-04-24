package main.content;

import java.io.Serializable;

/**
 * Top level interface for Entity Parameters and Properties. Requirements:
 * ArcaneVault integration: meta-info, classMap for Ability Constr. Easy
 * extension Rigorous Classifications Convenience Methods Enumeration utilities
 * - nomenclature lookup, class loading
 * <portrait>
 * Potential Issues: Namespace overshadowing Return type - int, string, or what?
 * <portrait>
 * <portrait>
 * VALUE must provide: structural info (group, tags...), metainfo, optionally -
 * list of values (Races)? a lot of parameters must be given to each VALUE type
 * what is the best way of maintaining and expanding the system?
 *
 * @author JustMe
 */
public interface VALUE extends Serializable { //TODO test serializable is needed
    String name();

    String getName();

    String getFullName();

    String getShortName();

    String getDescription();

    String getEntityType();

    default String getDisplayedName(){
        return getName();
    }

    String[] getEntityTypes();

    String getDefaultValue();

    boolean isLowPriority();

    void setLowPriority(boolean lowPriority);

    boolean isSuperLowPriority();

    void setSuperLowPriority(boolean lowPriority);

    boolean isHighPriority();

    void setHighPriority(boolean highPriority);

    boolean isDynamic();

    boolean isWriteToType();

    void setWriteToType(boolean writeToType);

    void addSpecialDefault(OBJ_TYPE type, Object value);

    Object getSpecialDefault(OBJ_TYPE type);

    INPUT_REQ getInputReq();

    default void setIconPath(String path) {

    }

    default String getIconPath() {
        return null ;
    }

    enum INPUT_REQ {
        SINGLE_TYPE,
        MULTI_TYPE,
        SINGLE_ENUM,
        MULTI_ENUM,
        SINGLE_TYPE_VAR,
        MULTI_TYPE_VAR,
        SINGLE_ENUM_VAR,
        MULTI_ENUM_VAR,
        STRING,
        INTEGER,
    }
}
