package logic.core;

import eidolons.game.core.Core;
import gdx.views.HeroView;
import logic.core.game.Game;
import logic.entity.Hero;
import logic.functions.GameController;

public class Aphos {
    public static Game game;
    public static Hero hero;
    public static HeroView view;
    public static logic.functions.meta.core.Core core;

    public static void start() {
        Core.onNewThread(() ->
                {
                    game = new Game();
                    game.start(LaunchData.createDefaultData());
                }
        );
    }

    public static GameController controller() {
        return game.getController();
    }
}
