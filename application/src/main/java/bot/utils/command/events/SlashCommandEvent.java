package bot.utils.command.events;

import bot.utils.command.CommandClient;
import bot.utils.command.events.eventReply.MessageReplyAction;
import bot.utils.command.events.eventReply.SlashMessageReplyAction;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.SlashOptionValue;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.Arrays;

@Slf4j
@Getter
public class SlashCommandEvent implements CommandEvent {

	private final CommandClient client;
	private final SlashCommandInteractionEvent event;

	public SlashCommandEvent(SlashCommandInteractionEvent event, CommandClient client) {
		this.client = client;
		this.event = event;
	}

	@Override
	public OptionValue getOption(OptionName optionName) {
		return SlashOptionValue.builder()
						.optionName(optionName)
						.optionMapping(event.getOption(optionName.getDisplayName()))
						.build();
	}

	@Override
	public boolean optionPresent(OptionName optionName) {
		return event.getOption(optionName.getDisplayName()) != null;
	}

	@Override
	public void deferReply() {
		event.deferReply().queue();
	}

	@Override
	public void reply(String message) {
		if (event.isAcknowledged()) {
			event.getHook().sendMessage(message).queue();
		} else {
			event.reply(message).queue();
		}
	}

	@Override
	public void reply(MessageEmbed... embeds) {
		if (event.isAcknowledged()) {
			event.getHook().sendMessageEmbeds(Arrays.asList(embeds)).queue();
		} else {
			event.reply(MessageCreateData.fromEmbeds(embeds)).queue();
		}
	}

	@Override
	public MessageReplyAction replyCallback(final String content) {
		return new SlashMessageReplyAction(event.reply(content), event);
	}

	@Override
	public void reactSuccess() {
	}

	@Override
	public void reactSuccessOrReply(String reply) {
		this.reply(reply);
	}

	@Override
	public void reactError() {
	}

	@Override
	public ChannelType getChannelType() {
		return event.getChannelType();
	}

	@Override
	public GuildChannel getGuildChannel() {
		return null;
	}

	@Override
	public TextChannel getTextChannel() {
		return null;
	}

	@Override
	public MessageChannel getChannel() {
		return event.getMessageChannel();
	}

	@Override
	public User getUser() {
		return event.getUser();
	}

	@Override
	public User getAuthor() {
		return event.getUser();
	}

	@Override
	public Member getMember() {
		return event.getMember();
	}

	@Override
	public Message getMessage() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public JDA getJDA() {
		return event.getJDA();
	}

	@Override
	public MessageChannelUnion getMessageChannel() {
		return event.getChannel();
	}

	@Override
	public Guild getGuild() {
		return event.getGuild();
	}

	public Member getSelfMember() {
		return event.getGuild() == null ? null : event.getGuild().getSelfMember();
	}

	@Override
	public boolean isFromType(ChannelType type) {
		return type.equals(event.getChannelType());
	}

	/**
	 * Tests whether the {@link net.dv8tion.jda.api.entities.User User} who triggered this event is an
	 * owner of the bot.
	 *
	 * @return {@code true} if the User is the Owner, else {@code false}
	 */
	@Override
	public boolean isOwner() {
		if (event.getUser().getId().equals(this.getClient().getOwnerId())) return true;
		if (this.getClient().getCoOwnerIds() == null) return false;
		for (String id : this.getClient().getCoOwnerIds())
			if (id.equals(event.getUser().getId())) return true;
		return false;
	}
}
