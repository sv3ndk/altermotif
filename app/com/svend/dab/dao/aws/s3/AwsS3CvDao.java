package com.svend.dab.dao.aws.s3;


import static com.svend.dab.core.beans.DabUploadFailedException.failureReason.fileFormatIncorrectError;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.ExternalLink;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.dao.ICvBinaryDao;
import com.svend.dab.core.beans.DabUploadFailedException;

/**
 * @author Svend
 *
 */
@Component
public class AwsS3CvDao implements ICvBinaryDao {

	// magic number identifying a pdf
	public static byte[] PDF_MN = { 0x25, 0x50, 0x44, 0x46 };
	public static String PDF_MIME_TYPE = "application/pdf";
	
	// key used to store CV in AWS S3 (same CV key for all profiles:
	// profiles/<username>/cv/cv1 )
	public final static String cv_key_part1 = "profiles/";
	public final static String cv_key_part2 = "/cv/cv1";
	
	// all binaries are in the same bucket for the moment
	public static String DEFAULT_S3_BUCKET = "dab";
	
	// -------------------------------------
	// helper beans
	
	private static Logger logger = Logger.getLogger(AwsS3CvDao.class.getName());
	
	@Autowired
	private AwsS3Tool awsS3Tool;
	
	
	// -------------------------------------
	// -------------------------------------
	
	

	
	@Override
	public void uploadCvPdf(UserProfile profile, byte[] cvContent) {
		if (profile == null || profile.getUsername() == null ) {
			throw new DabPreConditionViolationException("Cannot upload CV: null user or user with null username");
		}
		
		if (cvContent == null || cvContent.length < 4) {
			// this file is too small to be a valid PDF
			throw new DabUploadFailedException(fileFormatIncorrectError);
		}

		if (cvContent[0] != PDF_MN[0] || cvContent[1] != PDF_MN[1] || cvContent[2] != PDF_MN[2] || cvContent[3] != PDF_MN[3]) {
			throw new DabUploadFailedException(fileFormatIncorrectError);
		}

		
		S3Link cvLink = profile.getCvLink();
		if (cvLink == null) {
			cvLink = new S3Link(null, cv_key_part1 + profile.getUsername() + cv_key_part2, DEFAULT_S3_BUCKET);
		} 
		profile.setCvLink(cvLink);
		
		S3Link s3Link = (S3Link) cvLink;
		awsS3Tool.uploadBinary(s3Link, cvContent, PDF_MIME_TYPE);
	}
	
	
//	/**
//	 * @param editedUserProfile
//	 * @param removedPhotoKey
//	 */
//	@Override
//	public void removeCv(S3Link cvLink) {
//		
//		if (cvLink == null || cvLink.getS3BucketName() == null || cvLink.getS3Key() == null) {
//			logger.log(Level.WARNING, "Refusing to remove a null CV , not doing anything");
//			return;
//		}
//		
//		awsS3Tool.removeBinary(cvLink.getS3BucketName(), cvLink.getS3Key());
//		
//	}




}
