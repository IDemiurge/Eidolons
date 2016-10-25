package main.game.player;

import main.entity.obj.MicroObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.game.MicroGame;
import main.system.FilterMaster;
import main.system.auxiliary.Chronos;
import main.system.auxiliary.ColorManager.FLAG_COLOR;
import main.system.images.ImageManager;
import main.system.net.data.PartyData;
import main.system.net.data.PlayerData;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class Player {
    public static Player NEUTRAL = new Player("Neutral", Color.GRAY, false, "");

    public static Player I;

    public static Player ENEMY;
    protected ImageIcon portrait;
    protected String p;
    protected boolean ai;
    protected String allegiance;
    protected MicroObj heroObj;
    protected ObjType hero_type;
    protected PartyData partyData;
    protected Image emblem;
    protected MicroGame game;
    String name;
    Color color;
    FLAG_COLOR flagColor;
    boolean me;
    private boolean defender;
    private Set<Obj> units;

    public Player(String name, Color c, boolean me, String portrait) {
        main.system.auxiliary.LogMaster.log(0, "new player - " + name);
        this.name = name;

        color = c;
        this.me = me;

        setPortrait(ImageManager.getEmptyUnitIcon());
        this.p = portrait;
        if (p != null)
            if (!p.equals(""))
                setPortrait(ImageManager.getIcon(p));

        if (me)
            I = this;
    }

    public Player(PlayerData playerdata) {

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

    // protected void generatePortrait() {
    // this.portrait = ImageManager.getImage(p);
    //
    // }

    public ImageIcon getPortrait() {
        return portrait;
    }

    public void setPortrait(ImageIcon portrait) {
        this.portrait = new ImageIcon(portrait.getImage());

        // ImageManager.applyBorder(
        // portrait.getImage(), BORDER.GOLDEN));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getAllegiance() {
        return allegiance;
    }

    public synchronized void setAllegiance(String allegiance) {
        this.allegiance = allegiance;
    }

    public MicroObj getHeroObj() {
        if (heroObj == null) {
            if (getControlledUnits().size() > 0)
                heroObj = (MicroObj) getControlledUnits().toArray()[0];
        }
        return heroObj;
    }

    /**
     * @param heroObj the heroObj to set
     */
    public void setHeroObj(MicroObj heroObj) {
        this.heroObj = heroObj;
    }

    public ObjType getHeroObjType() {
        return getHero_type();
    }

    protected ObjType getHero_type() {
        return hero_type;
    }

    public void setHero_type(ObjType hero_type) {
        this.hero_type = hero_type;
    }

    public PartyData getPartyData() {
        return partyData;
    }

    public void setPartyData(PartyData partyData) {
        this.partyData = partyData;
    }

    public Image getEmblem() {
        return emblem;
    }

    public void setEmblem(Image emblem) {
        this.emblem = emblem;
    }

    public Set<Obj> getControlledUnits() {
        if (units == null)
            resetUnits();
        return units;

    }

    public boolean isEnemy() {
        // game.getPlayer(true)
        if (isMe())
            return false;
        return !isNeutral();
    }

    public boolean isHostileTo(Player player) {
        if (equals(player))
            return false;
        return !isNeutral();
    }

    public boolean isNeutral() {
        return this == NEUTRAL;
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

    public boolean isDefender() {
        return defender;
    }

    public void setDefender(boolean defender) {
        this.defender = defender;
    }

    public FLAG_COLOR getFlagColor() {
        return flagColor;
    }

    public void setFlagColor(FLAG_COLOR flagColor) {
        this.flagColor = flagColor;
    }

    public MicroGame getGame() {
        return game;
    }

    public void setGame(MicroGame game) {
        this.game = game;
    }

}
