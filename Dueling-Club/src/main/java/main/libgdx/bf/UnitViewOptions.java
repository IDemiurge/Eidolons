package main.libgdx.bf;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.content.values.properties.G_PROPS;
import main.data.filesys.PathFinder;
import main.entity.obj.BattleFieldObject;
import main.entity.obj.Structure;
import main.entity.obj.unit.Unit;
import main.game.module.dungeoncrawl.dungeon.Entrance;
import main.libgdx.GdxColorMaster;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.data.FileManager;
import main.system.images.ImageManager;

import static main.content.PARAMS.C_INITIATIVE;
import static main.libgdx.texture.TextureCache.getOrCreateR;

public class UnitViewOptions {

    private Runnable runnable;

    private TextureRegion portrateTexture;

    private TextureRegion directionPointerTexture;

    private Texture iconTexture;

    private TextureRegion clockTexture;
    private TextureRegion emblem;
    private int directionValue;

    private int clockValue;
    private Color teamColor;
    private boolean mainHero;
    private String name;
    public boolean cellBackground;


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

    public final TextureRegion getPortrateTexture() {
        return this.portrateTexture;
    }


    public final TextureRegion getDirectionPointerTexture() {
        return this.directionPointerTexture;
    }


    public final Texture getIconTexture() {
        return this.iconTexture;
    }

    public TextureRegion getEmblem() {
        return emblem;
    }

    public final TextureRegion getClockTexture() {
        return this.clockTexture;
    }


    public final int getDirectionValue() {
        return this.directionValue;
    }


    public final int getClockValue() {
        return this.clockValue;
    }

    public final void createFromGameObject(BattleFieldObject obj) {
        this.portrateTexture = getOrCreateR(obj.getImagePath());
        this.name =  obj.getName() ;
        this.mainHero =  obj.isMainHero() ;

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



        }else
        if (obj instanceof Unit) {
            this.directionValue = obj.getFacing().getDirection().getDegrees();
            this.directionPointerTexture = getOrCreateR("/UI/DIRECTION POINTER.png");

            this.clockTexture = getOrCreateR(
             "UI\\SPECIAL\\Data4_orc-0000001290.png"
//             "/UI/value icons/actions.png"
            );
            String emblem = obj.getProperty(G_PROPS.EMBLEM, true);

            if (ImageManager.isImage(emblem))
            {
                this.emblem = getOrCreateR(emblem);
            }
            else {
                emblem =PathFinder.getEmblemAutoFindPath()+
                 FileManager.findFirstFile(PathFinder.getImagePath()+ PathFinder.getEmblemAutoFindPath(),
                  obj.getSubGroupingKey(), true);
                if (ImageManager.isImage(emblem))
                    this.emblem = getOrCreateR(emblem);
                else
                    emblem = obj.getOwner().getHeroObj().getProperty(G_PROPS.EMBLEM, true);
                if (ImageManager.isImage(emblem))
                    this.emblem = getOrCreateR(emblem);
            }
            if (this.emblem == null)
                this.emblem = TextureCache.getOrCreateR(ImageManager.getEmptyEmblemPath());

            this.clockValue = obj.getIntParam(C_INITIATIVE);
            this.teamColor =
             GdxColorMaster.getColor(obj.getOwner().getFlagColor());
            if (teamColor == null) {
                teamColor = GdxColorMaster.NEUTRAL;
            }
        }
    }

    public boolean isMainHero() {
        return mainHero;
    }

    public String getName() {
        return name;
    }
}
