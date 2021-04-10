package libgdx.gui.panels.headquarters.datasource.hero;

import libgdx.gui.generic.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 *
 * SkillDataSource
 * ClassDataSource
 *
 */
public interface HeroScreenDataSource
//

{

    List<ValueContainer> getUnlockedMasteries();

    List<ValueContainer> getLockedMasteries();

    List<ValueContainer> getAvailableSkills();

    List<ValueContainer> getLearnedSkills();

    List<ValueContainer> getSkillsBlockedByXp();
}
