package bot.utils.command.impl;

import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.internal.entities.AbstractMessage;
import org.jetbrains.annotations.Nullable;

public class ApiMessage extends AbstractMessage {
  String rawContent;

  public ApiMessage(String rawContent) {
    super(rawContent, "123", false);
    this.rawContent = rawContent;
  }

  @Override
  protected void unsupported() {}

  @Override
  public long getApplicationIdLong() {
    return 0;
  }

  @Nullable
  @Override
  public MessageActivity getActivity() {
    return null;
  }

  @Override
  public long getIdLong() {
    return 0;
  }
}
