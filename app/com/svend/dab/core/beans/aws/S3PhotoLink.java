package com.svend.dab.core.beans.aws;

import com.svend.dab.core.beans.profile.ExternalLink;


/**
 * {@link S3Link} specific for photos: we need to keeep a reference of the "shortkey" and the "prefix"
 * 
 * @author Svend
 *
 */
public class S3PhotoLink extends S3Link{

	// end of the s3 key (without the prefix)
	private final String photoS3ShortKey;
	
	// prefix to add to photoS3ShortKey in order to obtain what s3 considers as "the key"
	private final String s3PhotoKeyPrefix;
	
	public S3PhotoLink(S3Link photoLink,  String photoS3ShortKey, String s3PhotoKeyPrefix) {
		super(photoLink);
		this.photoS3ShortKey = photoS3ShortKey;
		this.s3PhotoKeyPrefix = s3PhotoKeyPrefix;
	}

	
	public String getFullS3Key() {
		return s3PhotoKeyPrefix + photoS3ShortKey;
	}
	

	public String getS3PhotoKeyPrefix() {
		return s3PhotoKeyPrefix;
	}

	public String getPhotoS3ShortKey() {
		return photoS3ShortKey;
	}


	@Override
	public ExternalLink clone() {
		return new S3PhotoLink(this, photoS3ShortKey, s3PhotoKeyPrefix);
	}
	

}