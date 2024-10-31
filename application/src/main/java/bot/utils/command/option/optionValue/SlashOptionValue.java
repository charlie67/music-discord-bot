package bot.utils.command.option.optionValue;

import bot.utils.command.option.Response;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@RequiredArgsConstructor
@Builder
public class SlashOptionValue implements OptionValue {

  private final Response optionName;

  private final OptionMapping optionMapping;

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
    return Collections.singletonList(optionMapping.getAsAttachment());
  }

  @Override
  public String getAsString() {
    return optionMapping.getAsString();
  }

  @Override
  public Boolean getAsBoolean() {
    return optionMapping.getAsBoolean();
  }

  @Override
  public long getAsLong() {
    return optionMapping.getAsLong();
  }

  @Override
  public int getAsInt() {
    return optionMapping.getAsInt();
  }

  @Override
  public double getAsDouble() {
    return optionMapping.getAsDouble();
  }

  @Override
  public Member getAsMember() {
    return optionMapping.getAsMember();
  }

  @Override
  public User getAsUser() {
    return optionMapping.getAsUser();
  }

  @Override
  public Role getAsRole() {
    return optionMapping.getAsRole();
  }

  @Override
  public ChannelType getChannelType() {
    return optionMapping.getChannelType();
  }

  @Override
  public GuildChannelUnion getAsChannel() {
    return optionMapping.getAsChannel();
  }
}
