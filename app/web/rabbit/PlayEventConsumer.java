package web.rabbit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectMapper.DefaultTyping;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.amqp.core.Message;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.errorhandling.NonFailingJsonMessageConverter;

import controllers.BeanProvider;

import play.jobs.OnApplicationStart;
//import play.modules.rabbitmq.consumer.RabbitMQConsumer;

//@OnApplicationStart(async=true)
public class PlayEventConsumer 
//extends RabbitMQConsumer<Event> 
{

//	private static Logger logger = Logger.getLogger(PlayEventConsumer.class.getName());
//	
//	@Override
//	protected void consume(Event event) {
//		logger.log(Level.INFO, "event received!" + event);
//	}
//
//	@Override
//	protected Class getMessageType() {
//		
//		
//		return Event.class;
//	}
//
//	@Override
//	protected String queue() {
//		return "dab.events";
//	}
//	
//	@Override
//	protected Event toObject(byte[] bytes) throws Exception {
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
//		return mapper.readValue(bytes, Event.class);
//	}

}
