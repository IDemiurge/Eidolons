package elements.exec;

import elements.exec.targeting.TargetGroup;

public interface Executable {
    boolean execute(EntityRef ref);
    default TargetGroup selectTargets() {
        return null;
    }
}
