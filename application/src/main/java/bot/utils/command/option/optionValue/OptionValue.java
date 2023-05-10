package bot.utils.command.option.optionValue;

import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public interface OptionValue {

  OptionType getType();

  String getName();

  List<Message.Attachment> getAsAttachment();

  String getAsString();

  Boolean getAsBoolean();

  long getAsLong();

  int getAsInt();

  double getAsDouble();

  Member getAsMember();

  User getAsUser();

  Role getAsRole();

  ChannelType getChannelType();

  GuildChannelUnion getAsChannel();
}
