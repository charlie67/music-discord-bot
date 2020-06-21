package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import bot.repositories.GuildAliasHolderEntityRepository;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicBoolean;

import static bot.utils.TextChannelResponses.ALIAS_DELETE_ALIAS_DOES_NOT_EXIST;
import static bot.utils.TextChannelResponses.ALIAS_DELETE_ERROR_OCCURRED;
import static bot.utils.TextChannelResponses.ALIAS_DELETE_NONE_PROVIDED;
import static bot.utils.TextChannelResponses.ALIAS_REMOVED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testUtils.MockTextChannelCreator.createMockTextChannelWhereTextIsSentNoTyping;

@RunWith(MockitoJUnitRunner.class)
public class AliasDeleteCommandTests
{
    private static final String GUILD_ID = "1234";

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFailsSuccessfullyWhenNoArgIsSent()
    {
        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn("");
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);

        AliasDeleteCommand aliasDeleteCommand = new AliasDeleteCommand(null, null);
        aliasDeleteCommand.execute(mockCommandEvent);

        assertEquals(ALIAS_DELETE_NONE_PROVIDED, textChannelArgumentCaptor.getValue());
    }

    @Test
    public void testFailsSuccessfullyWhenAliasDoesNotExist()
    {
        final String ALIAS_NAME = "name";
        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        ArgumentCaptor<String> guildIdCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        GuildAliasHolder mockGuildAlisHolder = mock(GuildAliasHolder.class);

        AliasCommandEventListener mockAliasCommandEventListener = mock(AliasCommandEventListener.class);
        when(mockAliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildIdCaptor.capture())).thenReturn(mockGuildAlisHolder);

        AliasDeleteCommand aliasDeleteCommand = new AliasDeleteCommand(mockAliasCommandEventListener, null);
        aliasDeleteCommand.execute(mockCommandEvent);

        assertEquals(String.format(ALIAS_DELETE_ALIAS_DOES_NOT_EXIST, ALIAS_NAME), textChannelArgumentCaptor.getValue());
        assertEquals(GUILD_ID, guildIdCaptor.getValue());
    }

    @Test
    public void testFailsSuccessfullyWhenAliasCantBeDeleted()
    {
        final String ALIAS_NAME = "name";
        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        ArgumentCaptor<String> guildIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> aliasRemovalCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        GuildAliasHolder mockGuildAlisHolder = mock(GuildAliasHolder.class);
        doThrow(new IllegalArgumentException()).when(mockGuildAlisHolder).removeCommandWithAlias(aliasRemovalCaptor.capture());
        when(mockGuildAlisHolder.doesAliasExistForCommand(aliasRemovalCaptor.capture())).thenReturn(true);

        AliasCommandEventListener mockAliasCommandEventListener = mock(AliasCommandEventListener.class);
        when(mockAliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildIdCaptor.capture())).thenReturn(mockGuildAlisHolder);

        GuildAliasHolderEntityRepository mockGuildAliasHolderEntityRepository = mock(GuildAliasHolderEntityRepository.class);

        AliasDeleteCommand aliasDeleteCommand = new AliasDeleteCommand(mockAliasCommandEventListener,
                mockGuildAliasHolderEntityRepository);
        aliasDeleteCommand.execute(mockCommandEvent);

        assertEquals(ALIAS_DELETE_ERROR_OCCURRED, textChannelArgumentCaptor.getValue());
        assertEquals(GUILD_ID, guildIdCaptor.getValue());
        aliasRemovalCaptor.getAllValues().forEach(alias -> assertEquals(ALIAS_NAME, alias));
    }

    @Test
    public void testSuccessfullyDeleteAlias()
    {
        final String ALIAS_NAME = "name";
        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        ArgumentCaptor<String> guildIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> aliasRemovalCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        GuildAliasHolder mockGuildAlisHolder = mock(GuildAliasHolder.class);
        doAnswer(invocation -> null).when(mockGuildAlisHolder).removeCommandWithAlias(aliasRemovalCaptor.capture());
        when(mockGuildAlisHolder.doesAliasExistForCommand(aliasRemovalCaptor.capture())).thenReturn(true);

        AliasCommandEventListener mockAliasCommandEventListener = mock(AliasCommandEventListener.class);
        when(mockAliasCommandEventListener.getGuildAliasHolderForGuildWithId(guildIdCaptor.capture())).thenReturn(mockGuildAlisHolder);

        AtomicBoolean entitySaved = new AtomicBoolean(false);
        GuildAliasHolderEntityRepository mockGuildAliasHolderEntityRepository = mock(GuildAliasHolderEntityRepository.class);
        doAnswer(invocation ->
        {
            entitySaved.set(true);
            return null;
        }).when(mockGuildAliasHolderEntityRepository).save(any());

        AliasDeleteCommand aliasDeleteCommand = new AliasDeleteCommand(mockAliasCommandEventListener,
                mockGuildAliasHolderEntityRepository);
        aliasDeleteCommand.execute(mockCommandEvent);

        assertEquals(String.format(ALIAS_REMOVED, ALIAS_NAME), textChannelArgumentCaptor.getValue());
        assertEquals(GUILD_ID, guildIdCaptor.getValue());
        aliasRemovalCaptor.getAllValues().forEach(alias -> assertEquals(ALIAS_NAME, alias));
        assertTrue(entitySaved.get());
    }
}