package testUtils;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SeekCommandTestMocker
{
    public static CommandEvent createMockCommandEventWhereBotNotConnected(ArgumentCaptor<String> stringArgumentCaptor)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(false);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventWithTime(ArgumentCaptor<String> stringArgumentCaptor,
                                                              ArgumentCaptor<Long> longArgumentCaptor,
                                                              String timeArgument)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        AudioTrack mockAudioTrack = mock(AudioTrack.class);
        doAnswer(invocation -> null).when(mockAudioTrack).setPosition(longArgumentCaptor.capture());
        when(mockAudioTrack.getDuration()).thenReturn(Long.valueOf(999999999));
        when(mockAudioTrack.isSeekable()).thenReturn(true);

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(mockAudioTrack);

        AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);
        when(mockAudioPlayerSendHandler.getAudioPlayer()).thenReturn(mockAudioPlayer);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(timeArgument);

        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventThatFailsWithTime(ArgumentCaptor<String> stringArgumentCaptor,
                                                                       String timeArgument)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        AudioTrack mockAudioTrack = mock(AudioTrack.class);
        when(mockAudioTrack.getDuration()).thenReturn(Long.valueOf(999999999));
        when(mockAudioTrack.isSeekable()).thenReturn(true);

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(mockAudioTrack);

        AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);
        when(mockAudioPlayerSendHandler.getAudioPlayer()).thenReturn(mockAudioPlayer);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(timeArgument);

        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventThatFailsSongTooLong(ArgumentCaptor<String> stringArgumentCaptor,
                                                                          String timeArgument)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        AudioTrack mockAudioTrack = mock(AudioTrack.class);
        when(mockAudioTrack.getDuration()).thenReturn(Long.valueOf(0));
        when(mockAudioTrack.isSeekable()).thenReturn(true);

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(mockAudioTrack);

        AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);
        when(mockAudioPlayerSendHandler.getAudioPlayer()).thenReturn(mockAudioPlayer);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(timeArgument);

        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventThatFailsSongNotSeekable(ArgumentCaptor<String> stringArgumentCaptor,
                                                                              String timeArgument)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);

        AudioTrack mockAudioTrack = mock(AudioTrack.class);
        when(mockAudioTrack.getDuration()).thenReturn(Long.valueOf(999999999));
        when(mockAudioTrack.isSeekable()).thenReturn(false);

        AudioPlayer mockAudioPlayer = mock(AudioPlayer.class);
        when(mockAudioPlayer.getPlayingTrack()).thenReturn(mockAudioTrack);

        AudioPlayerSendHandler mockAudioPlayerSendHandler = mock(AudioPlayerSendHandler.class);
        when(mockAudioPlayerSendHandler.getAudioPlayer()).thenReturn(mockAudioPlayer);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(true);
        when(mockAudioManager.getSendingHandler()).thenReturn(mockAudioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(timeArgument);

        return mockCommandEvent;
    }
}
