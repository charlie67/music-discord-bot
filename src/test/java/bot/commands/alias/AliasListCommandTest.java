package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static bot.utils.TextChannelResponses.NO_ALIASES_SET;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testUtils.MockTextChannelCreator.createMockTextChannelWhereTextIsSentNoTyping;

@RunWith(MockitoJUnitRunner.class)
public class AliasListCommandTest
{
    private final String GUILD_ID = "445332";

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testListReturnsWhenThereAreNoAliases()
    {
        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasListCommand aliasListCommand = new AliasListCommand(aliasCommandEventListener);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasListCommand.execute(mockCommandEvent);

        assertEquals(NO_ALIASES_SET, textChannelArgumentCaptor.getValue());
    }

    @Test
    public void testListReturnsSingleAlias()
    {
        final String ALIAS_NAME_1 = "NAME";
        final String ALIAS_COMMAND_ARGUMENTS_1 = "COMMAND ARGUMENTS";
        final String COMMAND_NAME_1 = "PLAY";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        GuildAliasHolder guildAliasHolder = new GuildAliasHolder();

        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn(COMMAND_NAME_1);
        Alias mockAlias = mock(Alias.class);
        when(mockAlias.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_1);
        when(mockAlias.getAliasName()).thenReturn(ALIAS_NAME_1);
        when(mockAlias.getCommand()).thenReturn(mockCommand);

        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_1, mockAlias);
        aliasCommandEventListener.putGuildAliasHolderForGuildWithId(GUILD_ID, guildAliasHolder);

        AliasListCommand aliasListCommand = new AliasListCommand(aliasCommandEventListener);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasListCommand.execute(mockCommandEvent);

        assertEquals(String.format("`1:` `%s` executes command `%s` with arguments `%s`\n", ALIAS_NAME_1, COMMAND_NAME_1,
                ALIAS_COMMAND_ARGUMENTS_1),
                textChannelArgumentCaptor.getValue());
    }

    @Test
    public void testListReturnsMultipleAliases()
    {
        final String ALIAS_NAME_1 = "NAME";
        final String ALIAS_COMMAND_ARGUMENTS_1 = "COMMAND ARGUMENTS";
        final String COMMAND_NAME_1 = "PLAY";

        final String ALIAS_NAME_2 = "NAME_2";
        final String ALIAS_COMMAND_ARGUMENTS_2 = "COMMAND ARGUMENTS 2";
        final String COMMAND_NAME_2 = "PAUSE";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        GuildAliasHolder guildAliasHolder = new GuildAliasHolder();

        Command mockCommand1 = mock(Command.class);
        when(mockCommand1.getName()).thenReturn(COMMAND_NAME_1);
        Alias mockAlias1 = mock(Alias.class);
        when(mockAlias1.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_1);
        when(mockAlias1.getAliasName()).thenReturn(ALIAS_NAME_1);
        when(mockAlias1.getCommand()).thenReturn(mockCommand1);

        Command mockCommand2 = mock(Command.class);
        when(mockCommand2.getName()).thenReturn(COMMAND_NAME_2);
        Alias mockAlias2 = mock(Alias.class);
        when(mockAlias2.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_2);
        when(mockAlias2.getAliasName()).thenReturn(ALIAS_NAME_2);
        when(mockAlias2.getCommand()).thenReturn(mockCommand2);

        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_1, mockAlias1);
        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_2, mockAlias2);
        aliasCommandEventListener.putGuildAliasHolderForGuildWithId(GUILD_ID, guildAliasHolder);

        AliasListCommand aliasListCommand = new AliasListCommand(aliasCommandEventListener);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasListCommand.execute(mockCommandEvent);

        assertEquals(String.format("`1:` `%s` executes command `%s` with arguments `%s`\n`2:` `%s` executes command `%s` " +
                        "with arguments `%s`\n", ALIAS_NAME_2, COMMAND_NAME_2, ALIAS_COMMAND_ARGUMENTS_2, ALIAS_NAME_1,
                COMMAND_NAME_1, ALIAS_COMMAND_ARGUMENTS_1), textChannelArgumentCaptor.getValue());
    }

    @Test
    public void testListReturnsMultipleAliasesWhenTheyAreOver2000Characters()
    {
        final String ALIAS_NAME_1 = "NAME";
        final String ALIAS_COMMAND_ARGUMENTS_1 = "COMMAND ARGUMENTS";
        final String COMMAND_NAME_1 = "PLAY";

        final String ALIAS_NAME_2 = "NAME_2";
        final String ALIAS_COMMAND_ARGUMENTS_2 = "COMMAND ARGUMENTS 2";
        final String COMMAND_NAME_2 = "PAUSE";

        final String ALIAS_NAME_3 = "NAME_3";
        final String ALIAS_COMMAND_ARGUMENTS_3 = "So over the next several days we’ll be rolling out these initiatives. " +
                "We’ll be making sure that people have a very clear understanding of what can make a difference and what " +
                "we can do. And although we have to be very clear that this is not going to solve every violent crime in " +
                "this country it’s not going to prevent every mass shooting it’s not going to keep every gun out of the " +
                "hands of a criminal it will potentially save lives and spare families the pain and the extraordinary loss" +
                " that they’ve suffered as a consequence of a firearm getting in the hands of the wrong people. I’m also " +
                "confident that the recommendations that are being made by my team here are ones that are entirely " +
                "consistent with the Second Amendment and people’s lawful right to bear arms. And we’ve been very careful " +
                "recognizing that although we have a strong tradition of gun ownership in this country that even though " +
                "it’s who possess firearms for hunting for self-protection and for other legitimate reasons I want to make" +
                " sure that the wrong people don’t have them for the wrong reasons. Thank you very much everybody. " +
                "Regardless of where you stand on the matter we have to change some things. l be making sure that people " +
                "have a very clear understanding of what can make a difference and what we can do. And although we have to" +
                " be very clear that this is not going to solve every violent crime in this country it’s not going to " +
                "prevent every mass shooting it’s not going to keep every gun out of the hands of a criminal it will " +
                "potentially save lives and spare families the pain and the extraordinary loss that they’ve suffered as a " +
                "consequence of a firearm getting in the hands of the wrong people. Regardless of where you stand on the " +
                "matter we have to change some things. l be making sure that people have a very clear understanding of " +
                "what can make a difference and what we can do. And although we And although we And although we And " +
                "although we";
        final String COMMAND_NAME_3 = "ECHO";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        GuildAliasHolder guildAliasHolder = new GuildAliasHolder();

        Command mockCommand1 = mock(Command.class);
        when(mockCommand1.getName()).thenReturn(COMMAND_NAME_1);
        Alias mockAlias1 = mock(Alias.class);
        when(mockAlias1.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_1);
        when(mockAlias1.getAliasName()).thenReturn(ALIAS_NAME_1);
        when(mockAlias1.getCommand()).thenReturn(mockCommand1);

        Command mockCommand2 = mock(Command.class);
        when(mockCommand2.getName()).thenReturn(COMMAND_NAME_2);
        Alias mockAlias2 = mock(Alias.class);
        when(mockAlias2.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_2);
        when(mockAlias2.getAliasName()).thenReturn(ALIAS_NAME_2);
        when(mockAlias2.getCommand()).thenReturn(mockCommand2);

        Command mockCommand3 = mock(Command.class);
        when(mockCommand3.getName()).thenReturn(COMMAND_NAME_3);
        Alias mockAlias3 = mock(Alias.class);
        when(mockAlias3.getAliasCommandArguments()).thenReturn(ALIAS_COMMAND_ARGUMENTS_3);
        when(mockAlias3.getAliasName()).thenReturn(ALIAS_NAME_3);
        when(mockAlias3.getCommand()).thenReturn(mockCommand3);

        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_1, mockAlias1);
        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_2, mockAlias2);
        guildAliasHolder.addCommandWithAlias(ALIAS_NAME_3, mockAlias3);
        aliasCommandEventListener.putGuildAliasHolderForGuildWithId(GUILD_ID, guildAliasHolder);

        AliasListCommand aliasListCommand = new AliasListCommand(aliasCommandEventListener);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasListCommand.execute(mockCommandEvent);

        List<String> allMessages = textChannelArgumentCaptor.getAllValues();

        assertEquals(3, allMessages.size());

        assertEquals(String.format("`3:` `%s` executes command `%s` with arguments `%s`\n", ALIAS_NAME_1, COMMAND_NAME_1,
                ALIAS_COMMAND_ARGUMENTS_1), allMessages.get(2));

        assertEquals(String.format("`2:` `%s` executes command `%s` with arguments `%s`\n", ALIAS_NAME_3, COMMAND_NAME_3,
                ALIAS_COMMAND_ARGUMENTS_3), allMessages.get(1));

        assertEquals(String.format("`1:` `%s` executes command `%s` with arguments `%s`\n", ALIAS_NAME_2, COMMAND_NAME_2,
                ALIAS_COMMAND_ARGUMENTS_2), allMessages.get(0));

    }
}
