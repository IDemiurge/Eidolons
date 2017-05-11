package main.game.battlecraft.logic.meta.arcade;

public class BattleLevel {

    private ArenaArcade arcade;
    private String levelData;
    private String groupData;

    public BattleLevel(ArenaArcade arcade, String levelData, String groupData) {
        this.arcade = arcade;
        this.levelData = levelData;
        this.groupData = groupData;
    }

    public ArenaArcade getArcade() {
        return arcade;
    }

    public void setArcade(ArenaArcade arcade) {
        this.arcade = arcade;
    }

    public String getLevelData() {
        return levelData;
    }

    public void setLevelData(String levelData) {
        this.levelData = levelData;
    }

    public String getGroupData() {
        return groupData;
    }

    public void setGroupData(String groupData) {
        this.groupData = groupData;
    }

}
