package bot.utils.command.events.eventReply;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@AllArgsConstructor
public class TextMessageReplyAction implements MessageReplyAction {

	private String message = "";

	private final MessageChannel messageChannel;

	@Override
	public void queue() {
		messageChannel.sendMessage(message).queue();
	}

	@Override
	public MessageReplyAction addContent(String content) {
		message += content;
		return this;
	}
}
