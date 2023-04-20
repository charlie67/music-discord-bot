/// *
// * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
// package bot.utils.command;
//
// import bot.utils.command.events.SlashCommandEvent;
// import net.dv8tion.jda.annotations.ForRemoval;
// import net.dv8tion.jda.api.Permission;
// import net.dv8tion.jda.api.entities.GuildVoiceState;
// import net.dv8tion.jda.api.entities.Member;
// import net.dv8tion.jda.api.entities.channel.ChannelType;
// import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
// import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
// import net.dv8tion.jda.api.interactions.DiscordLocale;
// import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
// import net.dv8tion.jda.api.interactions.commands.build.CommandData;
// import net.dv8tion.jda.api.interactions.commands.build.Commands;
// import net.dv8tion.jda.api.interactions.commands.build.OptionData;
// import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
// import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
// import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
/// **
// * <h2><b>Slash Commands In JDA-Chewtils</b></h2>
// *
// *
// * <pre><code> public class ExampleCmd extends SlashCommand {
// *
// *      public ExampleCmd() {
// *          this.name = "example";
// *          this.help = "gives an example of commands do";
// *      }
// *
// *      {@literal @Override}
// *      protected void execute(SlashCommandEvent event) {
// *          event.reply("Hey look! This would be the bot's reply if this was a command!").queue();
// *      }
// *
// * }</code></pre>
// *
// * <p>Execution is with the provision of the SlashCommandEvent is performed in two steps:
// *
// * <ul>
// *   <li>{@link SlashCommand#run(SlashCommandEvent) run} - The command runs through a series of
// *       conditionals, automatically terminating the command instance if one is not met, and
// *       possibly providing an error response.
// *   <li>{@link Command#execute(bot.utils.command.events.CommandEvent) execute} - The command, now
// being cleared to
// *       run, executes and performs whatever lies in the abstract body method.
// * </ul>
// *
// * @author Olivia (Chew)
// */
// public abstract class SlashCommand extends Command {
//	/**
//	 * Localization of slash command name. Allows discord to change the language of the name of slash
//	 * commands in the client.<br>
//	 * Example:<br>
//	 *
//	 * <pre><code>
//	 *     public Command() {
//	 *          this.name = "help"
//	 *          this.nameLocalization = Map.of(DiscordLocale.GERMAN, "hilfe", DiscordLocale.RUSSIAN,
// "помощь");
//	 *     }
//	 * </code></pre>
//	 */
//	protected Map<DiscordLocale, String> nameLocalization = new HashMap<>();
//
//	/**
//	 * Localization of slash command description. Allows discord to change the language of the
//	 * description of slash commands in the client.<br>
//	 * Example:<br>
//	 *
//	 * <pre><code>
//	 *     public Command() {
//	 *          this.description = "all commands"
//	 *          this.descriptionLocalization = Map.of(DiscordLocale.GERMAN, "alle Befehle",
// DiscordLocale.RUSSIAN, "все команды");
//	 *     }
//	 * </code></pre>
//	 */
//	protected Map<DiscordLocale, String> descriptionLocalization = new HashMap<>();
//
//	/**
//	 * This option is deprecated in favor of using Discord's permissions<br>
//	 * This deprecation can be ignored if you intend to support normal and slash commands.
//	 */
//	@Deprecated
//	protected String requiredRole = null;
//
//	/**
//	 * The child commands of the command. These are used in the format {@code /<parent name> <child
//	 * name>}. This is synonymous with sub commands. Additionally, sub-commands cannot have children.
//	 * <br>
//	 */
//	protected SlashCommand[] children = new SlashCommand[0];
//
//	/**
//	 * The subcommand/child group this is associated with. Will be in format {@code /<parent name>
//	 * <subcommandGroup name> <subcommand name>}.
//	 *
//	 * <p><b>This only works in a child/subcommand.</b>
//	 *
//	 * <p>To instantiate: <code>{@literal new SubcommandGroupData(name, description)}</code><br>
//	 * It's important the instantiations are the same across children if you intend to keep them in
//	 * the same group.
//	 *
//	 * <p>Can be null, and it will not be assigned to a group.
//	 */
//	protected SubcommandGroupData subcommandGroup = null;
//
//	/**
//	 * The command client to be retrieved if needed.
//	 *
//	 * @deprecated This is now retrieved from {@link SlashCommandEvent#getClient()}.
//	 */
//	protected CommandClient client;
//
//	/**
//	 * The main body method of a {@link SlashCommand SlashCommand}. <br>
//	 * This is the "response" for a successful {@link SlashCommand#run(SlashCommandEvent)
//	 * #run(CommandEvent)}.
//	 *
//	 * @param event The {@link SlashCommandEvent SlashCommandEvent} that triggered this Command
//	 */
////  protected abstract void execute(SlashCommandEvent event);
//
//	/**
//	 * This body is executed when an auto-complete event is received. This only ever gets executed if
//	 * an auto-complete {@link #options option} is set.
//	 *
//	 * @param event The event to handle.
//	 * @see OptionData#setAutoComplete(boolean)
//	 */
//	public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
//	}
//
//	/**
//	 * Runs checks for the {@link SlashCommand SlashCommand} with the given {@link SlashCommandEvent
//	 * SlashCommandEvent} that called it. <br>
//	 * Will terminate, and possibly respond with a failure message, if any checks fail.
//	 *
//	 * @param event The SlashCommandEvent that triggered this Command
//	 */
//	public final void run(SlashCommandEvent event) {
//		// set the client
//		this.client = event.getClient();
//
//		// owner check
//		if (ownerCommand && !(isOwner(event, client))) {
//			terminate(event, "Only an owner may run this command. Sorry.", client);
//			return;
//		}
//
//		// is allowed check
//		try {
//			if (!isAllowed(event.getTextChannel())) {
//				terminate(event, "That command cannot be used in this channel!", client);
//				return;
//			}
//		} catch (Exception e) {
//			// ignore for now
//		}
//
//		// required role check
//		if (requiredRole != null)
//			if (!(event.getChannelType() == ChannelType.TEXT)
//							|| event.getMember().getRoles().stream()
//							.noneMatch(r -> r.getName().equalsIgnoreCase(requiredRole))) {
//				terminate(
//								event,
//								client.getError() + " You must have a role called `" + requiredRole + "` to use that!",
//								client);
//				return;
//			}
//
//		// availability check
//		if (event.getChannelType() != ChannelType.PRIVATE) {
//			// user perms
//			for (Permission p : userPermissions) {
//				// Member will never be null because this is only ran in a server (text channel)
//				if (event.getMember() == null) continue;
//
//				if (p.isChannel()) {
//					if (!event.getMember().hasPermission(event.getGuildChannel(), p)) {
//						terminate(
//										event,
//										String.format(userMissingPermMessage, client.getError(), p.getName(), "channel"),
//										client);
//						return;
//					}
//				} else {
//					if (!event.getMember().hasPermission(p)) {
//						terminate(
//										event,
//										String.format(userMissingPermMessage, client.getError(), p.getName(), "server"),
//										client);
//						return;
//					}
//				}
//			}
//
//			// bot perms
//			for (Permission p : botPermissions) {
//				// We can ignore this permission because bots can reply with embeds even without either of
//				// these perms.
//				// The only thing stopping them is the user's ability to use Application Commands.
//				// It's extremely dumb, but what more can you do.
//				if (p == Permission.VIEW_CHANNEL || p == Permission.MESSAGE_EMBED_LINKS) continue;
//
//				Member selfMember = event.getGuild() == null ? null : event.getGuild().getSelfMember();
//				if (p.isChannel()) {
//					if (p.isVoice()) {
//						GuildVoiceState gvc = event.getMember().getVoiceState();
//						AudioChannel vc = gvc == null ? null : gvc.getChannel();
//						if (vc == null) {
//							terminate(
//											event,
//											client.getError() + " You must be in a voice channel to use that!",
//											client);
//							return;
//						} else if (!selfMember.hasPermission(vc, p)) {
//							terminate(
//											event,
//											String.format(
//															botMissingPermMessage, client.getError(), p.getName(), "voice channel"),
//											client);
//							return;
//						}
//					} else {
//						if (!selfMember.hasPermission(event.getGuildChannel(), p)) {
//							terminate(
//											event,
//											String.format(botMissingPermMessage, client.getError(), p.getName(), "channel"),
//											client);
//							return;
//						}
//					}
//				} else {
//					if (!selfMember.hasPermission(p)) {
//						terminate(
//										event,
//										String.format(botMissingPermMessage, client.getError(), p.getName(), "server"),
//										client);
//						return;
//					}
//				}
//			}
//
//			// nsfw check
//			if (nsfwOnly
//							&& event.getChannelType() == ChannelType.TEXT
//							&& !event.getTextChannel().isNSFW()) {
//				terminate(event, "This command may only be used in NSFW text channels!", client);
//				return;
//			}
//		} else if (guildOnly) {
//			terminate(
//							event, client.getError() + " This command cannot be used in direct messages", client);
//			return;
//		}
//
//		// cooldown check, ignoring owner
//		if (cooldown > 0 && !(isOwner(event, client))) {
//			String key = getCooldownKey(event);
//			int remaining = client.getRemainingCooldown(key);
//			if (remaining > 0) {
//				terminate(event, getCooldownError(event, remaining, client), client);
//				return;
//			} else client.applyCooldown(key, cooldown);
//		}
//
//		// run
//		try {
//			execute(event);
//		} catch (Throwable t) {
//			if (client.getListener() != null) {
//				client.getListener().onSlashCommandException(event, this, t);
//				return;
//			}
//			// otherwise we rethrow
//			throw t;
//		}
//
//		if (client.getListener() != null) client.getListener().onCompletedSlashCommand(event, this);
//	}
//
//	/**
//	 * Tests whether or not the {@link net.dv8tion.jda.api.entities.User User} who triggered this
//	 * event is an owner of the bot.
//	 *
//	 * @param event  the event that triggered the command
//	 * @param client the command client for checking stuff
//	 * @return {@code true} if the User is the Owner, else {@code false}
//	 */
//	public boolean isOwner(SlashCommandEvent event, CommandClient client) {
//		if (event.getUser().getId().equals(client.getOwnerId())) return true;
//		if (client.getCoOwnerIds() == null) return false;
//		for (String id : client.getCoOwnerIds()) if (id.equals(event.getUser().getId())) return true;
//		return false;
//	}
//
//	/**
//	 * Gets the CommandClient.
//	 *
//	 * @return the CommandClient.
//	 * @deprecated This is now retrieved from {@link SlashCommandEvent#getClient()}.
//	 */
//	@Deprecated
//	@ForRemoval(deadline = "2.0.0")
//	public CommandClient getClient() {
//		return client;
//	}
//
//	/**
//	 * Gets the subcommand data associated with this subcommand.
//	 *
//	 * @return subcommand data
//	 */
//	public SubcommandGroupData getSubcommandGroup() {
//		return subcommandGroup;
//	}
//
//	/**
//	 * Gets the options associated with this command.
//	 *
//	 * @return the OptionData array for options
//	 */
//	public List<OptionData> getOptions() {
//		return options;
//	}
//

//
//	/**
//	 * Gets the {@link SlashCommand#children Command.children} for the Command.
//	 *
//	 * @return The children for the Command
//	 */
//	public SlashCommand[] getChildren() {
//		return children;
//	}
//
//	private void terminate(SlashCommandEvent event, String message, CommandClient client) {
//		if (message != null) event.reply(message).setEphemeral(true).queue();
//		if (client.getListener() != null) client.getListener().onTerminatedSlashCommand(event, this);
//	}
//
//	/**
//	 * Gets the proper cooldown key for this Command under the provided {@link SlashCommandEvent
//	 * SlashCommandEvent}.
//	 *
//	 * @param event The CommandEvent to generate the cooldown for.
//	 * @return A String key to use when applying a cooldown.
//	 */
//	public String getCooldownKey(SlashCommandEvent event) {
//		switch (cooldownScope) {
//			case USER:
//				return cooldownScope.genKey(name, event.getUser().getIdLong());
//			case USER_GUILD:
//				return event.getGuild() != null
//								? cooldownScope.genKey(name, event.getUser().getIdLong(), event.getGuild().getIdLong())
//								: CooldownScope.USER_CHANNEL.genKey(
//								name, event.getUser().getIdLong(), event.getChannel().getIdLong());
//			case USER_CHANNEL:
//				return cooldownScope.genKey(
//								name, event.getUser().getIdLong(), event.getChannel().getIdLong());
//			case GUILD:
//				return event.getGuild() != null
//								? cooldownScope.genKey(name, event.getGuild().getIdLong())
//								: CooldownScope.CHANNEL.genKey(name, event.getChannel().getIdLong());
//			case CHANNEL:
//				return cooldownScope.genKey(name, event.getChannel().getIdLong());
//			case SHARD:
//				event.getJDA().getShardInfo();
//				return cooldownScope.genKey(name, event.getJDA().getShardInfo().getShardId());
//			case USER_SHARD:
//				event.getJDA().getShardInfo();
//				return cooldownScope.genKey(
//								name, event.getUser().getIdLong(), event.getJDA().getShardInfo().getShardId());
//			case GLOBAL:
//				return cooldownScope.genKey(name, 0);
//			default:
//				return "";
//		}
//	}
//
//	/**
//	 * Gets an error message for this Command under the provided {@link SlashCommandEvent
//	 * SlashCommandEvent}.
//	 *
//	 * @param event     The CommandEvent to generate the error message for.
//	 * @param remaining The remaining number of seconds a command is on cooldown for.
//	 * @param client    The CommandClient for checking stuff
//	 * @return A String error message for this command if {@code remaining > 0}, else {@code null}.
//	 */
//	public String getCooldownError(SlashCommandEvent event, int remaining, CommandClient client) {
//		if (remaining <= 0) return null;
//		String front =
//						client.getWarning() + " That command is on cooldown for " + remaining + " more seconds";
//		if (cooldownScope.equals(CooldownScope.USER)) return front + "!";
//		else if (cooldownScope.equals(CooldownScope.USER_GUILD) && event.getGuild() == null)
//			return front + " " + CooldownScope.USER_CHANNEL.errorSpecification + "!";
//		else if (cooldownScope.equals(CooldownScope.GUILD) && event.getGuild() == null)
//			return front + " " + CooldownScope.CHANNEL.errorSpecification + "!";
//		else return front + " " + cooldownScope.errorSpecification + "!";
//	}
//
//	/**
//	 * Gets the specified localizations of slash command names.
//	 *
//	 * @return Slash command name localizations.
//	 */
//	public Map<DiscordLocale, String> getNameLocalization() {
//		return nameLocalization;
//	}
//
//	/**
//	 * Gets the specified localizations of slash command descriptions.
//	 *
//	 * @return Slash command description localizations.
//	 */
//	public Map<DiscordLocale, String> getDescriptionLocalization() {
//		return descriptionLocalization;
//	}
// }
