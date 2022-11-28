package net.fradantim.platformertutorial.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageUtil {

	private static final Map<String, Integer> IMAGES_HEIGHT = new HashMap<>();
	
	private ImageUtil() {
		super();
	}

	public static BufferedImage getImage(String imagePath) {
		try {
			return ImageIO.read(ImageUtil.class.getClassLoader().getResourceAsStream(imagePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Integer getImageHeight(String imagePath) {
		Integer height = IMAGES_HEIGHT.get(imagePath);
		
		if (height != null)
			return height;
		
		synchronized (ImageUtil.class) {
			height = IMAGES_HEIGHT.get(imagePath);
			
			if (height != null)
				return height;
			
			height = getImage(imagePath).getHeight();
			IMAGES_HEIGHT.put(imagePath, height);
		}
		
		return height;
	}

}
