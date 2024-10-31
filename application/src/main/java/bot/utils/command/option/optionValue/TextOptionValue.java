package bot.utils.command.option.optionValue;

import bot.utils.command.option.Response;

import java.util.List;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@RequiredArgsConstructor
@Builder
public class TextOptionValue implements OptionValue {

	private final Response optionName;

	private final JDA jda;
	private final MessageReceivedEvent event;
	private final String optionValue;

	@Override
	public OptionType getType() {
		return optionName.getOptionType();
	}

	@Override
	public String getName() {
		return optionName.getDisplayName();
	}

	@Override
	public List<Message.Attachment> getAsAttachment() {
		return event.getMessage().getAttachments();
	}

	@Override
	public String getAsString() {
		return optionValue;
	}

	@Override
	public Boolean getAsBoolean() {
		return Boolean.valueOf(optionValue);
	}

	@Override
	public long getAsLong() {
		return Long.parseLong(optionValue);
	}

	@Override
	public int getAsInt() {
		return Integer.parseInt(optionValue);
	}

	@Override
	public double getAsDouble() {
		return Double.parseDouble(optionValue);
	}

	@Override
	public Member getAsMember() {
		return event.getGuild().getMemberById(optionValue);
	}

	@Override
	public User getAsUser() {
		return jda.getUserById(optionValue);
	}

	@Override
	public Role getAsRole() {
		return null;
	}

	@Override
	public ChannelType getChannelType() {
		return event.getChannelType();
	}

	@Override
	public GuildChannelUnion getAsChannel() {
		return null;
	}
}
