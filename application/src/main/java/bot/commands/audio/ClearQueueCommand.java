package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClearQueueCommand extends Command {

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public ClearQueueCommand(VoiceChannelService voiceChannelService) {
    this.name = "clear";
    this.help = "Clear the queue for this server";

    this.voiceChannelService = voiceChannelService;
  }

  @Override
  protected void execute(CommandEvent event) {
    AudioPlayerSendHandler audioPlayerSendHandler;
    try {
      audioPlayerSendHandler =
          voiceChannelService.getAudioPlayerSendHandler(event.getJDA(), event.getGuild().getId());
    } catch (IllegalArgumentException e) {
      event.getChannel().sendMessage("**Not currently connected to the voice channel**").queue();
      return;
    }

    audioPlayerSendHandler.getTrackScheduler().clearQueue();
    event.reactSuccess();
  }
}
