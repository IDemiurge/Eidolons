package main.libgdx.anims;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import main.content.enums.entity.ItemEnums.WEAPON_GROUP;
import main.content.enums.entity.ItemEnums.WEAPON_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.entity.active.DC_ActiveObj;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.Obj;
import main.entity.type.ObjType;
import main.libgdx.anims.sprite.SpriteAnimation;
import main.libgdx.anims.sprite.SpriteAnimationFactory;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.math.PositionMaster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by JustMe on 9/6/2017.
 */
public class AnimMaster3d {

    private static final String SEPARATOR = "_";
    private static final String ANIM = "anim";
    private static Map<String, TextureAtlas> atlasMap = new HashMap<>();
    int frames = 20;
    //speed
    int[] sizes = {

    };

    public static Vector2 getOffset(DC_ActiveObj activeObj) {
        return null;
    }

    public static String getAtlasFileKeyForAction(Boolean projection,
                                                  DC_ActiveObj activeObj, boolean missile) {
        DC_WeaponObj weapon = activeObj.getActiveWeapon();
     if (missile){
         if (weapon.getAmmo()==null )
             return null;
         weapon= weapon.getAmmo().getWrappedWeapon();
     }
        StringBuilder s = new StringBuilder();
        s.append(
        StringMaster.join(SEPARATOR,
        weapon.getProperty(G_PROPS.BASE_TYPE),
//         "Warhammer",
         activeObj.getName(), ANIM,
         (projection == null ? "hor" :
          (projection ? "from" : "to"))
        ));

        if (isAssymetric(weapon.getProperty(G_PROPS.BASE_TYPE))) {
            if (activeObj.isOffhand())
                s.append(SEPARATOR+"l");
            else s.append(SEPARATOR+"r");
        }
        return s.toString().toLowerCase().replace(" ", "_");
    }
        public static String getAtlasPath(DC_ActiveObj activeObj) {

        StrPathBuilder s = new StrPathBuilder(
         PathFinder.getWeaponAnimPath(),"atlas",
         activeObj.getActiveWeapon().getWeaponType().toString()
         ,activeObj.getActiveWeapon().getWeaponGroup().toString()
         +".atlas"
        );
        return s.toString();
    }

    private static boolean isAssymetric(String activeWeapon) {
        switch (activeWeapon) {
            case "Fist":
                return true;

        }
        return false;
    }

    public static SpriteAnimation getFxSpriteForAction(DC_ActiveObj activeObj) {
        return null;
    }

    //TODO separate ranged weapon and missile !
    public static SpriteAnimation getSpriteForAction(float duration,
                                                     DC_ActiveObj activeObj,
                                                     Obj targetObj) {
        return getSpriteForAction(duration, activeObj, targetObj, false);
    }
        public static SpriteAnimation getSpriteForAction(float duration,
        DC_ActiveObj activeObj,
        Obj targetObj, boolean missile) {
        // loops,
        Boolean projection =
         PositionMaster.isAboveOr(activeObj.getOwnerObj(), targetObj);
        if (activeObj.getOwnerObj().getCoordinates().equals(targetObj.getCoordinates()))
            projection = activeObj.getOwnerObj().isMine();
        //TODO who is displayed above on the cell?
       boolean offhand = activeObj.isOffhand();
        Boolean flipHor = null;
        if (projection == null) {
            flipHor = PositionMaster.isToTheLeft(activeObj.getOwnerObj(), targetObj);
        } else {
            flipHor = offhand;
        }
//modify texture? coloring, sizing,
        float angle = PositionMaster.getAngle(activeObj.getOwnerObj(), targetObj);
//float baseAngle =
        float rotation = angle * 2 / 3;
        String name = getAtlasFileKeyForAction(projection, activeObj, missile);

        TextureAtlas atlas = getAtlas(activeObj, missile);
        Array<AtlasRegion> regions = atlas.findRegions(name);
if (regions.size==0 ){
    regions = findAtlasRegions(atlas, projection, activeObj);
}
        float frameDuration = duration / regions.size;
        int loops = 0;
        if (missile) {
            loops = Math.max(0,
             PositionMaster.getDistance(activeObj.getOwnerObj(), targetObj) - 1);
        }
        if (loops != 0)
            frameDuration /= loops;

        SpriteAnimation sprite = SpriteAnimationFactory.
         getSpriteAnimation(regions, frameDuration, loops);
//        sprite.setRotation(rotation);
        sprite.setFlipX(flipHor);
        return sprite;
    }

    private static Array<AtlasRegion> findAtlasRegions(TextureAtlas atlas,
                                                       Boolean projection,
                                                       DC_ActiveObj activeObj) {
        String name = getAtlasFileKeyForAction(projection, activeObj, false);
         List<ObjType> types = new LinkedList<>(DataManager.getBaseWeaponTypes()).stream().
         filter(type -> type.getProperty(G_PROPS.WEAPON_GROUP).equals(
          activeObj.getActiveWeapon().getProperty(G_PROPS.WEAPON_GROUP))).collect(Collectors.toList());
        Array<AtlasRegion> regions = null;
        for (ObjType sub : types) {
            name = sub.getName() +  name.substring(name.indexOf(SEPARATOR));
            regions = atlas.findRegions(name.toLowerCase());
            if (regions.size>0)
                break;
        }
        return regions;

    }

    private static TextureAtlas getAtlas(DC_ActiveObj activeObj, boolean missile) {
        String path = getAtlasPath(activeObj);
        TextureAtlas atlas = atlasMap.get(path);
        if (atlas == null) {
            atlas = new TextureAtlas(path);
            atlasMap.put(path, atlas);
        }
        return atlas;
    }




    public static boolean is3dAnim(DC_ActiveObj active) {
        if (active.isRanged())
            return true;
        if (active.getName().contains("Sword Swing"))
            return true;
        if (active.getName().contains("Slash"))
            return true;
        if (active.getName().contains("Thrust"))
            return true;
        if (active.getActiveWeapon().getWeaponType()== WEAPON_TYPE.BLUNT)
            return true;
        if (active.getActiveWeapon().getWeaponGroup()== WEAPON_GROUP.FISTS)
            return true;
        return false;
    }


    public static int getWeaponActionSpeed(DC_ActiveObj active) {
        if (active.isRanged())
            return 400;
        if (active.getActiveWeapon().isTwoHanded())
            return 40;
        return 50;
    }
}
