package bot.commands.audio;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtils
{
    static CommandEvent createMockCommandEventForAudioWithArguments(ArgumentCaptor<String> stringArgumentCaptor, String textChannelId, String memberId, String guildId, String commandArgument, AtomicBoolean messageQueued)
    {
        MessageAction mockMessageAction = mock(MessageAction.class);
        doAnswer(invocation ->
        {
            messageQueued.set(true);
            return null;
        }).when(mockMessageAction).queue();

        TextChannel mockTextChannel = mock(TextChannel.class);
        when(mockTextChannel.sendMessage(stringArgumentCaptor.capture())).thenReturn(mockMessageAction);
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
}
