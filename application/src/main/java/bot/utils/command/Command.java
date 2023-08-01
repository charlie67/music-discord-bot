/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bot.utils.command;

import bot.utils.command.events.CommandEvent;
import bot.utils.command.events.CommandEventType;
import bot.utils.command.events.SlashCommandEvent;
import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import bot.utils.command.option.optionValue.OptionValue;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.*;

/**
 * <h2><b>Commands In JDA-Utilities</b></h2>
 *
 * <p>The internal inheritance for Commands used in JDA-Utilities is that of the Command object.
 *
 * <p>Classes created inheriting this class gain the unique traits of commands operated using the
 * Commands Extension. <br>
 * Using several fields, a command can define properties that make it unique and complex while
 * maintaining a low level of development. <br>
 * All Commands extending this class can define any number of these fields in a object constructor
 * and then create the command action/response in the abstract {@link
 * bot.utils.command.Command#execute(CommandEvent) #execute(CommandEvent)} body:
 *
 * <pre><code> public class ExampleCmd extends Command {
 *
 *      public ExampleCmd() {
 *          this.name = "example";
 *          this.aliases = new String[]{"test","demo"};
 *          this.help = "gives an example of commands do";
 *      }
 *
 *      {@literal @Override}
 *      protected void execute(CommandEvent) {
 *          event.reply("Hey look! This would be the bot's reply if this was a command!");
 *      }
 *
 * }</code></pre>
 *
 * <p>Execution is with the provision of a MessageReceivedEvent-CommandClient wrapper called a
 * {@link CommandEvent CommandEvent} and is performed in two steps:
 *
 * <ul>
 *   <li>{@link bot.utils.command.Command#run(CommandEvent) run} - The command runs through a series
 *       of conditionals, automatically terminating the command instance if one is not met, and
 *       possibly providing an error response.
 *   <li>{@link bot.utils.command.Command#execute(CommandEvent) execute} - The command, now being
 *       cleared to run, executes and performs whatever lies in the abstract body method.
 * </ul>
 *
 * @author John Grosh (jagrosh)
 */
@Getter
public abstract class Command extends Interaction {
	/**
	 * Maps an option name to an index in the {@link bot.utils.command.Command#options options} list.
	 */
	private final Map<String, Integer> optionMap = new HashMap<>();
	/**
	 * The name of the command, allows the command to be called the formats: <br>
	 * Normal Command: {@code [prefix]<command name>}. <br>
	 * Slash Command: {@code /<command name>}
	 */
	protected String name = "null";
	/**
	 * A small help String that summarizes the function of the command, used in the default help
	 * builder, and shown in the client for Slash Commands.
	 */
	protected String help = "no help available";
	/**
	 * The {@link bot.utils.command.Command.Category Category} of the command. <br>
	 * This can perform any other checks not completed by the default conditional fields.
	 */
	protected Category category = null;
	/**
	 * {@code true} if the command may only be used in an NSFW {@link TextChannel} or DMs. {@code
	 * false} if it may be used anywhere <br>
	 * Default: {@code false}
	 */
	protected boolean nsfwOnly = false;
	/**
	 * A String name of a role required to use this command.
	 */
	protected String requiredRole = null;

	/**
	 * The aliases of the command, when calling a command these function identically to calling the
	 * {@link bot.utils.command.Command#name Command.name}. This options only works for normal
	 * commands, not slash commands.
	 */
	protected String[] aliases = new String[0];

	protected List<ChannelType> allowedInChannels = List.of(ChannelType.TEXT);

	protected List<CommandEventType> allowedCommandExecution =
					List.of(CommandEventType.SLASH, CommandEventType.TEXT);

	/**
	 * An array list of OptionData.
	 *
	 * <p><b>This is incompatible with children. You cannot have a child AND options.</b>
	 *
	 * <p>This is to specify different options for arguments and the stuff.
	 *
	 * <p>For example, to add an argument for "input", you can do this:<br>
	 *
	 * <pre><code>
	 *     OptionData data = new OptionData(OptionType.STRING, "input", "The input for the command").setRequired(true);
	 *    {@literal List<OptionData> dataList = new ArrayList<>();}
	 *     dataList.add(data);
	 *     this.options = dataList;</code></pre>
	 */
	protected List<Option> options = new ArrayList<>();

	/**
	 * The subcommand/child group this is associated with. Will be in format {@code /<parent name>
	 * <subcommandGroup name> <subcommand name>}.
	 *
	 * <p><b>This only works in a child/subcommand.</b>
	 *
	 * <p>To instantiate: <code>{@literal new SubcommandGroupData(name, description)}</code><br>
	 * It's important the instantiations are the same across children if you intend to keep them in
	 * the same group.
	 *
	 * <p>Can be null, and it will not be assigned to a group.
	 */
	protected SubcommandGroupData subcommandGroup = null;
	/**
	 * Localization of slash command description. Allows discord to change the language of the
	 * description of slash commands in the client.<br>
	 * Example:<br>
	 *
	 * <pre><code>
	 *     public Command() {
	 *          this.description = "all commands"
	 *          this.descriptionLocalization = Map.of(DiscordLocale.GERMAN, "alle Befehle",
	 * DiscordLocale.RUSSIAN, "все команды");
	 *     }
	 * </code></pre>
	 */
	protected Map<DiscordLocale, String> descriptionLocalization = new HashMap<>();
	/**
	 * The child commands of the command. These are used in the format {@code [prefix]<parent name>
	 * <child name>}.
	 */
	protected Command[] children = new Command[0];
	/**
	 * The {@link java.util.function.BiConsumer BiConsumer} for creating a help response to the format
	 * {@code [prefix]<command name> help}.
	 */
	protected BiConsumer<CommandEvent, Command> helpBiConsumer = null;
	/**
	 * {@code true} if this command checks a channel topic for topic-tags. <br>
	 * This means that putting {@code {-commandname}}, {@code {-command category}}, {@code {-all}} in
	 * a channel topic will cause this command to terminate. <br>
	 * Default {@code true}.
	 */
	protected boolean usesTopicTags = true;
	/**
	 * {@code true} if this command should be hidden from the help. <br>
	 * Default {@code false}<br>
	 * <b>This has no effect for SlashCommands.</b>
	 */
	protected boolean hidden = false;

	protected Map<DiscordLocale, String> nameLocalization = new HashMap<>();
	String arguments = "";

	/**
	 * The main body method of a {@link bot.utils.command.Command Command}. <br>
	 * This is the "response" for a successful {@link bot.utils.command.Command#run(CommandEvent)
	 * #run(CommandEvent)}.
	 *
	 * @param event The {@link CommandEvent CommandEvent} that triggered this Command
	 */
	protected abstract void execute(CommandEvent event);

	public Map<OptionName, OptionValue> createOptionMap(MessageReceivedEvent event) {
		return Map.of();
	}

	/**
	 * Runs checks for the {@link bot.utils.command.Command Command} with the given {@link
	 * CommandEvent CommandEvent} that called it. <br>
	 * Will terminate, and possibly respond with a failure message, if any checks fail.
	 *
	 * @param event The CommandEvent that triggered this Command
	 */
	public final void run(CommandEvent event) {
		// child check
		//		if (!event.getArgs().isEmpty()) {
		//			String[] parts = Arrays.copyOf(event.getArgs().split("\\s+", 2), 2);
		//			if (helpBiConsumer != null && parts[0].equalsIgnoreCase(event.getClient().getHelpWord())) {
		//				helpBiConsumer.accept(event, this);
		//				return;
		//			}
		//			for (Command cmd : children) {
		//				if (cmd.isCommandFor(parts[0])) {
		//					event.setArgs(parts[1] == null ? "" : parts[1]);
		//					cmd.run(event);
		//					return;
		//				}
		//			}
		//		}

		// owner check
		if (ownerCommand && !(event.isOwner())) {
			terminate(event, null);
			return;
		}

		// category check
		if (category != null && !category.test(event)) {
			terminate(event, category.getFailureResponse());
			return;
		}

		// is allowed check
		if (event.isFromType(ChannelType.TEXT) && !isAllowed(event.getTextChannel())) {
			terminate(event, "That command cannot be used in this channel!");
			return;
		}

		// required role check
		if (requiredRole != null)
			if (!event.isFromType(ChannelType.TEXT)
							|| event.getMember().getRoles().stream()
							.noneMatch(r -> r.getName().equalsIgnoreCase(requiredRole))) {
				terminate(
								event,
								event.getClient().getError()
												+ " You must have a role called `"
												+ requiredRole
												+ "` to use that!");
				return;
			}

		// availability check
		if (!event.isFromType(ChannelType.PRIVATE)) {
			// user perms
			for (Permission p : userPermissions) {
				if (p.isChannel()) {
					if (!event.getMember().hasPermission(event.getGuildChannel(), p)) {
						terminate(
										event,
										String.format(
														userMissingPermMessage, event.getClient().getError(), p.getName(), "channel"));
						return;
					}
				} else {
					if (!event.getMember().hasPermission(p)) {
						terminate(
										event,
										String.format(
														userMissingPermMessage, event.getClient().getError(), p.getName(), "server"));
						return;
					}
				}
			}

			// bot perms
			for (Permission p : botPermissions) {
				if (p.isChannel()) {
					if (p.isVoice()) {
						GuildVoiceState gvc = event.getMember().getVoiceState();
						AudioChannel vc = gvc == null ? null : gvc.getChannel();
						if (vc == null) {
							terminate(
											event,
											event.getClient().getError() + " You must be in a voice channel to use that!");
							return;
						} else if (!event.getSelfMember().hasPermission(vc, p)) {
							terminate(
											event,
											String.format(
															botMissingPermMessage,
															event.getClient().getError(),
															p.getName(),
															"voice channel"));
							return;
						}
					} else {
						if (!event.getSelfMember().hasPermission(event.getGuildChannel(), p)) {
							terminate(
											event,
											String.format(
															botMissingPermMessage, event.getClient().getError(), p.getName(), "channel"));
							return;
						}
					}
				} else {
					if (!event.getSelfMember().hasPermission(p)) {
						terminate(
										event,
										String.format(
														botMissingPermMessage, event.getClient().getError(), p.getName(), "server"));
						return;
					}
				}
			}

			// nsfw check
			if (nsfwOnly && event.isFromType(ChannelType.TEXT) && !event.getTextChannel().isNSFW()) {
				terminate(event, "This command may only be used in NSFW text channels!");
				return;
			}
		} else if (guildOnly) {
			terminate(
							event, event.getClient().getError() + " This command cannot be used in direct messages");
			return;
		}

		// cooldown check, ignoring owner
		if (cooldown > 0 && !(event.isOwner())) {
			String key = getCooldownKey(event);
			int remaining = event.getClient().getRemainingCooldown(key);
			if (remaining > 0) {
				terminate(event, getCooldownError(event, remaining));
				return;
			} else event.getClient().applyCooldown(key, cooldown);
		}

		// run
		try {
			execute(event);
		} catch (Throwable t) {
			if (event.getClient().getListener() != null) {
				event.getClient().getListener().onCommandException(event, this, t);
				return;
			}
			// otherwise we rethrow
			throw t;
		}

		if (event.getClient().getListener() != null)
			event.getClient().getListener().onCompletedCommand(event, this);
	}

	/**
	 * Checks if the given input represents this Command
	 *
	 * @param input The input to check
	 * @return {@code true} if the input is the name or an alias of the Command
	 */
	public boolean isCommandFor(String input) {
		if (name.equalsIgnoreCase(input)) return true;
		for (String alias : aliases) if (alias.equalsIgnoreCase(input)) return true;
		return false;
	}

	/**
	 * Checks whether a command is allowed in a {@link TextChannel} by searching the channel topic for
	 * topic tags relating to the command.
	 *
	 * <p>{-{@link bot.utils.command.Command#name name}}, {-{@link bot.utils.command.Command.Category
	 * category name}}, or {-{@code all}} are valid examples of ways that this method would return
	 * {@code false} if placed in a channel topic.
	 *
	 * <p><b>NOTE:</b>Topic tags are <b>case sensitive</b> and proper usage must be in lower case!
	 * <br>
	 * Also note that setting {@link bot.utils.command.Command#usesTopicTags usesTopicTags} to {@code
	 * false} will cause this method to always return {@code true}, as the feature would not be
	 * applicable in the first place.
	 *
	 * @param channel The TextChannel to test.
	 * @return {@code true} if the channel topic doesn't specify any topic-tags that would cause this
	 * command to be cancelled, or if {@code usesTopicTags} has been set to {@code false}.
	 */
	public boolean isAllowed(TextChannel channel) {
		if (!usesTopicTags) return true;
		if (channel == null) return true;
		String topic = channel.getTopic();
		if (topic == null || topic.isEmpty()) return true;
		topic = topic.toLowerCase(Locale.ROOT);
		String lowerName = name.toLowerCase(Locale.ROOT);
		if (topic.contains("{" + lowerName + "}")) return true;
		if (topic.contains("{-" + lowerName + "}")) return false;
		String lowerCat = category == null ? null : category.getName().toLowerCase(Locale.ROOT);
		if (lowerCat != null) {
			if (topic.contains("{" + lowerCat + "}")) return true;
			if (topic.contains("{-" + lowerCat + "}")) return false;
		}
		return !topic.contains("{-all}");
	}

	/**
	 * Checks if this Command can only be used in a {@link net.dv8tion.jda.api.entities.Guild Guild}.
	 *
	 * @return {@code true} if this Command can only be used in a Guild, else {@code false} if it can
	 * be used outside of one
	 */
	public boolean isGuildOnly() {
		return guildOnly;
	}

	/**
	 * Checks whether this command should be hidden from the help.
	 *
	 * @return {@code true} if the command should be hidden, otherwise {@code false}
	 */
	public boolean isHidden() {
		return hidden;
	}

	private void terminate(CommandEvent event, String message) {
		if (message != null) event.reply(message);
		if (event.getClient().getListener() != null)
			event.getClient().getListener().onTerminatedCommand(event, this);
	}

	/**
	 * Gets the proper cooldown key for this Command under the provided {@link CommandEvent
	 * CommandEvent}.
	 *
	 * @param event The CommandEvent to generate the cooldown for.
	 * @return A String key to use when applying a cooldown.
	 */
	public String getCooldownKey(CommandEvent event) {
		switch (cooldownScope) {
			case USER:
				return cooldownScope.genKey(name, event.getAuthor().getIdLong());
			case USER_GUILD:
				return event.getGuild() != null
								? cooldownScope.genKey(
								name, event.getAuthor().getIdLong(), event.getGuild().getIdLong())
								: CooldownScope.USER_CHANNEL.genKey(
								name, event.getAuthor().getIdLong(), event.getChannel().getIdLong());
			case USER_CHANNEL:
				return cooldownScope.genKey(
								name, event.getAuthor().getIdLong(), event.getChannel().getIdLong());
			case GUILD:
				return event.getGuild() != null
								? cooldownScope.genKey(name, event.getGuild().getIdLong())
								: CooldownScope.CHANNEL.genKey(name, event.getChannel().getIdLong());
			case CHANNEL:
				return cooldownScope.genKey(name, event.getChannel().getIdLong());
			case SHARD:
				return event.getJDA().getShardInfo() != JDA.ShardInfo.SINGLE
								? cooldownScope.genKey(name, event.getJDA().getShardInfo().getShardId())
								: CooldownScope.GLOBAL.genKey(name, 0);
			case USER_SHARD:
				return event.getJDA().getShardInfo() != JDA.ShardInfo.SINGLE
								? cooldownScope.genKey(
								name, event.getAuthor().getIdLong(), event.getJDA().getShardInfo().getShardId())
								: CooldownScope.USER.genKey(name, event.getAuthor().getIdLong());
			case GLOBAL:
				return cooldownScope.genKey(name, 0);
			default:
				return "";
		}
	}

	/**
	 * Gets an error message for this Command under the provided {@link CommandEvent CommanEvent}.
	 *
	 * @param event     The CommandEvent to generate the error message for.
	 * @param remaining The remaining number of seconds a command is on cooldown for.
	 * @return A String error message for this command if {@code remaining > 0}, else {@code null}.
	 */
	public String getCooldownError(CommandEvent event, int remaining) {
		if (remaining <= 0) return null;
		String front =
						event.getClient().getWarning()
										+ " That command is on cooldown for "
										+ remaining
										+ " more seconds";
		if (cooldownScope.equals(CooldownScope.USER)) return front + "!";
		else if (cooldownScope.equals(CooldownScope.USER_GUILD) && event.getGuild() == null)
			return front + " " + CooldownScope.USER_CHANNEL.errorSpecification + "!";
		else if (cooldownScope.equals(CooldownScope.GUILD) && event.getGuild() == null)
			return front + " " + CooldownScope.CHANNEL.errorSpecification + "!";
		else return front + " " + cooldownScope.errorSpecification + "!";
	}

	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
	}

	private List<OptionData> getOptionData() {
		return getOptions().stream().map(Option::optionData).collect(Collectors.toList());
	}

	/**
	 * Builds CommandData for the SlashCommand upsert. This code is executed when we need to upsert
	 * the command.
	 *
	 * <p>Useful for manual upserting.
	 *
	 * @return the built command data
	 */
	public CommandData buildCommandData() {
		// Make the command data
		SlashCommandData data = Commands.slash(getName(), getHelp());
		if (!getOptions().isEmpty()) {
			data.addOptions(getOptionData());
		}

		// Check name localizations
		if (!getNameLocalization().isEmpty()) {
			// Add localizations
			data.setNameLocalizations(getNameLocalization());
		}
		// Check description localizations
		if (!getDescriptionLocalization().isEmpty()) {
			// Add localizations
			data.setDescriptionLocalizations(getDescriptionLocalization());
		}

		// Check for children
		if (children.length != 0) {
			// Temporary map for easy group storage
			Map<String, SubcommandGroupData> groupData = new HashMap<>();
			for (Command child : children) {
				// Create subcommand data
				SubcommandData subcommandData = new SubcommandData(child.getName(), child.getHelp());
				// Add options
				if (!child.getOptions().isEmpty()) {
					subcommandData.addOptions(child.getOptionData());
				}

				// Check child name localizations
				if (!child.getNameLocalization().isEmpty()) {
					// Add localizations
					subcommandData.setNameLocalizations(child.getNameLocalization());
				}
				// Check child description localizations
				if (!child.getDescriptionLocalization().isEmpty()) {
					// Add localizations
					subcommandData.setDescriptionLocalizations(child.getDescriptionLocalization());
				}

				// If there's a subcommand group
				if (child.getSubcommandGroup() != null) {
					SubcommandGroupData group = child.getSubcommandGroup();

					SubcommandGroupData newData =
									groupData.getOrDefault(group.getName(), group).addSubcommands(subcommandData);

					groupData.put(group.getName(), newData);
				}
				// Just add to the command
				else {
					data.addSubcommands(subcommandData);
				}
			}
			if (!groupData.isEmpty()) data.addSubcommandGroups(groupData.values());
		}

		if (this.getUserPermissions() == null)
			data.setDefaultPermissions(DefaultMemberPermissions.DISABLED);
		else data.setDefaultPermissions(DefaultMemberPermissions.enabledFor(this.getUserPermissions()));

		data.setGuildOnly(this.guildOnly);
		return data;
	}

	/**
	 * To be used in {@link bot.utils.command.Command Command}s as a means of organizing commands into
	 * "Categories" as well as terminate command usage when the calling {@link CommandEvent
	 * CommandEvent} doesn't meet certain requirements.
	 *
	 * @author John Grosh (jagrosh)
	 */
	public static class Category {
		private final String name;
		private final String failResponse;
		private final Predicate<CommandEvent> predicate;

		/**
		 * A Command Category containing a name.
		 *
		 * @param name The name of the Category
		 */
		public Category(String name) {
			this.name = name;
			this.failResponse = null;
			this.predicate = null;
		}

		/**
		 * A Command Category containing a name and a {@link java.util.function.Predicate}.
		 *
		 * <p>The command will be terminated if {@link
		 * bot.utils.command.Command.Category#test(CommandEvent)} returns {@code false}.
		 *
		 * @param name      The name of the Category
		 * @param predicate The Category predicate to test
		 */
		public Category(String name, Predicate<CommandEvent> predicate) {
			this.name = name;
			this.failResponse = null;
			this.predicate = predicate;
		}

		public Category(String name, String failResponse, Predicate<CommandEvent> predicate) {
			this.name = name;
			this.failResponse = failResponse;
			this.predicate = predicate;
		}

		/**
		 * Gets the name of the Category.
		 *
		 * @return The name of the Category
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the failure response of the Category.
		 *
		 * @return The failure response of the Category
		 */
		public String getFailureResponse() {
			return failResponse;
		}

		/**
		 * Runs a test of the provided {@link java.util.function.Predicate}. Does not support
		 * SlashCommands.
		 *
		 * @param event The {@link CommandEvent} that was called when this method is invoked
		 * @return {@code true} if the Predicate was not set, was set as null, or was tested and
		 * returned true, otherwise returns {@code false}
		 */
		public boolean test(CommandEvent event) {
			return predicate == null || predicate.test(event);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Category other)) return false;
			return Objects.equals(name, other.name)
							&& Objects.equals(predicate, other.predicate)
							&& Objects.equals(failResponse, other.failResponse);
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 17 * hash + Objects.hashCode(this.name);
			hash = 17 * hash + Objects.hashCode(this.failResponse);
			hash = 17 * hash + Objects.hashCode(this.predicate);
			return hash;
		}
	}
}
