package main.test.libgdx;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created by PC on 03.11.2016.
 */
public class TexturePackerLaunch {

    public static void main(String[] args) throws Exception {
        String inputDir = "D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resIN";
        String outputDir = "D:\\NewRepos\\battlecraft\\Dueling-Club\\src\\main\\java\\main\\test\\libgdx\\resOUT";
        String packFileName = "exit_button";
        TexturePacker.process(inputDir, outputDir, packFileName);
//            TexturePacker.process(inputDir, outputDir, packFileName);
    }
}

