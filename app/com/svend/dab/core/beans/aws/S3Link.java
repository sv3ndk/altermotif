package com.svend.dab.core.beans.aws;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.services.s3.AmazonS3;
import com.svend.dab.core.beans.profile.ExternalLink;
import com.svend.dab.dao.aws.s3.AwsS3Tool;
import com.svend.dab.dao.aws.s3.DabS3AccessException;

/**
 * @author Svend
 * 
 */
public class S3Link extends ExternalLink implements Serializable {

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 122311502796103269L;

	private String s3Key;

	private String s3BucketName;

	private static Logger logger = Logger.getLogger(S3Link.class.getName());

	// ----------------------------
	// ----------------------------

	public S3Link() {
		super();
	}

	public S3Link(S3Link copied) {
		super(copied.getAddress());
		s3Key = copied.s3Key;
		s3BucketName = copied.s3BucketName;
	}

	public S3Link(String link, String s3Key, String s3BucketName) {
		super(link);
		this.s3Key = s3Key;
		this.s3BucketName = s3BucketName;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj == null) {
			return false;
		}
		
		S3Link otherLink = (S3Link) obj;
		
		boolean isS3KeyEqual = false;
		boolean isS3BucketNameEqual = false;
		
		if (s3Key == null) {
			isS3KeyEqual = (otherLink.getS3Key() == null);
		} else {
			isS3KeyEqual = s3Key.equals(otherLink.getS3Key());
		}

		if (s3BucketName == null) {
			isS3BucketNameEqual = (otherLink.getS3BucketName() == null);
		} else {
			isS3BucketNameEqual = s3BucketName.equals(otherLink.getS3BucketName());
		}
		
		return isS3KeyEqual && isS3BucketNameEqual;
	}
	
	
	// ----------------------------
	// ----------------------------

	@Override
	public void generateUrl(Date expirationdate) {

		if (expirationdate != null && s3BucketName != null && s3Key != null) {

			try {
				AmazonS3 s3Client = AwsS3Tool.buildS3Client();
				super.setUrl(s3Client.generatePresignedUrl(s3BucketName, s3Key, expirationdate).toExternalForm());
			} catch (IOException e) {
				throw new DabS3AccessException("could not prepare pre-signed link to dv", e);
			}
		} else {
			logger.log(Level.WARNING, "Not generating a link: null key or null buckeet name or null expieration date");
		}

	}
	@Override
	public String toString() {
		
		return "key: " + s3Key + " bucket: " + s3BucketName;
	}

	// ----------------------------
	// ----------------------------


	public String getS3Key() {
		return s3Key;
	}

	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
	}

}
