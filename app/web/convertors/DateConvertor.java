package web.convertors;

import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.BeanProvider;

public class DateConvertor {

	/**
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			return "";
		}
		
		return new SimpleDateFormat(BeanProvider.getConfig().getDateDisplayFormat()).format(date);
	}
	
}
