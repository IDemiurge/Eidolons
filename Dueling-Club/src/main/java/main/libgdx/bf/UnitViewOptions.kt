package main.libgdx.bf

import com.badlogic.gdx.graphics.Texture
import main.content.OBJ_TYPES
import main.content.properties.G_PROPS
import main.entity.obj.DC_HeroObj
import main.libgdx.texture.TextureCache

class UnitViewOptions(var obj: DC_HeroObj, textureCache: TextureCache, var unitMap: MutableMap<DC_HeroObj, UnitView>) {
    var runnable: Runnable? = null
    var portrateTexture: Texture? = null
    var directionPointerTexture: Texture? = null
    var iconTexture: Texture? = null
    var clockTexture: Texture? = null
    var directionValue: Int = 0
    var clockValue: String? = null
    var hideBorder: Boolean = false
    var overlaying: Boolean = false

    init {
        createFromGameObject(obj, textureCache)
    }

    fun createFromGameObject(obj: DC_HeroObj, textureCache: TextureCache) {
        portrateTexture = textureCache.getOrCreate(obj.imagePath)

        if (obj.obJ_TYPE_ENUM === OBJ_TYPES.UNITS) {
            if (obj is Rotatable) {
                directionValue = obj.facing.direction.degrees
                directionPointerTexture = textureCache.getOrCreate("\\UI\\DIRECTION POINTER.png")
            }

            clockTexture = textureCache.getOrCreate("\\UI\\value icons\\actions.png")

            val emblem = obj.getProperty(G_PROPS.EMBLEM, true)
            if (emblem != null) {

            }
        }

        if (obj.obJ_TYPE_ENUM === OBJ_TYPES.BF_OBJ) {
            hideBorder = true
        }
    }
}

