package main.test.libgdx;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import main.data.filesys.PathFinder;

/**
 * Created with IntelliJ IDEA.
 * Date: 01.11.2016
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class TestMeshRender implements ApplicationListener {

    static final int WIDTH = 480;
    static final int HEIGHT = 320;

    public static void main(String[] args) {
/*
        Rectangle scissors = new Rectangle();
        Rectangle clipBounds = new Rectangle(x,y,w,h);
        ScissorStack.calculateScissors(camera, spriteBatch.getTransformMatrix(), clipBounds, scissors);
        ScissorStack.pushScissors(scissors);
        spriteBatch.draw(...);
        spriteBatch.flush();
        ScissorStack.popScissors();
*/
        TestMeshRender r = new TestMeshRender();
        new LwjglApplication(r, "1", 480, 320);
    }

    private Texture cellTexture;
    Mesh mesh;
    Rectangle glViewport;
    OrthographicCamera cam;
    ShaderProgram shader;

    public Mesh createQuad() {
        float[] verts = new float[]{
                -0.5f, -0.5f, 0, 1, 1, 1, 1, 0, 1,
                0.5f, -0.5f, 0, 1, 1, 1, 1, 1, 1,
                0.5f, 0.5f, 0, 1, 1, 1, 1, 1, 0,
                -0.5f, 0.5f, 0, 1, 1, 1, 1, 0, 0
        };

        // статическая полигональная сетка с четырьмя вершинами и без индексов
        Mesh mesh = new Mesh(true, 4, 6, VertexAttribute.Position(), VertexAttribute.  ColorUnpacked(), VertexAttribute.TexCoords(0));
//                new VertexAttribute(VertexAttributes.Usage.Position, 3, "attr_Position"),
//                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
//                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "attr_texCoords"));
//                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));

        mesh.setVertices(verts);

        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});

        return mesh;
    }

    @Override
    public void create() {
        PathFinder.init();
        cellTexture = new Texture(PathFinder.getImagePath() + "\\UI\\cells\\Empty Cell v3.png");
        mesh = createQuad();

        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.position.set(WIDTH / 2, HEIGHT / 2, 0);

        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);

        String vertexShader = "attribute vec4 a_position;    \n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_worldView;\n" +
                "varying vec4 v_color;" +
                "varying vec2 v_texCoords;" +
                "void main()                  \n" +
                "{                            \n" +
                "   v_color = vec4(1, 1, 1, 1); \n" +
                "   v_texCoords = a_texCoord0; \n" +
                "   gl_Position =  u_worldView * a_position;  \n" +
                "}                            \n";
        String fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "uniform sampler2D u_texture;\n" +
                "void main()                                  \n" +
                "{                                            \n" +
                "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
                "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        GL20 gl = Gdx.graphics.getGL20();

        // Камера --------------------- /
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);

        cam.update();
        //cam.apply(gl);

        // Текстурирование --------------------- /
        gl.glActiveTexture(GL20.GL_TEXTURE0);
        gl.glEnable(GL20.GL_TEXTURE_2D);
        cellTexture.bind();
        shader.begin();
        shader.setUniformMatrix("u_worldView", cam.combined);
        shader.setUniformi("u_texture", 0);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
