package main.content.enums.entity;

/**
 * Created by Alexander on 2/1/2022
 */
public class Hero2Enums {
public enum RACE_RARITY{
    C("Common"), U("Uncommon"), R("Rare")
    ;
    String name;

    RACE_RARITY(String name) {
        this.name = name;
    }
}
}
