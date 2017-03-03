package main.test.libgdx;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * Date: 30.10.2016
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class TEST_JFRAME_LIBGDX_INTEGRATION extends JFrame {


    private JScrollPane graphicsScrollPane;
    private LwjglAWTCanvas canvas;

    public TEST_JFRAME_LIBGDX_INTEGRATION() {
        initComponents();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TEST_JFRAME_LIBGDX_INTEGRATION javaLibgdx = new TEST_JFRAME_LIBGDX_INTEGRATION();
                javaLibgdx.setVisible(true);
            }
        });
    }

    private void initComponents() {

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new JMenu("File"));
        menuBar.add(new JMenu("Edit"));
        setJMenuBar(menuBar);

        LwjglApplicationConfiguration lwjglApplicationConfiguration = new LwjglApplicationConfiguration();
        lwjglApplicationConfiguration.title = "demo";
        lwjglApplicationConfiguration.useGL30 = true;

/*        lwjglApplicationConfiguration.width = 1920;
        lwjglApplicationConfiguration.height = 1080;*/

        lwjglApplicationConfiguration.width = 1600;
        lwjglApplicationConfiguration.height = 900;
        lwjglApplicationConfiguration.fullscreen = true;

        canvas = new LwjglAWTCanvas(new DENIS_Launcher(), lwjglApplicationConfiguration);
        //canvas.getCanvas().setSize(1600, 900);

        graphicsScrollPane = new JScrollPane(canvas.getCanvas());

        graphicsScrollPane.getViewport().
                setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

        /*graphicsScrollPane.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Container parent1 = graphicsScrollPane.getParent();
                if (parent1 instanceof JComponent) {
                    ((JComponent) parent1).revalidate();
                }
                Window window = SwingUtilities.getWindowAncestor(graphicsScrollPane);
                if (window != null) {
                    window.validate();
                    System.out.println("validated . . .");
                }
            }
        });*/

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(graphicsScrollPane,
                                GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(graphicsScrollPane,
                                GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
        );

        pack();
    }
}
