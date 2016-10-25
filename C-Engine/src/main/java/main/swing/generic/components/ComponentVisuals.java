package main.swing.generic.components;

import javax.swing.*;
import java.awt.*;

public interface ComponentVisuals {

    Dimension getSize();

    String getImgPath();

    Image getImage();

    JLabel getLabel();

    int getWidth();

    int getHeight();

}
