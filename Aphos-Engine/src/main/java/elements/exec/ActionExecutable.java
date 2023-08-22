package elements.exec;

import elements.exec.targeting.TargetGroup;

public class ActionExecutable implements Executable{

    @Override
    public boolean execute(EntityRef ref) {
        return false;
    }

    @Override
    public TargetGroup selectTargets() {
        //wait for UI?!
        TargetGroup availableTargets;
        //TODO MOCK!
        // new UiEvent(UiEventType.Selection, );

        return null;
    }

}
