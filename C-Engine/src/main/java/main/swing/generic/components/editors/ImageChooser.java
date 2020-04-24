package main.swing.generic.components.editors;

import main.content.DC_TYPE;
import main.data.filesys.PathFinder;
import main.system.PathUtils;
import main.system.images.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class ImageChooser extends FileChooser {

    private int size;

    public ImageChooser() {
        this(PathFinder.getImagePath());
    }

    public ImageChooser(String path) {
        super();
        setDefaultFileLocation(path);
    }

    @Override
    protected String cropPrefix(String selected) {
        return PathUtils.cropImagePath(selected);
    }

    @Override
    protected boolean checkFile(String fileLocation) {
        return ImageManager.getImage(fileLocation) != null;
    }

    @Override
    public void launch(JTable table, int row, int column, String v, MouseEvent e) {
        size = 64;
        if (table.getName() != null) {
            boolean big = table.getName().equals(DC_TYPE.CHARS.getName())
             || table.getName().equals(DC_TYPE.UNITS.getName());
            boolean small = table.getName().equals(DC_TYPE.ABILS.getName())
             || table.getName().equals(DC_TYPE.BUFFS.getName());

            if (big) {
                size = 128;
            } else if (small) {
                size = 32;
            }

        }
        addPreview();

        super.launch(table, row, column, v, e);
    }

    private void addPreview() {
        FileChooserImagePreview preview = new FileChooserImagePreview(size);
        fc.setAccessory(preview);
        fc.addPropertyChangeListener(preview);
    }

    @Override
    public String launch(String v, Component parent) {
        size = 128;
        addPreview();
        return super.launch(v, parent);
    }

    public static class FileChooserImagePreview extends JPanel implements PropertyChangeListener {

        private int width, height;
        private Image image;
        private int size;
        private Color bg;

        public FileChooserImagePreview(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, -1));
            bg = getBackground();
        }

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();

            // Make sure we are responding to the right event.
            if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
                File selection = (File) e.getNewValue();
                String name;

                if (selection == null) {
                    return;
                } else {
                    name = selection.getAbsolutePath();
                }

				/*
                 * Make reasonably sure we have an image format that AWT can
				 * handle so we don't try to draw something silly.
				 */
                if (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".gif") || name.toLowerCase().endsWith(".png")) {
                    ImageIcon icon = new ImageIcon(name);
                    image = icon.getImage();
                    scaleImage();
                    repaint();
                }
            }
        }

        private void scaleImage() {
            width = image.getWidth(this);
            height = image.getHeight(this);
            if (!(width > size || height > size)) {
                image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return;
            }

            double ratio = 1.0;

			/*
             * Determine how to scale the image. Since the accessory can expand
			 * vertically make sure we don't go larger than 150 when scaling
			 * vertically.
			 */
            if (width >= height) {
                ratio = (double) (size - 5) / width;
                width = size - 5;
                height = (int) (height * ratio);
            } else {
                if (getHeight() > 150) {
                    ratio = (double) (size - 5) / height;
                    height = size - 5;
                    width = (int) (width * ratio);
                } else {
                    ratio = (double) getHeight() / height;
                    height = getHeight();
                    width = (int) (width * ratio);
                }
            }

            image = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.setColor(bg);

			/*
             * If we don't do this, we will end up with garbage from previous
			 * images if they have larger sizes than the one we are currently
			 * drawing. Also, it seems that the file list can paint outside of
			 * its rectangle, and will cause odd behavior if we don't clear or
			 * fill the rectangle for the accessory before drawing. This might
			 * be a bug in JFileChooser.
			 */
            g.fillRect(0, 0, size, getHeight());
            g.drawImage(image, getWidth() / 2 - width / 2 + 5, getHeight() / 2 - height / 2, this);
        }

    }

}
