package bot.utils.command.events;

import bot.utils.command.CommandClient;
import bot.utils.command.option.OptionName;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;

public interface CommandEvent {

	/**
	 * Splits a String into one or more Strings who's length does not exceed 2000 characters. <br>
	 * Also nullifies usages of {@code @here} and {@code @everyone} so that they do not mention
	 * anyone. <br>
	 * Useful for splitting long messages so that they can be sent in more than one {@link
	 * net.dv8tion.jda.api.entities.Message Message} at maximum potential length.
	 *
	 * @param stringtoSend The String to split and send
	 * @return An {@link java.util.ArrayList ArrayList} containing one or more Strings, with nullified
	 * occurrences of {@code @here} and {@code @everyone}, and that do not exceed 2000 characters
	 * in length
	 */
	default ArrayList<String> splitMessage(String stringtoSend) {
		ArrayList<String> msgs = new ArrayList<>();
		if (stringtoSend != null) {
			stringtoSend =
							stringtoSend.replace("@everyone", "@\u0435veryone").replace("@here", "@h\u0435re").trim();
			while (stringtoSend.length() > 2000) {
				int leeway = 2000 - (stringtoSend.length() % 2000);
				int index = stringtoSend.lastIndexOf("\n", 2000);
				if (index < leeway) index = stringtoSend.lastIndexOf(" ", 2000);
				if (index < leeway) index = 2000;
				String temp = stringtoSend.substring(0, index).trim();
				if (!temp.equals("")) msgs.add(temp);
				stringtoSend = stringtoSend.substring(index).trim();
			}
			if (!stringtoSend.equals("")) msgs.add(stringtoSend);
		}
		return msgs;
	}

	OptionMapping getOption(OptionName optionName);

	boolean optionPresent(OptionName optionName);

	void deferReply();

	void reply(String message);

	void reply(MessageEmbed... embed);

	ReplyCallbackAction replyCallback(String message);

	void reactSuccess();

	void reactSuccessOrReply(String reply);

	void reactError();

	MessageChannelUnion getMessageChannel();

	Guild getGuild();

	ChannelType getChannelType();

	GuildChannel getGuildChannel();

	TextChannel getTextChannel();

	MessageChannel getChannel();

	User getUser();

	JDA getJDA();

	CommandClient getClient();

	Member getSelfMember();

	Member getMember();

	Message getMessage();

	boolean isFromType(ChannelType type);

	boolean isOwner();

	default boolean isFromGuild() {
		return isFromType(ChannelType.TEXT);
	}
}
