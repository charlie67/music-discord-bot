package bot.utils.command.option.optionValue;

import bot.utils.command.option.OptionName;
import java.util.List;
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
public class TextOptionValue implements OptionValue {

  private final OptionName optionName;

  private final JDA jda;
  MessageReceivedEvent event;
  private String optionString;

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
    return optionString;
  }

  @Override
  public Boolean getAsBoolean() {
    return Boolean.valueOf(optionString);
  }

  @Override
  public long getAsLong() {
    return Long.parseLong(optionString);
  }

  @Override
  public int getAsInt() {
    return Integer.parseInt(optionString);
  }

  @Override
  public double getAsDouble() {
    return Double.parseDouble(optionString);
  }

  @Override
  public Member getAsMember() {
    return event.getGuild().getMemberById(optionString);
  }

  @Override
  public User getAsUser() {
    return jda.getUserById(optionString);
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
