package org.arig.robot.utils;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * The Class ImageUtils.
 * 
 * @author GregoryDepuille
 */
public final class ImageUtils {

	public static BufferedImage mirrorX(BufferedImage img) {
        // Appliquation d'un mirroir horizontale sur l'image. Le repère robot est différent du repère informatique.
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -img.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
	}
}
