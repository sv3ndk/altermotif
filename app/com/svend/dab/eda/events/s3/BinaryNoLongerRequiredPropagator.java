package com.svend.dab.eda.events.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabIllegalFormatException;
import com.svend.dab.dao.aws.s3.AwsS3Tool;
import com.svend.dab.eda.IEventPropagator;

@Component
public class BinaryNoLongerRequiredPropagator implements IEventPropagator<BinaryNoLongerRequiredEvent> {

	@Autowired
	private AwsS3Tool awsS3Tool;
	
	@Override
	public void propagate(BinaryNoLongerRequiredEvent event) throws DabException {
		
		if (event == null || event.getBinaryBucket() == null || event.getBinaryKey() == null) {
			throw new DabIllegalFormatException("cannot propagate a BinaryNoLongerRequiredEvent event: null event or null binary key or null bucket name");
		}
		
		awsS3Tool.removeBinary(event.getBinaryBucket(), event.getBinaryKey());
	}

}
