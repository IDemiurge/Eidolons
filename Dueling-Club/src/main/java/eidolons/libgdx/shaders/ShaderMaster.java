package eidolons.libgdx.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import main.data.filesys.PathFinder;
import main.system.auxiliary.data.FileManager;
import main.system.launch.CoreEngine;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JustMe on 12/2/2018.
 */
public class ShaderMaster {

    private static Map<SHADER, ShaderProgram> shaderMap = new HashMap<>();
    @Test
    public  void syncShaderData(){
        CoreEngine.systemInit();
        String srcPath = (PathFinder.getEnginePath() + getClass().getPackage().toString())
         .replace(".", "/")
         .replace("target", "src\\main\\java")
         .replace("package ", "") + "/data"
         ;
        String targetPath =   PathFinder.getTextPath() + "shaders/";

        for (File file : FileManager.getFilesFromDirectory(srcPath, false)) {
            FileManager.copy(file.getPath(), targetPath+file.getName());
        }
    }
    public static ShaderProgram getShader(SHADER shader) {
        ShaderProgram program = shaderMap.get(shader);
        if (program == null) {
           String path = PathFinder.getShadersPath()+ shader.getPath();
            String vert = FileManager.readFile(path + ".vert");
            String frag = FileManager.readFile(path + ".frag");
            program = new ShaderProgram(vert, frag);
            shaderMap.put(shader, program);
        }
        return program;
    }

    public enum SHADER{
        DARKEN,
        GRAYSCALE,
        FISH_EYE,
        BLUR("shadertut/lesson5"),
;
        String path;

        SHADER() {
            this.path = toString();
        }

        public String getPath() {
            return path;
        }

        SHADER(String path) {
            this.path = path;
        }
    }

    // substitute vars
    public static void compileDynamicShader(){
        //        PathFinder.getShadersPath()
        //        ShaderProgram shader = new ShaderProgram(vert, frag);
    }
}
