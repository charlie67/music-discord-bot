package bot.utils.command.events.eventReply;

public interface MessageReplyAction {
	void queue();

	MessageReplyAction addContent(String content);
}
