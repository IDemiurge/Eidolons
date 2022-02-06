package main.elements.conditions;

import main.entity.Entity;

public interface RequirementsManager {
    int NORMAL_MODE = 0;

    int ALT_MODE = 1;

    int RANK_MODE = 6;

    String check(Entity hero, Entity type);

    String check(Entity hero, Entity type, int mode);

    Requirements getRequirements(Entity type, int mode);

    Requirements generateSpellRequirements(Entity type, int mode);

    Requirements generateSkillRequirements(Entity type, int mode);

    void setHero(Entity hero);

}
