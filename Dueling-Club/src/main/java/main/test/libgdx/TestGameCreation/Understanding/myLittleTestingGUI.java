package main.test.libgdx.TestGameCreation.Understanding;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * Created by PC on 03.11.2016.
 */
public class myLittleTestingGUI extends Stage {

    Button button;
    TextButton exit_button;
    float x_pos_for_butt;
    float y_pos_for_butt;
    float width_for_butt;
    float height_for_butt;
    static float View_Width = 800;
    static float View_Height = 600;
    Group gui_group;
//
//    public myLittleTestingGUI() {
//      super(new FitViewport(View_Width,View_Height));
//        width_for_butt = 100;
//        height_for_butt = 100;
//        x_pos_for_butt = View_Width - width_for_butt-1;
//        y_pos_for_butt = View_Height - height_for_butt-1;
//
//        gui_group = new Group();
//        gui_group.setBounds(View_Width+1,0,View_Width,View_Height);
//        button = new Button(ResHolder.buttonStyle);
//        button.setBounds(x_pos_for_butt,y_pos_for_butt,width_for_butt,height_for_butt);
//        button.addListener(new ClickListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                if (GameScore.statement == Statements.PLAY){
//                    GameScore.statement = Statements.PAUSE;
//                    gui_group.addAction(Actions.moveTo(0,0,0.5f));
//                }else {
//                    GameScore.statement = Statements.PLAY;
//                    gui_group.addAction(Actions.moveTo(View_Width + 1,0,0.5f));
//                }
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        });
////        addActor(button);
////        exit_button = new TextButton("Exit",ResHolder.exit_button_skin);
//        exit_button= new TextButton("Exit",ResHolder.exit_button_style);
//
//        exit_button.setBounds(View_Width/2,View_Height/2 - 75,100,75);
//        exit_button.addListener(new ClickListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//
//                return true;
////                return super.touchDown(event, x, y, pointer, button);
//            }
//
//            @Override
//            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
//                Gdx.app.exit();
//                super.touchUp(event, x, y, pointer, button);
//            }
//        });
//
////        gui_group.addActor(button);
//        gui_group.addActor(exit_button);
//        addActor(gui_group);
//        addActor(button);
//    }
//
//    @Override
//    public void draw() {
//        super.draw();
//        getBatch().begin();
//        ResHolder.font.draw(getBatch(),Integer.toString(GameScore.Score),View_Width/2-50,View_Height);
//        getBatch().end();
//    }
}
