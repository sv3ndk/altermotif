package com.svend.dab.core;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;

@Component
public class PhotoUtils {
	
	private static Logger logger = Logger.getLogger(PhotoUtils.class.getName());
	
	public static String JPEG_MIME_TYPE ="image/jpeg";

	
	@Autowired
	private Config config;

	
	public byte[] readPhotoContent(File photoContent) throws DabUploadFailedException{
		
		InputStream photoContentStream = null ;
		
		try {
			
			photoContentStream = new BufferedInputStream(new FileInputStream(photoContent));
			
			if (photoContentStream.available() == 0 || photoContentStream.available() > config.getMaxUploadedPhotoSizeInBytes()) {
				throw new DabUploadFailedException("Photo size is too big", failureReason.fileTooBig);
			}

			
			byte[] receivedPhoto = new byte[photoContentStream.available()];
			ByteStreams.readFully(photoContentStream, receivedPhoto);
			return receivedPhoto;

			

		} catch (IOException e) {
			throw new DabUploadFailedException("Could not upload photo" + failureReason.fileFormatIncorrectError, e);
		} finally {
			try {
				photoContentStream.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Could not close uploaded content stream", e);
			}
		}
	
	}
	
	
	
	/**
	 * @param photoContent
	 * @return
	 */
	public byte[] resizePhotoToTargetSize(byte[] photoContent, int targetMaxDimension) {

		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {
			in = new ByteArrayInputStream(photoContent);
			BufferedImage image = ImageIO.read(in);
			
			if (image == null) {
				throw new DabUploadFailedException("cannot read this image", failureReason.fileFormatIncorrectError);
			}

			double scaleCoef = computeCoef(image.getWidth(), image.getHeight(), targetMaxDimension);

			if (scaleCoef == 1d) {
				return photoContent;
			} else {

				
				int newWidth = (int) (image.getWidth() * scaleCoef);
				int newHeight = (int) (image.getHeight() * scaleCoef);

				int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
				BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);

				Graphics2D g = resizedImage.createGraphics();
				g.setComposite(AlphaComposite.Src);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(image, 0, 0, newWidth, newHeight, null);
				g.dispose();
				
				baos = new ByteArrayOutputStream();
				ImageIO.write( resizedImage, "jpg", baos );
				
				return baos.toByteArray();

			}

		} catch (IOException e) {
			throw new DabUploadFailedException("", failureReason.technicalError, e);
			
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while trying to close stream, ignoring...", e);
				}
			}

			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while trying to close stream, ignoring...", e);
				}
			}

		}
	}

	/**
	 * @param width
	 * @param height
	 * @param targetMaxDimension
	 * @return
	 */
	public double computeCoef(int width, int height, int targetMaxDimension) {

		if (width == 0 || height == 0) {
			// not scaling a 0 sized image
			return 1d;
		}

		if (width > targetMaxDimension || height > targetMaxDimension) {

			if (width > height) {
				return (double) targetMaxDimension / width;
			} else {
				return (double) targetMaxDimension / height;
			}

		} else {
			return 1d;
		}
	}



	public void setConfig(Config config) {
		this.config = config;
	}
}
