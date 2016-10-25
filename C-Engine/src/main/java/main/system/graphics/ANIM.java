package main.system.graphics;

import main.system.graphics.AnimPhase.PHASE_TYPE;

import java.util.List;

public interface ANIM {

    Object getKey();

    boolean isPending();

    void start();

    List<AnimPhase> getPhases();

    void addPhaseArgs(boolean ifNotNullOnly, PHASE_TYPE buff, Object... args);

    void addPhase(AnimPhase lastPhase);

    void addPhaseArgs(PHASE_TYPE type, Object... args);

    void setPhaseFilter(List<PHASE_TYPE> list);

    ANIM clone();

    ANIM cloneAndAdd();

    boolean isFinished();

    ANIM getFilteredClone(PHASE_TYPE... allowedTypes);

}
