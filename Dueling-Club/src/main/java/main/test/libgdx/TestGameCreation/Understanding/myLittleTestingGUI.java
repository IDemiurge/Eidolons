package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import main.data.filesys.PathFinder;

/**
 * Created by PC on 03.11.2016.
 */
public class myLittleTestingGUI extends Stage {

    Button button;


//    public myLittleTestingGUI() {
//      super(new FitViewport(15,20));
//
//        button = new Button(ResHolder.buttonStyle);
//        button.setBounds(10,10,3,3);
//        button.addListener(new ClickListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                if (GameScore.statement == Statements.PLAY){
//                    GameScore.statement = Statements.PAUSE;
//                }else {
//                    GameScore.statement = Statements.PLAY;
//                }
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        });
//        addActor(button);
//    }
//
//    @Override
//    public void draw() {
//        super.draw();
//        getBatch().begin();
//        ResHolder.font.draw(getBatch(),Integer.toString(GameScore.Score),0,18);
//        getBatch().end();
//    }
}
