package testUtils;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AudioTestMocker
{
    public static CommandEvent createMockCommandEventForPlayCommandWhereItErrorsOut(String textChannelId,
                                                                                    String memberId,
                                                                                    String guildId,
                                                                                    String commandArgument)
    {
        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.getId()).thenReturn(textChannelId);

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getId()).thenReturn(guildId);
        when(mockGuild.getTextChannelById(anyString())).thenReturn(mockTextChannel);
        when(mockGuild.getMemberById(anyString())).thenReturn(mockMember);

        JDA mockJDA = mock(JDA.class);
        when(mockJDA.getGuildById(anyString())).thenReturn(mockGuild);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getJDA()).thenReturn(mockJDA);
        when(mockCommandEvent.getArgs()).thenReturn(commandArgument);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getMember()).thenReturn(mockMember);

        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventForPlayCommandWhereItErrorsOut(ArgumentCaptor<String> stringArgumentCaptor,
                                                                                    String textChannelId,
                                                                                    String memberId,
                                                                                    String guildId,
                                                                                    String commandArgument,
                                                                                    Answer<Void> messageQueuedAnswer)
    {
        CommandEvent mockCommandEvent = createMockCommandEventForPlayCommandWhereItErrorsOut(
                textChannelId, memberId, guildId, commandArgument);

        TextChannel mockTextChannel = (TextChannel) mockCommandEvent.getChannel();

        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(messageQueuedAnswer).when(mockMessageAction).queue();

        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
        when(mockTextChannel.getId()).thenReturn(textChannelId);

        return mockCommandEvent;
    }

    public static CommandEvent createMockCommandEventForPlayCommandWhereAudioGetsPlayed(ArgumentCaptor<String> stringArgumentCaptor,
                                                                                        String textChannelId,
                                                                                        String memberId,
                                                                                        String guildId,
                                                                                        String commandArgument,
                                                                                        boolean isVoiceConnected,
                                                                                        AudioSendHandler audioPlayerSendHandler,
                                                                                        ArgumentCaptor<MessageEmbed> messageEmbedArgumentCaptor,
                                                                                        Answer<Void> messageQueuedAnswer)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation -> mockMessageAction).when(mockMessageAction).append(stringArgumentCaptor.capture());

        RestAction mockRestAction = mock(RestAction.class);
        doAnswer(invocation -> null).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
        when(mockTextChannel.sendMessage(messageEmbedArgumentCaptor.capture())).thenReturn(mockMessageAction);
        when(mockTextChannel.getId()).thenReturn(textChannelId);
        when(mockTextChannel.sendTyping()).thenReturn(mockRestAction);

        Member mockMember = mock(Member.class);
        when(mockMember.getId()).thenReturn(memberId);

        VoiceChannel mockVoiceChannel = mock(VoiceChannel.class);
        ArrayList<Member> memberList = new ArrayList<>();
        memberList.add(mockMember);
        when(mockVoiceChannel.getMembers()).thenReturn(memberList);

        AudioManager mockAudioManager = mock(AudioManager.class);
        when(mockAudioManager.isConnected()).thenReturn(isVoiceConnected);
        when(mockAudioManager.getConnectedChannel()).thenReturn(mockVoiceChannel);
        when(mockAudioManager.getSendingHandler()).thenReturn(audioPlayerSendHandler);

        Guild mockGuild = mock(Guild.class);
        when(mockGuild.getId()).thenReturn(guildId);
        when(mockGuild.getTextChannelById(anyString())).thenReturn(mockTextChannel);
        when(mockGuild.getMemberById(anyString())).thenReturn(mockMember);
        when(mockGuild.getAudioManager()).thenReturn(mockAudioManager);

        JDA mockJDA = mock(JDA.class);
        when(mockJDA.getGuildById(anyString())).thenReturn(mockGuild);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getJDA()).thenReturn(mockJDA);
        when(mockCommandEvent.getArgs()).thenReturn(commandArgument);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mockGuild);
        when(mockCommandEvent.getMember()).thenReturn(mockMember);

        return mockCommandEvent;
    }
}
