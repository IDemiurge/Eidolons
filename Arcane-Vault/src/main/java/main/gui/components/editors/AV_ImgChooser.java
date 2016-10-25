package main.gui.components.editors;

import main.swing.generic.components.editors.ImageChooser;
import main.system.images.ImageManager;

public class AV_ImgChooser extends ImageChooser {

	private String defaultImageLocation;

	public AV_ImgChooser() {

	}

	public AV_ImgChooser(String defaultImageLocation) {
		this.defaultImageLocation = defaultImageLocation;
	}

	@Override
	protected String getDefaultFileLocation() {
		if (defaultImageLocation != null)
			return ImageManager.getDefaultImageLocation() + defaultImageLocation;
		return ImageManager.getDefaultImageLocation();
	}
}
