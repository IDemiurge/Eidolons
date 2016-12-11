package main.libgdx

import com.badlogic.gdx.graphics.Texture
import java.io.File
import java.util.*

class TextureCache(private var imagePath: String) {
    private var cache: MutableMap<String, Texture> = HashMap()

    fun getOreCreate(path: String): Texture {
        var p = path;
        if (!path.startsWith(File.separator)) {
            p = File.separator + path
        }

        p = imagePath + p

        if (!cache.containsKey(p)) {
            val t = Texture(p)
            cache.put(p, t)
        }

        return cache[p] as Texture
    }
}