package bot.utils.command.events.eventReply;

import bot.utils.command.events.SlashCommandEvent;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

@RequiredArgsConstructor
public class SlashMessageReplyAction implements MessageReplyAction {

	private final ReplyCallbackAction replyCallbackAction;

	private final SlashCommandInteractionEvent event;

	@Override
	public void queue() {
		if (event.isAcknowledged()) {
			event.getHook().sendMessage(replyCallbackAction.getContent()).queue();
		} else {
			replyCallbackAction.queue();
		}
	}

	@Override
	public MessageReplyAction addContent(final String content) {
		replyCallbackAction.addContent(content);
		return this;
	}
}
