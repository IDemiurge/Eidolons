package main.game.module.dungeoncrawl.explore;

/**
 * Created by JustMe on 9/9/2017.
 */
public interface RealTimeGameLoop {
    public void end();

    public void act(float delta);
}
