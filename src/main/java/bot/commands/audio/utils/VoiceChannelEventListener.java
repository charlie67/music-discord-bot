package bot.commands.audio.utils;

import bot.utils.Injector;
import bot.utils.SystemEnv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceChannelEventListener extends ListenerAdapter
{
    // 60 milliseconds
    public static final int VOICE_CHECK_DELAY = 60 * 1000;

    /**
     * The discord ID of the bot used to check if the bot is alone in the voice channel
     */
    @SystemEnv("BOT_USER_ID")
    private String BOT_USER_ID;

    public VoiceChannelEventListener()
    {
        super();

        Injector.injectSystemEnvValue(this);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event)
    {
        Member movedMember = event.getMember();

        if (!movedMember.getId().equals(BOT_USER_ID))
        {
            return;
        }

        //set a timer for 1 minute to call leaveVoiceChannel and cancel it if someone comes back
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                int membersInVoiceChannel = event.getChannelLeft().getMembers().size();

                if (membersInVoiceChannel == 1)
                {
                    leaveVoiceChannel(event);
                }
            }
        }, VOICE_CHECK_DELAY);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event)
    {
        Member leftMember = event.getMember();
        boolean leftMemberIsBot = leftMember.getId().equals(BOT_USER_ID);
        List<Member> membersLeft = event.getChannelLeft().getMembers();

        AudioManager audioManager = event.getGuild().getAudioManager();
        VoiceChannel connectedChannel = audioManager.getConnectedChannel();

        if (connectedChannel == null || !connectedChannel.getId().equals(event.getChannelLeft().getId()))
        {
            return;
        }

        boolean botAlone = membersLeft.size() == 1 && !leftMemberIsBot;

        if (leftMemberIsBot || membersLeft.size() == 0)
        {
            leaveVoiceChannel(event);
        }
        else if (botAlone)
        {
            //set a timer for 1 minute to call leaveVoiceChannel and cancel it if someone comes back
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    int membersInVoiceChannel = event.getChannelLeft().getMembers().size();

                    if (membersInVoiceChannel == 1)
                    {
                        leaveVoiceChannel(event);
                    }
                }
            }, VOICE_CHECK_DELAY);
        }
    }

    /**
     * If still connected leave the currently connected voice channel and cleanup
     *
     * @param event The voice event that triggered this handler
     */
    private void leaveVoiceChannel(@Nonnull GenericGuildVoiceEvent event)
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
