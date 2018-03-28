package main.game.battlecraft.logic.meta.universal;

/**
 * Created by JustMe on 5/12/2017.
 */
public abstract class MetaInitializer<E extends MetaGame> extends MetaGameHandler<E> {
    public MetaInitializer(MetaGameMaster master) {
        super(master);
    }


    public abstract E initMetaGame(String data);
}
