package main.content;

import main.entity.type.ObjType;

import java.awt.event.MouseEvent;

public interface ValueEditor {

    boolean checkClickProcessed(MouseEvent e, ObjType selectedType, VALUE val, String value);

}
