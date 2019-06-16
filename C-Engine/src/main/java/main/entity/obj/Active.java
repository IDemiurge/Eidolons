package main.entity.obj;

import main.entity.Ref;
import main.entity.Referred;

public interface Active extends Referred {


    boolean activatedOn(Ref ref);
    boolean activate();

    boolean canBeActivated(Ref ref);

    boolean isInterrupted();

    // public Abilities getAbilities();
    // public void setAbilities(Abilities);

}
