package main.level_editor.backend.metadata.decor;

public interface IDecorHandler {

    /*
    rotate? quick-adjust? flip?

    toggle copy-paste layer (should work for delete/etc

    script helper with available imgs, templates, and colors

    number scale

    alignment!

     */

    void clear();

    void copy();

    void paste();

    void cut();

    void edit();
}
