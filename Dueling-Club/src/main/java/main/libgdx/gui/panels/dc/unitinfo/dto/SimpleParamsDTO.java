package main.libgdx.gui.panels.dc.unitinfo.dto;

public class SimpleParamsDTO {
    private String resistance;
    private String dextrity;
    private String armor;
    private String stamina;
    private String wisdom;

    public SimpleParamsDTO(String resistance, String dextrity, String armor, String stamina, String wisdom) {
        this.resistance = resistance;
        this.dextrity = dextrity;
        this.armor = armor;
        this.stamina = stamina;
        this.wisdom = wisdom;
    }

    public String getResistance() {
        return resistance;
    }

    public String getDextrity() {
        return dextrity;
    }

    public String getArmor() {
        return armor;
    }

    public String getStamina() {
        return stamina;
    }

    public String getWisdom() {
        return wisdom;
    }
}
