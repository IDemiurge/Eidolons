package eidolons.libgdx.bf.grid.cell;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.Structure;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.atb.AtbMaster;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.texture.TextureCache;
import main.content.CONTENT_CONSTS;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

import static eidolons.libgdx.texture.TextureCache.fromAtlas;
import static eidolons.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewOptions {

    public static final boolean UNIT_VIEW_ATLAS = false;
    public boolean cellBackground;
    private Runnable runnable;
    private TextureRegion portraitTexture;
    private TextureRegion directionPointerTexture;
    private TextureRegion emblem;
    private int directionValue;
    private int clockValue;
    private Color teamColor;
    private boolean mainHero;
    private boolean wall;
    private String name;
    private boolean hoverResponsive;
    private String portraitPath;
    private BattleFieldObject obj;
    private CONTENT_CONSTS.FLIP flip;

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
        portraitTexture = TextureCache.getOrCreateR(portraitPath);
    }

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
        if (UNIT_VIEW_ATLAS) {
            String imgPath = obj.getImagePath().replace("main", "unitview");
            if (obj instanceof Structure) {
                imgPath = "unitview/objects/" + PathUtils.getLastPathSegment(imgPath);
            }
            {
                this.portraitTexture = fromAtlas(UnitView.getAtlasPath(), imgPath);
            }
        }
        if (portraitTexture == null) {

        }
        this.portraitTexture = getOrCreateR(obj.getImagePath());
        this.portraitPath = (obj.getImagePath());
        this.name = obj.getName();
        this.obj = obj;
        this.flip = obj.getFlip();


        this.mainHero = obj.isMine() && obj.isMainHero();

        if (obj instanceof Structure) {

            if (obj.isWall())
                wall = true;
            else if (obj.isLandscape()) {
                cellBackground = true;
            }
            // if (obj.isWall()) {
            //     cellBackground = true;
            // }
            if (obj instanceof Entrance) {
                cellBackground = true;
            }


        } else if (obj instanceof Unit) {
            this.directionValue = obj.getFacing().getDirection().getDegrees();
            this.directionPointerTexture = getOrCreateR(
                    PathFinder.getUiPath() + "DIRECTION POINTER.png");


            String emblem = PathFinder.getEmblemAutoFindPath() + obj.getProperty(G_PROPS.EMBLEM, true);
            if (obj.isMine()) {
                emblem = PathFinder.getEmblemAutoFindPath() + "undead.png";
            }
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
                if (UNIT_VIEW_ATLAS) {
                    this.emblem = fromAtlas(UnitView.getAtlasPath(), PathUtils.getLastPathSegment(emblem));
                } else if (ImageManager.isImage(emblem))
                    this.emblem = getOrCreateR(emblem);
            }
            if (this.emblem == null)
                this.emblem = getOrCreateR(ImageManager.getEmptyEmblemPath());

            this.clockValue = AtbMaster.getDisplayedAtb(obj);
        }
        boolean altColor = false;

        if (obj.isMine()) {
            altColor = !obj.isPlayerCharacter();
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

    public void setDirectionPointerTexture(TextureRegion directionPointerTexture) {
        this.directionPointerTexture = directionPointerTexture;
    }

    public void setEmblem(TextureRegion emblem) {
        this.emblem = emblem;
    }

    public void setTeamColor(Color teamColor) {
        this.teamColor = teamColor;
    }

    public CONTENT_CONSTS.FLIP getFlip() {
        return flip;
    }

    public void setHoverResponsive(boolean hoverResponsive) {
        this.hoverResponsive = hoverResponsive;
    }

    public boolean isWall() {
        return wall;
    }
}
