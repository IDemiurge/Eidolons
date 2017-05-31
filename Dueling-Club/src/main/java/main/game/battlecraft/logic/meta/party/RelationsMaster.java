package main.game.battlecraft.logic.meta.party;

/**
 * Created by JustMe on 5/30/2017.
 * Maybe use Trust/Esteem/Affection/…?
 How is status determined then?
How will it change during campaign?

 fighting together
 speeches
 scripted events
 temporal ?

 */
public class RelationsMaster {

    public enum RELATIONS_STATUS{
        BONDED, //refuse to separate

        FRIENDLY,
        IN_LOVE,
        NORMAL,
        SUSPICIOUS,

        DISGUSTED, //refuse to fight together
        TERRIFIED,  //refuse to fight together
    }
}
