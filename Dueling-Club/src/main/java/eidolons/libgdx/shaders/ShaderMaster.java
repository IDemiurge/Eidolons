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
    private static String defaultVertex;

    public static String getDefaultVertex() {
        if (defaultVertex == null) {
            String path = getShadersPath() ;
            defaultVertex = FileManager.readFile(path + "std.vert");
        }
        return defaultVertex;
    }

    public static void setDefaultVertex(String defaultVertex) {
        ShaderMaster.defaultVertex = defaultVertex;
    }

    @Test
    public  void syncShaderData(){
        CoreEngine.systemInit();
        String srcPath = (PathFinder.getRootPath() + getClass().getPackage().toString())
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
        ShaderProgram.pedantic = false;
        ShaderProgram program = shaderMap.get(shader);
        if (program == null) {
            String path = getShadersPath() + "/" + shader.getPath();
            String vert = FileManager.readFile(path + ".vert");
            if (vert.isEmpty()) {
                vert = getDefaultVertex();
            }
            String frag = FileManager.readFile(path + ".frag");
            program = new ShaderProgram(vert, frag);
            shaderMap.put(shader, program);
        }
        return program;
    }
   static String path;
    private static String getShadersPath() {
        if (path == null) {
            path = PathFinder. getShadersPath();
//            path = (PathFinder.getRootPath() +ShaderMaster.class.getPackage().toString())
//             .replace(".", "/")
//             .replace("target", "src\\main\\java")
//             .replace("package ", "") + "/data/"
            ;
        }
        return path;
    }

    public enum SHADER{
        DARKEN,
        INVERT,
        GRAYSCALE,
        FISH_EYE,
        BLUR(),
        GREY_DARKEN;
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
