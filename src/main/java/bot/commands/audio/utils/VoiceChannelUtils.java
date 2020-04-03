package bot.commands.audio.utils;

import bot.utils.TextChannelResponses;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceChannelUtils
{
    public static void joinVoiceChannel(Member member, Guild guild, AudioPlayerManager playerManager) throws IllegalArgumentException
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

    public static void SearchAndPlaySong(JDA jda, String argument, String guildID, String textChannelId, String memberID, boolean playTop, AudioPlayerManager playerManager) throws IllegalArgumentException
    {
        if (guildID == null || guildID.equals(""))
        {
            throw new IllegalArgumentException("Guild ID is NULL");
        }

        if (textChannelId == null || textChannelId.equals(""))
        {
            throw new IllegalArgumentException("message channel ID is NULL");
        }

        if (memberID == null || memberID.equals(""))
        {
            throw new IllegalArgumentException("member ID is NULL");
        }


        Guild guild = jda.getGuildById(guildID);
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
        Member member = guild.getMemberById(memberID);
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
            } catch (IllegalArgumentException e)
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


        playerManager.loadItem(argument, new AudioSearchResultHandler(trackScheduler, audioPlayerSendHandler, channel, argument, playTop));
    }

    public static AudioPlayerSendHandler getAudioPlayerSendHandler(Guild guild) throws IllegalArgumentException
    {
        AudioManager audioManager = guild.getAudioManager();

        if (!audioManager.isConnected())
        {
            throw new IllegalArgumentException("Not currently connected to the voice channel");
        }

        return (AudioPlayerSendHandler) audioManager.getSendingHandler();
    }
}
