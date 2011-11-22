package com.svend.dab.dao.aws.s3;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.aws.S3Link;

/**
 * @author Svend
 * 
 *         Handy tool for handling AWS S3 objects and interfaces
 * 
 */
@Component
public class AwsS3Tool implements Serializable {

	private static final long serialVersionUID = -4694502839373437468L;

	private static Logger logger = Logger.getLogger(AwsS3Tool.class.getName());

	/**
	 * @param bucket
	 * @param key
	 * @param expiration
	 * @return
	 */
	public S3Link generatePreSignedUrl(String bucket, String key, Calendar expiration) {
		if (bucket == null || key == null) {
			return null;
		} else {

			try {
				AmazonS3 s3 = buildS3Client();
				return new S3Link(s3.generatePresignedUrl(bucket, key, expiration.getTime()).toExternalForm(), key, bucket);

			} catch (Exception e) {
				throw new DabS3AccessException("Could not generate pre-signed URL, returning null", e);
			}
		}
	}

	/**
	 * @param bucket
	 * @param key
	 * @param stream
	 */
	public void uploadBinary(String bucket, String key, byte[] bytes, String contentType) {

		InputStream stream = null;

		try {
			AmazonS3 s3 = buildS3Client();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			metadata.setHeader(Headers.CONTENT_LENGTH, new Long(bytes.length));

			stream = new BufferedInputStream(new ByteArrayInputStream(bytes));

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, stream, metadata);

			s3.putObject(putObjectRequest);

		} catch (Exception e) {
			throw new DabS3AccessException("Could not upload binary content to S3", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "problem while trying to close stream ", e);
				}
			}
		}
	}

	/**
	 * @param cvLink
	 * @param cvContent
	 * @param pDF_MIME_TYPE
	 */
	public void uploadBinary(S3Link s3Link, byte[] cvContent, String contentType) {
		uploadBinary(s3Link.getS3BucketName(), s3Link.getS3Key(), cvContent, contentType);
	}

	/**
	 * @param s3BucketName
	 * @param sS3Key
	 * @param contentStream
	 * @param contentType
	 */
	public void uploadBinaryStreamAndClose(String s3BucketName, String sS3Key, InputStream contentStream, String contentType, long contentLength) {
		try {
			AmazonS3 s3 = buildS3Client();
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			metadata.setContentLength(contentLength);
			// metadata.setHeader(Headers.CONTENT_LENGTH, new Long(contentStream.));
			PutObjectRequest putObjectRequest = new PutObjectRequest(s3BucketName, sS3Key, contentStream, metadata);
			s3.putObject(putObjectRequest);
			contentStream.close();
		} catch (Exception e) {
			throw new DabS3AccessException("Could not upload binary content to S3", e);
		}

	}

	/**
	 * @param bucket
	 * @param key
	 */
	public void removeBinary(String bucket, String key) {

		if (bucket == null) {
			throw new DabPreConditionViolationException("Cannot remove binary: null bucket");
		}

		if (key == null) {
			throw new DabPreConditionViolationException("Cannot remove binary: null key");
		}

		try {
			AmazonS3 s3 = buildS3Client();
			s3.deleteObject(bucket, key);

		} catch (Exception e) {
			throw new DabS3AccessException("Could not remove binary content from S3", e);
		}
	}

	public static AmazonS3 buildS3Client() throws IOException {
		// TODO: is this thread safe? (can I make it a class member?)
		return new AmazonS3Client(new PropertiesCredentials(AwsS3Tool.class.getResourceAsStream("AwsCredentials.properties")));
	}

}
