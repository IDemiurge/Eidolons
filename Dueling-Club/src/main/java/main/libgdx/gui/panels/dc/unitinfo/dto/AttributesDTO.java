package main.libgdx.gui.panels.dc.unitinfo.dto;

public class AttributesDTO {

    private String strength;
    private String vitality;
    private String agility;
    private String dexterity;
    private String willpower;
    private String spellpower;
    private String intelligence;
    private String knowledge;
    private String wisdom;
    private String charisma;

    public AttributesDTO(String strength, String vitality,
                         String agility, String dexterity,
                         String willpower, String spellpower,
                         String intelligence, String knowledge,
                         String wisdom, String charisma) {
        this.strength = strength;
        this.vitality = vitality;
        this.agility = agility;
        this.dexterity = dexterity;
        this.willpower = willpower;
        this.spellpower = spellpower;
        this.intelligence = intelligence;
        this.knowledge = knowledge;
        this.wisdom = wisdom;
        this.charisma = charisma;
    }

    public String getStrength() {
        return strength;
    }

    public String getVitality() {
        return vitality;
    }

    public String getAgility() {
        return agility;
    }

    public String getDexterity() {
        return dexterity;
    }

    public String getWillpower() {
        return willpower;
    }

    public String getSpellpower() {
        return spellpower;
    }

    public String getIntelligence() {
        return intelligence;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public String getWisdom() {
        return wisdom;
    }

    public String getCharisma() {
        return charisma;
    }
}
