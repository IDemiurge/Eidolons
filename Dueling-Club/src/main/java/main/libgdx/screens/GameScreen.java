package main.libgdx.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import main.libgdx.bf.mouse.InputController;
import main.libgdx.stage.ChainedStage;
import main.system.audio.DC_SoundMaster;

/**
 * Created by JustMe on 2/3/2018.
 */
public class GameScreen extends ScreenWithVideoLoader{

    protected ChainedStage dialogsStage = null;
    protected OrthographicCamera cam;
    protected InputController controller;
    protected Vector2 velocity;
    protected DC_SoundMaster soundMaster;
    protected Vector2 cameraDestination;
    protected ShaderProgram bufferedShader;
    protected Float speed;
    
    @Override
    protected void afterLoad() {
        
    }
}
