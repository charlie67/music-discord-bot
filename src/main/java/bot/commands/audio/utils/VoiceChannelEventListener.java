package bot.commands.audio.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.util.List;

public class VoiceChannelEventListener extends ListenerAdapter
{
    private static final String BOT_USER_ID = "567437271733633024";

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event)
    {
        Member leftMember = event.getMember();
        boolean leftMemberIsBot = leftMember.getId().equals(BOT_USER_ID);
        List<Member> membersLeft = event.getChannelLeft().getMembers();

        boolean botAlone = membersLeft.size() == 1 && !leftMemberIsBot;

        if (leftMemberIsBot || membersLeft.size() == 0 || botAlone)
        {
            AudioManager audioManager = event.getGuild().getAudioManager();

            AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();

            if (audioPlayerSendHandler == null)
            {
                return;
            }

            audioManager.closeAudioConnection();
            audioPlayerSendHandler.getTrackScheduler().getQueue().clear();
            audioPlayerSendHandler.getAudioPlayer().destroy();
        }
    }
}
