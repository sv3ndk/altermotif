package com.svend.dab.web.servlets.aws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.web.upload.IUploadProcessor;

/**
 * This receives a parsed upload request and calls the appropriate business logic operations depending on the content
 * 
 * @author Svend
 * 
 */
@Component("AwsUploadProcessor")
public class AwsUploadProcessor implements IUploadProcessor {

	// -----------------------------------
	// config

	public static int WAIT_PERIOD_IF_FAILED_PERM_KEY_IN_MILLS = 2500;

	public static int READ_CHUNK_SIZE = 1024 * 10;

	// -----------------------------------
	// helper beans

	private static Logger logger = Logger.getLogger(AwsUploadProcessor.class.getName());

	@Autowired
	private IUserProfileService userProfileService;


	@Autowired
	private Config config;

	// -----------------------------------
	// IStore interface
	// -----------------------------------
	// -----------------------------------
	// Photo upload

	/**
	 * processing of a CV upload request
	 * 
	 * @param uploadRequest
	 */

	
	public void processUploadCvRequest(File theFile, String username) {

		if (theFile == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		UserProfile profile = userProfileService.loadUserProfile(username, false);

		if (profile == null) {
			throw new DabUploadFailedException("cannot process: no user profile found for  " + username, failureReason.technicalError);
		}

		try {
			userProfileService.updateCv(profile, readAndCloseStream(new FileInputStream(theFile), config.getMaxUploadedCVSizeInBytes()));
		} catch (FileNotFoundException e) {
			throw new DabUploadFailedException("Could not upload cv" + failureReason.fileFormatIncorrectError, e);
		}
	}

	/**
	 * @param uploadRequest
	 * 
	 *            TODO: retrieve the permkey, and the s3 data are enough at this level... => would be faster
	 * 
	 * @return
	 */
	/**
	 * @param stream
	 * @param maxStreamSizeInBytes
	 * @return
	 */
	private byte[] readAndCloseStream(InputStream stream, long maxStreamSizeInBytes) {

		if (stream == null) {
			throw new DabUploadFailedException("Could not read cv input stream: null stream", failureReason.technicalError);
		}

		final ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] buf = new byte[READ_CHUNK_SIZE];
		try {
			while (stream.read(buf) != -1) {
				bao.write(buf);
				if (bao.size() > maxStreamSizeInBytes) {
					throw new DabUploadFailedException("Uploaded file is bigger then " + maxStreamSizeInBytes, failureReason.fileTooBig);
				}
			}
			return bao.toByteArray();

		} catch (IOException e) {
			throw new DabUploadFailedException("Could not read uploaded input stream", failureReason.technicalError, e);
		} finally {

			if (stream != null) {

				try {
					stream.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "could not close stream (giving up...)", e);
				}
			}

			try {

				bao.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "could not close stream (giving up...)", e);
			}
		}
	}
}