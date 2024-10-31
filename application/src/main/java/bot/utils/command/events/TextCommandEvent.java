package bot.utils.command.events;

import bot.utils.command.CommandClient;
import bot.utils.command.CommandClientBuilder;
import bot.utils.command.events.eventReply.MessageReplyAction;
import bot.utils.command.events.eventReply.TextMessageReplyAction;
import bot.utils.command.impl.CommandClientImpl;
import bot.utils.command.option.Option;
import bot.utils.command.option.Response;
import bot.utils.command.option.optionValue.OptionValue;
import bot.utils.command.option.optionValue.TextOptionValue;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class TextCommandEvent implements CommandEvent {
	public static int MAX_MESSAGES = 2;

	private final MessageReceivedEvent event;
	private final String prefix;
	private final CommandClient client;
	private final Map<Response, OptionValue> optionMap;
	private String args;

	public TextCommandEvent(
					MessageReceivedEvent event,
					String prefix,
					String args,
					CommandClient client,
					List<Option> options) {
		this.event = event;
		this.prefix = prefix;
		this.args = args;
		this.client = client;
		this.optionMap = new HashMap<>();

		if (options.isEmpty()) {
			return;
		} else if (options.size() == 1) {
			Option option = options.getFirst();
			OptionValue optionValue = TextOptionValue.builder()
							.optionName(option.optionName())
							.jda(event.getJDA())
							.event(event)
							.optionValue(args)
							.build();
			optionMap.put(option.optionName(), optionValue);
		} else {
			options.sort(Comparator.comparingInt(Option::position));

			String[] split = args.split(" ");
			for (int i = 0; i < split.length; i++) {
				final String arg = split[i];

				Option chosenOption = options.get(i);
			}
		}
	}

	void setArgs(String args) {
		this.args = args;
	}

	// functional calls

	/**
	 * Links a {@link net.dv8tion.jda.api.entities.Message Message} with the calling Message contained
	 * by this CommandEvent.
	 *
	 * <p>This method is exposed for those who wish to use linked deletion but may require usage of
	 * {@link MessageChannel#sendMessage(MessageCreateData) MessageChannel#sendMessage()} or for other
	 * reasons cannot use the standard {@code reply()} methods.
	 *
	 * <p>If the Message provided is <b>not</b> from the bot (IE: {@link
	 * net.dv8tion.jda.api.entities.SelfUser SelfUser}), an {@link java.lang.IllegalArgumentException
	 * IllegalArgumentException} will be thrown.
	 *
	 * @param message The Message to add, must be from the SelfUser while linked deletion is being
	 *                used.
	 * @throws java.lang.IllegalArgumentException If the Message provided is not from the bot.
	 */
	public void linkId(Message message) {
		Checks.check(
						message.getAuthor().equals(getSelfUser()),
						"Attempted to link a Message who's author was not the bot!");
		((CommandClientImpl) client).linkIds(event.getMessageIdLong(), message);
	}

	public void reply(String message) {
		sendMessage(event.getChannel(), message);
	}

	@Override
	public void reply(MessageEmbed... embed) {
		event.getChannel().sendMessageEmbeds(embed[0], Arrays.copyOfRange(embed, 1, embed.length)).queue();
	}

	@Override
	public void reply(EmbedPaginator buttonMenu) {
		buttonMenu.display(event.getChannel());
	}

	@Override
	public MessageReplyAction replyCallback(final String content) {
		return new TextMessageReplyAction(content, event.getChannel());
	}

	/**
	 * Replies with a String message and then queues a {@link java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages. <br>
	 * The Consumer will be applied to the last message sent if this occurs.
	 *
	 * @param message A String message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 */
	public void reply(String message, Consumer<Message> success) {
		sendMessage(event.getChannel(), message, success);
	}

	/**
	 * Replies with a String message and then queues a {@link java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages. <br>
	 * Either Consumer will be applied to the last message sent if this occurs.
	 *
	 * @param message A String message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void reply(String message, Consumer<Message> success, Consumer<Throwable> failure) {
		sendMessage(event.getChannel(), message, success, failure);
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * @param embed The MessageEmbed to reply with
	 */
	public void reply(MessageEmbed embed) {
		event
						.getChannel()
						.sendMessageEmbeds(embed)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
										});
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} and then queues a
	 * {@link java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * @param embed   The MessageEmbed to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 */
	public void reply(MessageEmbed embed, Consumer<Message> success) {
		event
						.getChannel()
						.sendMessageEmbeds(embed)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
											success.accept(m);
										});
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} and then queues a
	 * {@link java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * @param embed   The MessageEmbed to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void reply(MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure) {
		event
						.getChannel()
						.sendMessageEmbeds(embed)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
											success.accept(m);
										},
										failure);
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * @param message The Message to reply with
	 */
	public void reply(MessageCreateData message) {
		event
						.getChannel()
						.sendMessage(message)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
										});
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message} and then queues a {@link
	 * java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#success()} with the provided
	 * Consumer as it's success callback.
	 *
	 * @param message The Message to reply with
	 * @param success The Consumer to success after sending the Message is sent.
	 */
	public void reply(MessageCreateData message, Consumer<Message> success) {
		event
						.getChannel()
						.sendMessage(message)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
											success.accept(m);
										});
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message} and then queues a {@link
	 * java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * @param message The Message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void reply(
					MessageCreateData message, Consumer<Message> success, Consumer<Throwable> failure) {
		event
						.getChannel()
						.sendMessage(message)
						.queue(
										m -> {
											if (event.isFromType(ChannelType.TEXT)) linkId(m);
											success.accept(m);
										},
										failure);
	}

	/**
	 * Replies with a {@link java.io.File} with the provided name, or a default name if left null.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p>This method uses {@link MessageChannel#sendFiles(FileUpload...)
	 * MessageChannel#sendFile(FileUpload...)} to send the File. For more information on what a bot
	 * may send using this, you may find the info in that method.
	 *
	 * @param file     The File to reply with
	 * @param filename The filename that Discord should display (null for default).
	 */
	public void reply(File file, String filename) {
		event.getChannel().sendFiles(FileUpload.fromData(file, filename)).queue();
	}

	/**
	 * Replies with a String message and a {@link java.io.File} with the provided name, or a default
	 * name if left null.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p>This method uses {@link MessageChannel#sendFiles(FileUpload...)
	 * MessageChannel#sendFile(FileUpload...)} to send the File. For more information on what a bot
	 * may send using this, you may find the info in that method.
	 *
	 * @param message  A String message to reply with
	 * @param file     The File to reply with
	 * @param filename The filename that Discord should display (null for default).
	 */
	public void reply(String message, File file, String filename) {
		event.getChannel().sendFiles(FileUpload.fromData(file, filename)).addContent(message).queue();
	}

	/**
	 * Replies with a formatted String message using the provided arguments.
	 */
	public void replyFormatted(String format, Object... args) {
		sendMessage(event.getChannel(), String.format(format, args));
	}

	@Override
	public OptionValue getOption(Response optionName) {
		return optionMap.get(optionName);
	}

	@Override
	public boolean optionPresent(Response optionName) {
		return optionMap.containsKey(optionName);
	}

	@Override
	public void deferReply() {
		// send the typing event to the channel
		event.getChannel().sendTyping().queue();
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} if possible, or
	 * just a String message if it cannot send the embed.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p><b>NOTE:</b> This alternate String message can exceed the 2000 character cap, and will be
	 * sent in two split Messages.
	 *
	 * @param embed            The MessageEmbed to reply with
	 * @param alternateMessage A String message to reply with if the provided MessageEmbed cannot be
	 *                         sent
	 */
	public void replyOrAlternate(MessageEmbed embed, String alternateMessage) {
		try {
			event.getChannel().sendMessageEmbeds(embed).queue();
		} catch (PermissionException e) {
			reply(alternateMessage);
		}
	}

	/**
	 * Replies with a String message and a {@link java.io.File} with the provided name, or a default
	 * name if left null.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p>This method uses {@link MessageChannel#sendFiles(FileUpload...)
	 * MessageChannel#sendFile(FileUpload...)} to send the File. For more information on what a bot
	 * may send using this, you may find the info in that method.
	 *
	 * <p><b>NOTE:</b> This alternate String message can exceed the 2000 character cap, and will be
	 * sent in two split Messages.
	 *
	 * @param message          A String message to reply with
	 * @param file             The File to reply with
	 * @param filename         The filename that Discord should display (null for default).
	 * @param alternateMessage A String message to reply with if the file cannot be uploaded, or an
	 *                         {@link java.io.IOException} is thrown
	 */
	public void replyOrAlternate(
					String message, File file, String filename, String alternateMessage) {
		try {
			event.getChannel().sendFiles(FileUpload.fromData(file, filename)).addContent(message).queue();
		} catch (Exception e) {
			reply(alternateMessage);
		}
	}

	public void replyInDm(String message) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(message);
		else {
			event.getAuthor().openPrivateChannel().queue(pc -> sendMessage(pc, message));
		}
	}

	/**
	 * Replies with a String message sent to the calling {@link net.dv8tion.jda.api.entities.User
	 * User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * <p><b>NOTE:</b> This alternate String message can exceed the 2000 character cap, and will be
	 * sent in two split Messages.
	 *
	 * @param message A String message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 */
	public void replyInDm(String message, Consumer<Message> success) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(message, success);
		else {
			event.getAuthor().openPrivateChannel().queue(pc -> sendMessage(pc, message, success));
		}
	}

	/**
	 * Replies with a String message sent to the calling {@link net.dv8tion.jda.api.entities.User
	 * User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * <p><b>NOTE:</b> This alternate String message can exceed the 2000 character cap, and will be
	 * sent in two split Messages.
	 *
	 * @param message A String message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void replyInDm(String message, Consumer<Message> success, Consumer<Throwable> failure) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(message, success, failure);
		else {
			event
							.getAuthor()
							.openPrivateChannel()
							.queue(pc -> sendMessage(pc, message, success, failure), failure);
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} sent to the
	 * calling {@link net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * @param embed The MessageEmbed to reply with
	 */
	public void replyInDm(MessageEmbed embed) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(embed);
		else {
			event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessageEmbeds(embed).queue());
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} sent to the
	 * calling {@link net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * @param embed   The MessageEmbed to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 */
	public void replyInDm(MessageEmbed embed, Consumer<Message> success) {
		if (event.isFromType(ChannelType.PRIVATE))
			getPrivateChannel().sendMessageEmbeds(embed).queue(success);
		else {
			event
							.getAuthor()
							.openPrivateChannel()
							.queue(pc -> pc.sendMessageEmbeds(embed).queue(success));
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.MessageEmbed MessageEmbed} sent to the
	 * calling {@link net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * @param embed   The MessageEmbed to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void replyInDm(
					MessageEmbed embed, Consumer<Message> success, Consumer<Throwable> failure) {
		if (event.isFromType(ChannelType.PRIVATE))
			getPrivateChannel().sendMessageEmbeds(embed).queue(success, failure);
		else {
			event
							.getAuthor()
							.openPrivateChannel()
							.queue(pc -> pc.sendMessageEmbeds(embed).queue(success, failure), failure);
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message} sent to the calling {@link
	 * net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * @param message The Message to reply with
	 */
	public void replyInDm(MessageCreateData message) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(message);
		else {
			event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue());
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message} sent to the calling {@link
	 * net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * @param message The Message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 */
	public void replyInDm(MessageCreateData message, Consumer<Message> success) {
		if (event.isFromType(ChannelType.PRIVATE))
			getPrivateChannel().sendMessage(message).queue(success);
		else {
			event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(message).queue(success));
		}
	}

	/**
	 * Replies with a {@link net.dv8tion.jda.api.entities.Message Message} sent to the calling {@link
	 * net.dv8tion.jda.api.entities.User User}'s {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the first Consumer as
	 * it's success callback and the second Consumer as the failure callback.
	 *
	 * @param message The Message to reply with
	 * @param success The Consumer to queue after sending the Message is sent.
	 * @param failure The Consumer to run if an error occurs when sending the Message.
	 */
	public void replyInDm(Message message, Consumer<Message> success, Consumer<Throwable> failure) {
		if (event.isFromType(ChannelType.PRIVATE))
			getPrivateChannel()
							.sendMessage(MessageCreateData.fromMessage(message))
							.queue(success, failure);
		else {
			event
							.getAuthor()
							.openPrivateChannel()
							.queue(
											pc -> pc.sendMessage(MessageCreateData.fromMessage(message)).queue(success, failure),
											failure);
		}
	}

	/**
	 * Replies with a String message and a {@link java.io.File} with the provided name, or a default
	 * name if left null, and sent to the calling {@link net.dv8tion.jda.api.entities.User User}'s
	 * {@link PrivateChannel}.
	 *
	 * <p>If the User to be Direct Messaged does not already have a PrivateChannel open to send
	 * messages to, this method will automatically open one.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p>This method uses {@link MessageChannel#sendFiles(FileUpload...)
	 * MessageChannel#sendFile(FileUpload...)} to send the File. For more information on what a bot
	 * may send using this, you may find the info in that method.
	 *
	 * @param message  A String message to reply with
	 * @param file     The {@code File} to reply with
	 * @param filename The filename that Discord should display (null for default).
	 */
	public void replyInDm(String message, File file, String filename) {
		if (event.isFromType(ChannelType.PRIVATE)) reply(message, file, filename);
		else {
			event
							.getAuthor()
							.openPrivateChannel()
							.queue(
											pc -> pc.sendFiles(FileUpload.fromData(file, filename)).addContent(message).queue());
		}
	}

	/**
	 * Replies with a String message, and a prefixed success emoji.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 */
	public void replySuccess(String message) {
		reply(client.getSuccess() + " " + message);
	}

	/**
	 * Replies with a String message and a prefixed success emoji and then queues a {@link
	 * java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 * @param queue   The Consumer to queue after sending the Message is sent.
	 */
	public void replySuccess(String message, Consumer<Message> queue) {
		reply(client.getSuccess() + " " + message, queue);
	}

	/**
	 * Replies with a String message, and a prefixed warning emoji.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 */
	public void replyWarning(String message) {
		reply(client.getWarning() + " " + message);
	}

	/**
	 * Replies with a String message and a prefixed warning emoji and then queues a {@link
	 * java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 * @param queue   The Consumer to queue after sending the Message is sent.
	 */
	public void replyWarning(String message, Consumer<Message> queue) {
		reply(client.getWarning() + " " + message, queue);
	}

	/**
	 * Replies with a String message and a prefixed error emoji.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()}.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 */
	public void replyError(String message) {
		reply(client.getError() + " " + message);
	}

	/**
	 * Replies with a String message and a prefixed error emoji and then queues a {@link
	 * java.util.function.Consumer}.
	 *
	 * <p>The {@link net.dv8tion.jda.api.requests.RestAction RestAction} returned by sending the
	 * response as a {@link net.dv8tion.jda.api.entities.Message Message} automatically does {@link
	 * net.dv8tion.jda.api.requests.RestAction#queue() RestAction#queue()} with the provided Consumer
	 * as it's success callback.
	 *
	 * <p><b>NOTE:</b> This message can exceed the 2000 character cap, and will be sent in two split
	 * Messages.
	 *
	 * @param message A String message to reply with
	 * @param queue   The Consumer to queue after sending the Message is sent.
	 */
	public void replyError(String message, Consumer<Message> queue) {
		reply(client.getError() + " " + message, queue);
	}

	/**
	 * Adds a success reaction to the calling {@link net.dv8tion.jda.api.entities.Message Message}.
	 */
	public void reactSuccess() {
		react(client.getSuccess());
	}

	@Override
	public void reactSuccessOrReply(String reply) {
		reactSuccess();
	}

	/**
	 * Adds a warning reaction to the calling {@link net.dv8tion.jda.api.entities.Message Message}.
	 */
	public void reactWarning() {
		react(client.getWarning());
	}

	/**
	 * Adds an error reaction to the calling {@link net.dv8tion.jda.api.entities.Message Message}.
	 */
	public void reactError() {
		react(client.getError());
	}

	@Override
	public MessageChannelUnion getMessageChannel() {
		return event.getChannel();
	}

	// private methods

	/**
	 * Uses the {@link bot.utils.command.CommandClient#getScheduleExecutor() client's executor} to run
	 * the provided {@link java.lang.Runnable Runnable} asynchronously without blocking the thread
	 * this is called in.
	 *
	 * <p>The ScheduledExecutorService this runs on can be configured using {@link
	 * CommandClientBuilder#setScheduleExecutor(java.util.concurrent.ScheduledExecutorService)
	 * CommandClientBuilder#setScheduleExecutor(ScheduledExecutorService)}.
	 *
	 * @param runnable The runnable to run async
	 */
	public void async(Runnable runnable) {
		Checks.notNull(runnable, "Runnable");
		client.getScheduleExecutor().submit(runnable);
	}

	private void react(String reaction) {
		if (reaction == null || reaction.isEmpty()) return;
		try {
			event
							.getMessage()
							.addReaction(Emoji.fromFormatted(reaction.replaceAll("<a?:(.+):(\\d+)>", "$1:$2")))
							.queue();
		} catch (PermissionException ignored) {
		}
	}

	private void sendMessage(MessageChannel chan, String message) {
		ArrayList<String> messages = splitMessage(message);
		for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
			chan.sendMessage(messages.get(i))
							.queue(
											m -> {
												if (event.isFromType(ChannelType.TEXT)) linkId(m);
											});
		}
	}

	private void sendMessage(MessageChannel chan, String message, Consumer<Message> success) {
		ArrayList<String> messages = splitMessage(message);
		for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
			if (i + 1 == MAX_MESSAGES || i + 1 == messages.size()) {
				chan.sendMessage(messages.get(i))
								.queue(
												m -> {
													if (event.isFromType(ChannelType.TEXT)) linkId(m);
													success.accept(m);
												});
			} else {
				chan.sendMessage(messages.get(i))
								.queue(
												m -> {
													if (event.isFromType(ChannelType.TEXT)) linkId(m);
												});
			}
		}
	}

	private void sendMessage(
					MessageChannel chan, String message, Consumer<Message> success, Consumer<Throwable> failure) {
		ArrayList<String> messages = splitMessage(message);
		for (int i = 0; i < MAX_MESSAGES && i < messages.size(); i++) {
			if (i + 1 == MAX_MESSAGES || i + 1 == messages.size()) {
				chan.sendMessage(messages.get(i))
								.queue(
												m -> {
													if (event.isFromType(ChannelType.TEXT)) linkId(m);
													success.accept(m);
												},
												failure);
			} else {
				chan.sendMessage(messages.get(i))
								.queue(
												m -> {
													if (event.isFromType(ChannelType.TEXT)) linkId(m);
												});
			}
		}
	}

	// custom shortcuts

	/**
	 * Gets a {@link net.dv8tion.jda.api.entities.SelfUser SelfUser} representing the bot. <br>
	 * This is the same as invoking {@code event.getJDA().getSelfUser()}.
	 *
	 * @return A User representing the bot
	 */
	public SelfUser getSelfUser() {
		return event.getJDA().getSelfUser();
	}

	/**
	 * Gets a {@link net.dv8tion.jda.api.entities.Member Member} representing the bot, or null if the
	 * event does not take place on a {@link net.dv8tion.jda.api.entities.Guild Guild}. <br>
	 * This is the same as invoking {@code event.getGuild().getSelfMember()}.
	 *
	 * @return A possibly-null Member representing the bot
	 */
	public Member getSelfMember() {
		return event.getGuild() == null ? null : event.getGuild().getSelfMember();
	}

	/**
	 * Tests whether or not the {@link net.dv8tion.jda.api.entities.User User} who triggered this
	 * event is an owner of the bot.
	 *
	 * @return {@code true} if the User is the Owner, else {@code false}
	 */
	public boolean isOwner() {
		if (event.getAuthor().getId().equals(this.getClient().getOwnerId())) return true;
		if (this.getClient().getCoOwnerIds() == null) return false;
		for (String id : this.getClient().getCoOwnerIds())
			if (id.equals(event.getAuthor().getId())) return true;
		return false;
	}

	// shortcuts

	/**
	 * Gets the {@link net.dv8tion.jda.api.entities.User User} who triggered this CommandEvent.
	 *
	 * @return The User who triggered this CommandEvent
	 */
	public User getAuthor() {
		return event.getAuthor();
	}

	/**
	 * Gets the {@link MessageChannel} that the CommandEvent was triggered on.
	 *
	 * @return The MessageChannel that the CommandEvent was triggered on
	 */
	public MessageChannel getChannel() {
		return event.getChannel();
	}

	@Override
	public User getUser() {
		return event.getAuthor();
	}

	/**
	 * Gets the {@link ChannelType} of the {@link MessageChannel} that the CommandEvent was triggered
	 * on.
	 *
	 * @return The ChannelType of the MessageChannel that this CommandEvent was triggered on
	 */
	public ChannelType getChannelType() {
		return event.getChannelType();
	}

	/**
	 * Gets the {@link net.dv8tion.jda.api.entities.Guild Guild} that this CommandEvent was triggered
	 * on.
	 *
	 * @return The Guild that this CommandEvent was triggered on
	 */
	public Guild getGuild() {
		return event.getGuild();
	}

	/**
	 * Gets the instance of {@link net.dv8tion.jda.api.JDA JDA} that this CommandEvent was caught by.
	 *
	 * @return The instance of JDA that this CommandEvent was caught by
	 */
	public JDA getJDA() {
		return event.getJDA();
	}

	/**
	 * Gets the {@link net.dv8tion.jda.api.entities.Member Member} that triggered this CommandEvent.
	 *
	 * @return The Member that triggered this CommandEvent
	 */
	public Member getMember() {
		return event.getMember();
	}

	/**
	 * Gets the {@link net.dv8tion.jda.api.entities.Message Message} responsible for triggering this
	 * CommandEvent.
	 *
	 * @return The Message responsible for the CommandEvent
	 */
	public Message getMessage() {
		return event.getMessage();
	}

	/**
	 * Gets the {@link PrivateChannel} that this CommandEvent may have taken place on, or {@code null}
	 * if it didn't happen on a PrivateChannel.
	 *
	 * @return The PrivateChannel that this CommandEvent may have taken place on, or null if it did
	 * not happen on a PrivateChannel.
	 */
	public PrivateChannel getPrivateChannel() {
		return event.getChannel().asPrivateChannel();
	}

	/**
	 * Gets the response number for the {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent
	 * MessageReceivedEvent}.
	 *
	 * @return The response number for the MessageReceivedEvent
	 */
	public long getResponseNumber() {
		return event.getResponseNumber();
	}

	/**
	 * Gets the {@link TextChannel} that this CommandEvent may have taken place on, or {@code null} if
	 * it didn't happen on a TextChannel.
	 *
	 * @return The TextChannel this CommandEvent may have taken place on, or null if it did not happen
	 * on a TextChannel.
	 */
	public TextChannel getTextChannel() {
		return event.getChannel().asTextChannel();
	}

	/**
	 * Gets the {@link GuildMessageChannel} that this CommandEvent may have taken place on, or {@code
	 * null} if it didn't happen on a GuildMessageChannel.
	 *
	 * @return The GuildMessageChannel this CommandEvent may have taken place on, or null if it did
	 * not happen on a GuildMessageChannel.
	 */
	public GuildMessageChannel getGuildChannel() {
		return event.getGuildChannel();
	}

	/**
	 * Compares a provided {@link ChannelType} with the one this CommandEvent occurred on, returning
	 * {@code true} if they are the same ChannelType.
	 *
	 * @param channelType The ChannelType to compare
	 * @return {@code true} if the CommandEvent originated from a {@link MessageChannel} of the
	 * provided ChannelType, otherwise {@code false}.
	 */
	public boolean isFromType(ChannelType channelType) {
		return event.isFromType(channelType);
	}
}
