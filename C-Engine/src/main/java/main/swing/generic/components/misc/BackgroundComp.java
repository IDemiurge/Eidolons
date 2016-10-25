package main.swing.generic.components.misc;

import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;

public class BackgroundComp extends JLabel {

    public BackgroundComp(String path, Dimension size) {
        super(ImageManager.getSizedIcon(path, size));
    }


}
