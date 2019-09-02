package eidolons.libgdx.bf.grid;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.content.PARAMS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.bf.boss.entity.BossMaster;
import eidolons.libgdx.bf.overlays.OverlayingMaster;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewOptions {

    public boolean cellBackground;
    private Runnable runnable;
    private TextureRegion portraitTexture;
    private TextureRegion directionPointerTexture;
    private TextureRegion emblem;
    private int directionValue;
    private int clockValue;
    private Color teamColor;
    private boolean mainHero;
    private String name;
    private boolean hoverResponsive;
    private String portraitPath;
    private BattleFieldObject obj;


    public UnitViewOptions() {

    }
    public UnitViewOptions(BattleFieldObject obj) {
        createFromGameObject(obj);
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public final Runnable getRunnable() {
        return this.runnable;
    }

    public final void setRunnable(Runnable var1) {
        this.runnable = var1;
    }

    public final TextureRegion getPortraitTexture() {
        return this.portraitTexture;
    }


    public final TextureRegion getDirectionPointerTexture() {
        return this.directionPointerTexture;
    }

    public TextureRegion getEmblem() {
        return emblem;
    }

    public final int getDirectionValue() {
        return this.directionValue;
    }


    public final int getClockValue() {
        return this.clockValue;
    }

    public BattleFieldObject getObj() {
        return obj;
    }

    public final void createFromGameObject(BattleFieldObject obj) {
        this.portraitTexture = getOrCreateR(obj.getImagePath());
        this.portraitPath =  (obj.getImagePath());
        this.name = obj.getName();
        this.obj = obj;



        this.mainHero =obj.isMine() && obj.isMainHero();

        if (obj instanceof Structure) {
            if (obj.isLandscape()) {
                cellBackground = true;
            }
            if (obj.isWall()) {
                cellBackground = true;
            }
            if (obj instanceof Entrance) {
                cellBackground = true;
            }


        } else if (obj instanceof Unit) {
            this.directionValue = obj.getFacing().getDirection().getDegrees();
            this.directionPointerTexture = getOrCreateR(
             StrPathBuilder.build(PathFinder.getUiPath(),
              "DIRECTION POINTER.png"));


            String emblem = obj.getProperty(G_PROPS.EMBLEM, true);

            if (ImageManager.isImage(emblem)) {
                this.emblem = getOrCreateR(emblem);
            } else {
                emblem = PathFinder.getEmblemAutoFindPath() +
                 FileManager.findFirstFile(PathFinder.getImagePath() + PathFinder.getEmblemAutoFindPath(),
                  obj.getSubGroupingKey(), true);
                if (ImageManager.isImage(emblem))
                    this.emblem = getOrCreateR(emblem);
                else
                    emblem = obj.getOwner().getHeroObj().getProperty(G_PROPS.EMBLEM, true);
                if (ImageManager.isImage(emblem))
                    this.emblem = getOrCreateR(emblem);
            }
            if (this.emblem == null)
                this.emblem = getOrCreateR(ImageManager.getEmptyEmblemPath());

            this.clockValue = obj.getIntParam(PARAMS.C_INITIATIVE);
        }
        boolean altColor=false;

        if (obj.isMine()) {
            altColor = !obj.isPlayerCharacter();
        } else {
            if (EidolonsGame.DUEL) {
                altColor = false;
            } else {
            altColor= true;
            if (obj.getName().contains("Carnifex"))
                altColor=false;
            if (obj.getName().contains("Igor"))
                altColor=false;
            if (obj.getName().contains("Hollow Reaper"))
                altColor=false;
            if (obj.getName().contains("Hollow Defiler"))
                altColor=false;
        }
        }

        if (obj.getOwner() != null)
            this.teamColor = GdxColorMaster.getColor(
                    altColor ?
                            obj.getOwner().getFlagColorAlt() :
                    obj.getOwner().getFlagColor());
        if (this.teamColor == null) {
            this.teamColor = GdxColorMaster.NEUTRAL;
        }
        hoverResponsive = !obj.isWall();
    }

    public boolean isMainHero() {
        return mainHero;
    }

    public String getName() {
        return name;
    }

    public boolean isHoverResponsive() {
        return hoverResponsive;
    }

    public String getPortraitPath() {
        return portraitPath;
    }


}
