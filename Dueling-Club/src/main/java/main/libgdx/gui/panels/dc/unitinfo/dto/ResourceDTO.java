package main.libgdx.gui.panels.dc.unitinfo.dto;

public class ResourceDTO {
    private String toughness;
    private String endurance;
    private String stamina;
    private String morale;
    private String essence;
    private String focus;

    public ResourceDTO(String toughness, String endurance, String stamina, String morale, String essence, String focus) {
        this.toughness = toughness;
        this.endurance = endurance;
        this.stamina = stamina;
        this.morale = morale;
        this.essence = essence;
        this.focus = focus;
    }

    public String getToughness() {
        return toughness;
    }

    public String getEndurance() {
        return endurance;
    }

    public String getStamina() {
        return stamina;
    }

    public String getMorale() {
        return morale;
    }

    public String getEssence() {
        return essence;
    }

    public String getFocus() {
        return focus;
    }
}
