package main.libgdx.gui.panels.dc.logpanel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class SimpleLogPanel extends LogPanel {
    private MovableHeader movableHeader;
    private ExtendButton extendButton;

    public SimpleLogPanel() {
        super();

        movableHeader = new MovableHeader();
        movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
        movableHeader.addCaptureListener(new InputListener() {
            private Vector2 offset;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float x1 = getX();
                float y1 = getY();

                Vector2 vector2 = new Vector2(x, y);
                vector2 = localToParentCoordinates(vector2);

                offset = new Vector2(x1 - vector2.x, y1 - vector2.y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                x = Math.min(Math.max(getX() + x + offset.x, 0), getStage().getWidth() - getWidth());
                y = Math.min(Math.max(getY() + y + offset.y, 0), getStage().getHeight() - getHeight());
                setPosition(x, y);
                updatePos = true;
                event.stop();
            }
        });

        addActor(movableHeader);

        extendButton = new ExtendButton();
        addActor(extendButton);

        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);

        extendButton.addCaptureListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                setHeight(Math.min(Math.max(getHeight() + y, 250), 600));
                updatePos = true;
            }
        });
    }

    @Override
    protected void updateAct() {
        super.updateAct();

        movableHeader.setBounds(0, getHeight() - 10, getWidth(), 10);
        extendButton.setPosition(getWidth() / 2 - extendButton.getWidth() / 2, getHeight() - movableHeader.getHeight() + 4);
    }
}
