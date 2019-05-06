package main.game.logic.battle.player;

import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.game.core.game.GenericGame;
import main.system.auxiliary.log.Chronos;
import main.system.entity.FilterMaster;
import main.system.graphics.ColorManager.FLAG_COLOR;

import java.awt.*;
import java.util.Set;

public class Player {
    public static Player NEUTRAL;


    protected MicroObj heroObj;
    protected GenericGame game;
    protected String portrait;
    protected boolean ai;
    FLAG_COLOR flagColor;
    String name;
    boolean me;
    private boolean neutral;
    private String emblem;
    private Set<Obj> units;
    private String mainHeroName;

    public Player(String name, Color c,
                  boolean neutral, boolean me,
                  String portrait,
                  String emblem) {
        this.name = name;
        this.me = me;
        this.neutral = neutral;
        this.portrait = portrait;
        this.emblem = emblem;


    }

    public Player(String name, Color color, boolean me, String emblem) {
        this(name, color, false, me, emblem, null);
    }


    @Override
    public String toString() {
        return name;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getEmblem() {
        return emblem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public MicroObj getHeroObj() {
        if (heroObj == null) {
            if (collectControlledUnits().size() > 0) {
                heroObj = (MicroObj) collectControlledUnits().toArray()[0];
            }
        }
        return heroObj;
    }

    /**
     * @param heroObj the heroObj to set
     */
    public void setHeroObj(MicroObj heroObj) {
        this.heroObj = heroObj;
    }


    public Set<Obj> collectControlledUnits() {
        if (units == null) {
            resetUnits();
        }
        return units;

    }

    public boolean isEnemy() {
        // game.getPlayer(true)
        if (isMe()) {
            return false;
        }
        return !isNeutral();
    }

    public boolean isHostileTo(Player player) {
        if (equals(player)) {
            return false;
        }
        return !isNeutral();
    }

    public boolean isNeutral() {
        return neutral;
    }

    public void resetUnits() {
        Chronos.mark("resetUnits for " + this);
        units = FilterMaster.getPlayerControlledUnits(this);
        Chronos.logTimeElapsedForMark("resetUnits for " + this);
    }

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }


    public FLAG_COLOR getFlagColor() {
        return flagColor;
    }

    public void setFlagColor(FLAG_COLOR flagColor) {
        this.flagColor = flagColor;
    }

    public GenericGame getGame() {
        return game;
    }

    public void setGame(GenericGame game) {
        this.game = game;
    }

    public String getMainHeroName() {
        return mainHeroName;
    }

    public void setMainHeroName(String mainHeroName) {
        this.mainHeroName = mainHeroName;
    }
}
