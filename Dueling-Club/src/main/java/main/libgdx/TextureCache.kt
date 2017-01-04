package main.libgdx

import com.badlogic.gdx.graphics.Texture
import main.system.images.ImageManager
import java.io.File
import java.util.*

class TextureCache(private var imagePath: String) {
    private var cache: MutableMap<String, Texture> = HashMap()

    fun getOrCreate(path: String): Texture {
        var p = path
        if (!path.startsWith(File.separator)) {
            p = File.separator + path
        }

        p = imagePath + p
        if (ImageManager.getPATH() != null)
            if (!ImageManager.isImage(p)) {
                p = ImageManager.getPATH() + (ImageManager.getAltEmptyListIcon())
            }

        if (!cache.containsKey(p)) {
            val t = Texture(p)
            cache.put(p, t)
        }

        return cache[p] as Texture
    }
}