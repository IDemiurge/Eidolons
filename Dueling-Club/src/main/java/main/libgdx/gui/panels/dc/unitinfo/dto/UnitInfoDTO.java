package main.libgdx.gui.panels.dc.unitinfo.dto;

public class UnitInfoDTO {
    private DescriptionDTO description;
    private boolean isSetDescription;

    private WeaponInfoDTO mainWeaponInfo;
    private boolean isSetMainWeaponInfo;

    private ResourceDTO resource;
    private boolean isSetResource;

    private StatsDTO stats;
    private boolean isSetStats;

    private IconGridDTO effects;
    private boolean isSetEffects;

    private IconGridDTO abilities;
    private boolean isSetAbilities;


    private AvatarDTO avatar;
    private boolean isSetAvatar;

    private InitiativeAndActionDTO initiativeAndAction;
    private boolean isSetInitiativeAndAction;

    private UnknownParamDTO unknownParam;
    private boolean isSetUnknownParam;

    private ArmorDTO armor;
    private boolean isSetArmor;

    private TabsDTO resistTabs;
    private boolean isSetResistTabs;


    private DescriptionDTO description2;
    private boolean isSetDescription2;

    private WeaponInfoDTO offWeaponInfo;
    private boolean isSetOffWeaponInfo;

    private TabsDTO combatStats;
    private boolean isSetCombatStats;

    public DescriptionDTO getDescription() {
        return description;
    }

    public void setDescription(DescriptionDTO description) {
        isSetDescription = true;
        this.description = description;
    }

    public boolean isSetDescription() {
        return isSetDescription;
    }

    public WeaponInfoDTO getMainWeaponInfo() {
        return mainWeaponInfo;
    }

    public void setMainWeaponInfo(WeaponInfoDTO mainWeaponInfo) {
        isSetMainWeaponInfo = true;
        this.mainWeaponInfo = mainWeaponInfo;
    }

    public boolean isSetMainWeaponInfo() {
        return isSetMainWeaponInfo;
    }

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        isSetResource = true;
        this.resource = resource;
    }

    public boolean isSetResource() {
        return isSetResource;
    }

    public StatsDTO getStats() {
        return stats;
    }

    public void setStats(StatsDTO stats) {
        isSetStats = true;
        this.stats = stats;
    }

    public boolean isSetStats() {
        return isSetStats;
    }

    public IconGridDTO getEffects() {
        return effects;
    }

    public void setEffects(IconGridDTO effects) {
        isSetEffects = true;
        this.effects = effects;
    }

    public boolean isSetEffects() {
        return isSetEffects;
    }

    public IconGridDTO getAbilities() {
        return abilities;
    }

    public void setAbilities(IconGridDTO abilities) {
        isSetAbilities = true;
        this.abilities = abilities;
    }

    public boolean isSetAbilities() {
        return isSetAbilities;
    }

    public AvatarDTO getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarDTO avatar) {
        isSetAvatar = true;
        this.avatar = avatar;
    }

    public boolean isSetAvatar() {
        return isSetAvatar;
    }

    public InitiativeAndActionDTO getInitiativeAndAction() {
        return initiativeAndAction;
    }

    public void setInitiativeAndAction(InitiativeAndActionDTO initiativeAndAction) {
        isSetInitiativeAndAction = true;
        this.initiativeAndAction = initiativeAndAction;
    }

    public boolean isSetInitiativeAndAction() {
        return isSetInitiativeAndAction;
    }

    public UnknownParamDTO getUnknownParam() {
        return unknownParam;
    }

    public void setUnknownParam(UnknownParamDTO unknownParam) {
        isSetUnknownParam = true;
        this.unknownParam = unknownParam;
    }

    public boolean isSetUnknownParam() {
        return isSetUnknownParam;
    }

    public ArmorDTO getArmor() {
        return armor;
    }

    public void setArmor(ArmorDTO armor) {
        isSetArmor = true;
        this.armor = armor;
    }

    public boolean isSetArmor() {
        return isSetArmor;
    }

    public TabsDTO getResistTabs() {
        return resistTabs;
    }

    public void setResistTabs(TabsDTO resistTabs) {
        isSetResistTabs = true;
        this.resistTabs = resistTabs;
    }

    public boolean isSetResistTabs() {
        return isSetResistTabs;
    }

    public DescriptionDTO getDescription2() {
        return description2;
    }

    public void setDescription2(DescriptionDTO description2) {
        isSetDescription2 = true;
        this.description2 = description2;
    }

    public boolean isSetDescription2() {
        return isSetDescription2;
    }

    public WeaponInfoDTO getOffWeaponInfo() {
        return offWeaponInfo;
    }

    public void setOffWeaponInfo(WeaponInfoDTO offWeaponInfo) {
        isSetOffWeaponInfo = true;
        this.offWeaponInfo = offWeaponInfo;
    }

    public boolean isSetOffWeaponInfo() {
        return isSetOffWeaponInfo;
    }

    public TabsDTO getCombatStats() {
        return combatStats;
    }

    public void setCombatStats(TabsDTO combatStats) {
        isSetCombatStats = true;
        this.combatStats = combatStats;
    }

    public boolean isSetCombatStats() {
        return isSetCombatStats;
    }
}
