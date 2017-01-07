package main.libgdx.texture

import com.badlogic.gdx.graphics.Texture
import java.io.File
import java.util.*

class TextureCache(private var imagePath: String) {
    private var cache: MutableMap<String, Texture> = HashMap()

    fun getOrCreate(path: String): Texture {
        return get(path, true)
    }

    fun get(path: String): Texture {
        return get(path, false)
    }

    fun get(path: String, save: Boolean): Texture {
        var p = path
        if (!path.startsWith(File.separator)) {
            p = File.separator + path
        }

        p = imagePath + p


        if (!cache.containsKey(p)) {
            val t = Texture(p)
            if (save)
                cache.put(p, t)
            else return t
        }

        return cache[p] as Texture
    }
}