package bot.commands.audio.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;

public class VoiceChannelEventListener extends ListenerAdapter
{
    private static final String BOT_USER_ID = "567437271733633024";

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event)
    {
        Member leftMember = event.getMember();
        if (leftMember.getId().equals(BOT_USER_ID) || event.getChannelLeft().getMembers().size() == 0)
        {
            event.getGuild().getAudioManager();
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        audioPlayerSendHandler.getTrackScheduler().getQueue().clear();
        audioPlayerSendHandler.getAudioPlayer().destroy();
    }
}
