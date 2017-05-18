package main.game.battlecraft.logic.meta.universal;

/**
 * Created by JustMe on 5/10/2017.
 */
public abstract class MetaGame<E extends MetaGame > {

    MetaGameMaster<E> master ;

    public MetaGame(MetaGameMaster<E > master){
        this.master = master;
    }

}
