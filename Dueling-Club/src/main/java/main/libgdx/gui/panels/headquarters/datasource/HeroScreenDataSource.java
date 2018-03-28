package main.libgdx.gui.panels.headquarters.datasource;

import main.libgdx.gui.panels.dc.ValueContainer;

import java.util.List;

/**
 * Created by JustMe on 5/21/2017.
 */
public interface HeroScreenDataSource
// extends ResourceSource
{

    List<ValueContainer> getUnlockedMasteries();

    List<ValueContainer> getLockedMasteries();

    List<ValueContainer> getAvailableSkills();

    List<ValueContainer> getLearnedSkills();

    List<ValueContainer> getSkillsBlockedByXp();
}
