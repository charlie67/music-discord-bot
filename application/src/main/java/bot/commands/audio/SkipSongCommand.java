package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.service.VoiceChannelService;
import bot.utils.command.Command;
import bot.utils.command.events.CommandEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static bot.utils.EmoteHelper.THUMBS_UP_STRING;
import static bot.utils.EmoteHelper.WAVE_STRING;

@Component
public class SkipSongCommand extends Command {

  private final VoiceChannelService voiceChannelService;

  @Autowired
  public SkipSongCommand(VoiceChannelService voiceChannelService) {
    this.name = "skip";
    this.help = "Skip the currently playing song";

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

    if (audioPlayerSendHandler != null) {
      // disable looping
      audioPlayerSendHandler.getTrackScheduler().setLoopTrack(null);

      audioPlayerSendHandler.getAudioPlayer().stopTrack();
      event.reactSuccessOrReply(THUMBS_UP_STRING);
    }
  }
}
