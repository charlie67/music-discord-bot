package bot.commands.audio.utils;

import bot.utils.TextChannelResponses;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceChannelUtils
{
    /**
     * Connect to the voice channel that member is in
     *
     * @param member        The member to join
     * @param guild         The server that the member is in
     * @param playerManager The player manager for this guild
     * @throws IllegalArgumentException        If the voice channel can't be joined
     * @throws InsufficientPermissionException If the bot lacks the permission to join the voice channel
     */
    public static void joinVoiceChannel(Member member, Guild guild, AudioPlayerManager playerManager) throws IllegalArgumentException, InsufficientPermissionException
    {
        GuildVoiceState voiceState = member.getVoiceState();

        if (voiceState == null || !voiceState.inVoiceChannel())
        {
            throw new IllegalArgumentException("Unable to join the voice channel");
        }

        AudioManager audioManager = guild.getAudioManager();

        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler trackScheduler = new TrackScheduler();
        player.addListener(trackScheduler);

        audioManager.setSendingHandler(new AudioPlayerSendHandler(player, trackScheduler));
        audioManager.openAudioConnection(voiceState.getChannel());
    }

    public static void SearchAndPlaySong(JDA jda, String argument, String guildId, String textChannelId,
                                         String memberId, boolean playTop, AudioPlayerManager playerManager) throws IllegalArgumentException
    {
        if (guildId == null || guildId.equals(""))
        {
            throw new IllegalArgumentException("Guild ID is NULL");
        }

        if (textChannelId == null || textChannelId.equals(""))
        {
            throw new IllegalArgumentException("message channel ID is NULL");
        }

        if (memberId == null || memberId.equals(""))
        {
            throw new IllegalArgumentException("member ID is NULL");
        }


        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
        {
            throw new IllegalArgumentException("Guild is NULL is the ID correct?");
        }

        MessageChannel channel = guild.getTextChannelById(textChannelId);
        if (channel == null)
        {
            throw new IllegalArgumentException("Channel is NULL is the ID correct?");
        }

        // The user who triggered the command
        Member member = guild.getMemberById(memberId);
        if (member == null)
        {
            throw new IllegalArgumentException("Member is NULL is the ID correct?");
        }

        if (argument.isEmpty())
        {
            channel.sendMessage(TextChannelResponses.NO_ARGUMENT_PROVIDED_TO_PLAY_COMMAND).queue();
            return;
        }


        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected())
        {
            try
            {
                VoiceChannelUtils.joinVoiceChannel(member, guild, playerManager);
            }
            catch(InsufficientPermissionException e)
            {
                channel.sendMessage(TextChannelResponses.DONT_HAVE_PERMISSION_TO_JOIN_VOICE_CHANNEL).queue();
                return;
            }
            catch(IllegalArgumentException e)
            {
                channel.sendMessage(TextChannelResponses.NOT_CONNECTED_TO_VOICE_MESSAGE).queue();
                return;
            }
        }

        if (audioManager.getConnectedChannel() != null && !audioManager.getConnectedChannel().getMembers().contains(member))
        {
            channel.sendMessage(TextChannelResponses.NOT_CONNECTED_TO_VOICE_MESSAGE).queue();
            return;
        }

        channel.sendMessage("Searching for `").append(argument).append("`").queue();
        channel.sendTyping().queue();

        AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();
        TrackScheduler trackScheduler = audioPlayerSendHandler.getTrackScheduler();


        playerManager.loadItem(argument, new AudioSearchResultHandler(trackScheduler, audioPlayerSendHandler, channel
                , argument, playTop));
    }

    public static AudioPlayerSendHandler getAudioPlayerSendHandler(JDA jda, String guildId) throws IllegalArgumentException
    {
        if (guildId == null || guildId.equals(""))
        {
            throw new IllegalArgumentException("Guild ID is NULL");
        }

        Guild guild = jda.getGuildById(guildId);
        if (guild == null)
        {
            throw new IllegalArgumentException("Guild is NULL is the ID correct?");
        }

        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected())
        {
            throw new IllegalArgumentException("Not currently connected to the voice channel");
        }

        return (AudioPlayerSendHandler) audioManager.getSendingHandler();
    }
}
