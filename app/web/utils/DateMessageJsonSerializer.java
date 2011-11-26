package web.utils;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * JSON serializser used when we send JSON in the Play REST response that contains date that shoudl be displayed in the mesasges screens
 * 
 * @author svend
 *
 */
public class DateMessageJsonSerializer implements JsonSerializer<Date> {

	public JsonElement serialize(final Date date, Type arg1, JsonSerializationContext arg2) {
		return new JsonPrimitive(Utils.formatDateWithTime(date));
	}
};