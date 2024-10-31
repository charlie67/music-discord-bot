package bot.configuration;


import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EventWaiterConfig {
	
	@Bean
	public EventWaiter eventWaiter() {
		return new EventWaiter();
	}
}
