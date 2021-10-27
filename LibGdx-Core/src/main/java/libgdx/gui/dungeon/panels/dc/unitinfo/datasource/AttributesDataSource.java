package libgdx.gui.dungeon.panels.dc.unitinfo.datasource;

public interface AttributesDataSource {
    String getStrength();

    String getVitality();

    String getAgility();

    String getDexterity();

    String getWillpower();

    String getSpellpower();

    String getIntelligence();

    String getKnowledge();

    String getWisdom();

    String getCharisma();

    String getAttribute(String name);
}
