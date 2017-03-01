package main.libgdx.gui.panels.dc.unitinfo.dto;

public class MainParamsDTO {
    private String resistance;
    private String defense;
    private String armor;
    private String fortitude;
    private String spirit;

    public MainParamsDTO(String resistance, String defense, String armor, String fortitude, String spirit) {
        this.resistance = resistance;
        this.defense = defense;
        this.armor = armor;
        this.fortitude = fortitude;
        this.spirit = spirit;
    }

    public String getResistance() {
        return resistance;
    }

    public String getDefense() {
        return defense;
    }

    public String getArmor() {
        return armor;
    }

    public String getFortitude() {
        return fortitude;
    }

    public String getSpirit() {
        return spirit;
    }
}
