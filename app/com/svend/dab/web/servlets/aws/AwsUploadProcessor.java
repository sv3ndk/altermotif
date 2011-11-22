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

import com.svend.dab.core.IProfilePhotoService;
import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.web.servlets.DabUploadFailedException;
import com.svend.dab.web.servlets.DabUploadFailedException.failureReason;
import com.svend.dab.web.servlets.IUploadProcessor;
import com.svend.dab.web.servlets.UPLOAD_TYPE;
import com.svend.dab.web.servlets.UploadRequest;

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
	private IProfilePhotoService profilePhotoService;

	@Autowired
	private Config config;

	// -----------------------------------
	// IStore interface
	// -----------------------------------

	@Override
	// public void processUploadRequest(UploadRequest uploadRequest) {
	public void processUploadRequest(File theFile, String uploadtype, String username) {

		if (theFile == null || uploadtype == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		UPLOAD_TYPE ut = null;
		try {
			ut = UPLOAD_TYPE.valueOf(uploadtype);
		} catch (IllegalArgumentException e) {
			throw new DabUploadFailedException("Could not parse incoming upload request: unknown upload type: " + uploadtype, failureReason.fileFormatIncorrectError, e);
		}

		switch (ut) {
		case CV:
			processUploadCvRequest(theFile, username);
			break;

		case PHOTO:
			processUploadPhotoRequest(theFile, username);
			break;

		default:
			throw new DabUploadFailedException("Warning : unrecognized upload type:" + ut, failureReason.technicalError);
		}

	}

	// -----------------------------------
	// Photo upload

	/**
	 * processing of a photo upload request
	 * 
	 * @param uploadRequest
	 */
	private void processUploadPhotoRequest(File theFile, String username) {

		// TODO
		// profilePhotoService.addOnePhoto(profile, readAndCloseStream(uploadRequest.getStream(), config.getMaxUploadedPhotoSizeInBytes()));
	}

	/**
	 * processing of a CV upload request
	 * 
	 * @param uploadRequest
	 */
	private void processUploadCvRequest(File theFile, String username) {

		UserProfile profile = userProfileService.loadUserProfile(username, false);

		// TODO: pass directly stream to s3 client ... (but how to check for magic number then?)
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
	private UserProfile validatePermKeyAndGetProfile(UploadRequest uploadRequest) {

		if (uploadRequest.getUploadPermKey() == null || uploadRequest.getUsername() == null || uploadRequest.getStream() == null) {
			throw new DabUploadFailedException(failureReason.technicalError);
		} else {

			UserProfile profile = userProfileService.loadUserProfile(uploadRequest.getUsername(), false);

			if (profile == null) {
				throw new DabUploadFailedException(failureReason.userNotFound);
			}

			if (!uploadRequest.getUploadPermKey().equals(profile.getUploadPermKey())) {

				logger.log(Level.WARNING, "upload key mismatch : " + uploadRequest.getUploadPermKey() + " != " + profile.getUploadPermKey() + " => waiting a bit more...");

				// TODO: exponential back-off here..
				try {
					Thread.sleep(WAIT_PERIOD_IF_FAILED_PERM_KEY_IN_MILLS);
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "interrupted while sleeping (was retrying for )!?", e);
				}

				profile = userProfileService.loadUserProfile(uploadRequest.getUsername(), false);

				if (!uploadRequest.getUploadPermKey().equals(profile.getUploadPermKey())) {
					throw new DabUploadFailedException("cannot upload CV file: upload key mismatch : " + uploadRequest.getUploadPermKey() + " != " + profile.getUploadPermKey(),
							failureReason.securityError);
				}
			}

			return profile;

		}
	}

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