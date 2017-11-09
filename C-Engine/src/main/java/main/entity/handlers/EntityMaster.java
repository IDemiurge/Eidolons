package main.entity.handlers;


import main.entity.Entity;
import main.game.core.game.Game;

/**
 * Created by JustMe on 2/15/2017.
 */
public abstract class EntityMaster  <E extends Entity>   {

    EntityInitializer initializer;
    EntityCalculator calculator;
    EntityChecker entityChecker;
    EntityHandler entityHandler;
    EntityAnimator entityAnimator;
    EntityLogger entityLogger;
    EntityResetter resetter;
    E entity;
    Game game;


    public EntityMaster(E entity){
        this.entity=(entity);
        initializer=createInitializer();
        calculator=createCalculator();
        entityHandler= createHandler();
        entityChecker=createEntityChecker();
        resetter=createResetter();
        entityLogger=createEntityLogger();
        entityAnimator=createEntityAnimator();
        game = entity.getGame();
    }

    protected abstract EntityAnimator<E> createEntityAnimator();
    protected abstract EntityLogger<E> createEntityLogger();
    protected abstract EntityInitializer<E> createInitializer();
    protected abstract EntityChecker<E> createEntityChecker();
    protected abstract EntityResetter<E> createResetter();
    protected abstract EntityCalculator<E> createCalculator();
    protected abstract EntityHandler<E> createHandler();

    public EntityAnimator getAnimator() {
        return entityAnimator;
    }

    public EntityLogger getLogger() {
        return entityLogger;
    }

    public EntityInitializer getInitializer() {
        return initializer;
    }

    public EntityCalculator getCalculator() {
        return calculator;
    }

    public EntityChecker getChecker() {
        return entityChecker;
    }

    public EntityHandler getHandler() {
        return entityHandler;
    }

    public EntityResetter getResetter() {
        return resetter;
    }

    public E getEntity() {
        return entity;
    }

    public Game getGame() {
        return game;
    }
}
