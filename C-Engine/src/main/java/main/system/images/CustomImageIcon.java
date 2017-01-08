package main.system.images;

import javax.swing.*;

/**
 * Created by JustMe on 1/8/2017.
 */
public class CustomImageIcon extends ImageIcon{
    public String imgPath;

    public CustomImageIcon(String s ){
        super(s);
        imgPath = s.toLowerCase(). replace(ImageManager.getPATH().toLowerCase(), "");

    }
}
