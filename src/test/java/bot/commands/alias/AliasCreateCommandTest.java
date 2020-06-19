package bot.commands.alias;

import bot.listeners.AliasCommandEventListener;
import bot.repositories.GuildAliasHolderEntityRepository;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static bot.utils.TextChannelResponses.ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND;
import static bot.utils.TextChannelResponses.ALIAS_CREATED;
import static bot.utils.TextChannelResponses.ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND;
import static bot.utils.TextChannelResponses.NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testUtils.MockTextChannelCreator.createMockTextChannelWhereTextIsSentNoTyping;

@RunWith(MockitoJUnitRunner.class)
public class AliasCreateCommandTest
{
    private final Set<String> ALL_CURRENT_COMMAND_NAMES = new HashSet<>();
    private final HashMap<String, Command> commandNameToCommandMap = new HashMap<>();

    private final String GUILD_ID = "445332";

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);
        ALL_CURRENT_COMMAND_NAMES.add("play");
        ALL_CURRENT_COMMAND_NAMES.add("pause");
        ALL_CURRENT_COMMAND_NAMES.add("nowplaying");
        ALL_CURRENT_COMMAND_NAMES.add("join");
        ALL_CURRENT_COMMAND_NAMES.add("leave");

        Command mockPlayCommand = mock(Command.class);

        commandNameToCommandMap.put("play", mockPlayCommand);
    }

    @Test
    public void testAliasCreatesSuccessfullyForMultiWordArguments()
    {
        final String ALIAS_NAME = "dishwasher";
        final String ALIAS_COMMAND = "play";
        final String ALIAS_ARGUMENTS = "dishwasher song";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND + " " + ALIAS_ARGUMENTS);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasCreateCommand.execute(mockCommandEvent);
        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(GUILD_ID);
        assertTrue(guildAliasHolder.doesAliasExistForCommand(ALIAS_NAME));
        Alias alias = guildAliasHolder.getCommandWithAlias(ALIAS_NAME);

        assertEquals(ALIAS_ARGUMENTS, alias.getAliasCommandArguments());

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_CREATED, ALIAS_NAME, ALIAS_COMMAND,
                ALIAS_ARGUMENTS));
    }

    @Test
    public void testAliasCreatesSuccessfullyForSingleWordArguments()
    {
        final String ALIAS_NAME = "dishwasher";
        final String ALIAS_COMMAND = "play";
        final String ALIAS_ARGUMENTS = "dishwasher";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND + " " + ALIAS_ARGUMENTS);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasCreateCommand.execute(mockCommandEvent);
        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(GUILD_ID);
        assertTrue(guildAliasHolder.doesAliasExistForCommand(ALIAS_NAME));
        Alias alias = guildAliasHolder.getCommandWithAlias(ALIAS_NAME);

        assertEquals(ALIAS_ARGUMENTS, alias.getAliasCommandArguments());

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_CREATED, ALIAS_NAME, ALIAS_COMMAND,
                ALIAS_ARGUMENTS));
    }

    @Test
    public void testAliasCreatesSuccessfullyForNoArguments()
    {
        final String ALIAS_NAME = "dishwasher";
        final String ALIAS_COMMAND = "play";
        final String ALIAS_ARGUMENTS = " ";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasCreateCommand.execute(mockCommandEvent);
        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(GUILD_ID);
        assertTrue(guildAliasHolder.doesAliasExistForCommand(ALIAS_NAME));
        Alias alias = guildAliasHolder.getCommandWithAlias(ALIAS_NAME);

        assertEquals(ALIAS_ARGUMENTS, alias.getAliasCommandArguments());

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_CREATED, ALIAS_NAME, ALIAS_COMMAND,
                ALIAS_ARGUMENTS));
    }

    @Test
    public void testAliasExecutesSuccessfully()
    {
        ArgumentCaptor<MessageReceivedEvent> messageReceivedEventArgumentCaptor =
                ArgumentCaptor.forClass(MessageReceivedEvent.class);

        final String ALIAS_NAME = "dishwasher";
        final String CALL_ALIAS_MESSAGE = "-" + ALIAS_NAME;
        final String ALIAS_COMMAND = "play";
        final String ALIAS_ARGUMENTS = "dishwasher song";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND + " " + ALIAS_ARGUMENTS);
        when(mockCommandEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockCommandEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasCreateCommand.execute(mockCommandEvent);
        GuildAliasHolder guildAliasHolder = aliasCommandEventListener.getGuildAliasHolderForGuildWithId(GUILD_ID);
        assertTrue(guildAliasHolder.doesAliasExistForCommand(ALIAS_NAME));
        Alias mockAlias = mock(Alias.class);
        doAnswer(invocation -> null).when(mockAlias).execute(messageReceivedEventArgumentCaptor.capture(), any());

        //replace the alias because it's not possible to mock out a command due to the final methods used
        guildAliasHolder.addCommandWithAlias(ALIAS_NAME, mockAlias);

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_CREATED, ALIAS_NAME, ALIAS_COMMAND,
                ALIAS_ARGUMENTS));

        MessageReceivedEvent mockMessageReceivedEvent = mock(MessageReceivedEvent.class);

        when(mockMessageReceivedEvent.getAuthor()).thenReturn(mock(User.class));
        when(mockMessageReceivedEvent.getAuthor().isBot()).thenReturn(false);
        when(mockMessageReceivedEvent.getMessage()).thenReturn(mock(Message.class));
        when(mockMessageReceivedEvent.getMessage().getContentRaw()).thenReturn("not command a message");
        when(mockMessageReceivedEvent.getGuild()).thenReturn(mock(Guild.class));
        when(mockMessageReceivedEvent.getGuild().getId()).thenReturn(GUILD_ID);

        aliasCommandEventListener.onMessageReceived(mockMessageReceivedEvent);

        when(mockMessageReceivedEvent.getMessage().getContentRaw()).thenReturn("a message");
        aliasCommandEventListener.onMessageReceived(mockMessageReceivedEvent);

        when(mockMessageReceivedEvent.getMessage().getContentRaw()).thenReturn("-play");
        aliasCommandEventListener.onMessageReceived(mockMessageReceivedEvent);

        when(mockMessageReceivedEvent.getMessage().getContentRaw()).thenReturn("-not-command");
        aliasCommandEventListener.onMessageReceived(mockMessageReceivedEvent);

        when(mockMessageReceivedEvent.getMessage().getContentRaw()).thenReturn(CALL_ALIAS_MESSAGE);
        aliasCommandEventListener.onMessageReceived(mockMessageReceivedEvent);

        assertEquals(1, messageReceivedEventArgumentCaptor.getAllValues().size());

        MessageReceivedEvent messageReceivedEvent = messageReceivedEventArgumentCaptor.getValue();
        assertEquals(CALL_ALIAS_MESSAGE, messageReceivedEvent.getMessage().getContentRaw());
    }

    @Test
    public void testAliasCantBeCreatedWithSameNameAsExistingCommand()
    {
        final String ALIAS_NAME = "play";
        final String ALIAS_COMMAND = "np";
        final String ALIAS_ARGUMENTS = " ";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND);

        aliasCreateCommand.execute(mockCommandEvent);

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_NAME_ALREADY_IN_USE_AS_COMMAND, ALIAS_NAME));
    }

    @Test
    public void testAliasCantBeCreatedWhenNotAllParametersAreProvided()
    {
        final String ALIAS_NAME = "alias_name";
        final String ALIAS_COMMAND = "";
        final String ALIAS_ARGUMENTS = "";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND);

        aliasCreateCommand.execute(mockCommandEvent);

        assertEquals(textChannelArgumentCaptor.getValue(), NEED_MORE_ARGUMENTS_TO_CREATE_AN_ALIAS);
    }

    @Test
    public void testAliasCantBeCreatedWhenCommandCantBeFound()
    {
        final String ALIAS_NAME = "alias_name";
        final String ALIAS_COMMAND = "does_not_exist";

        ArgumentCaptor<String> textChannelArgumentCaptor = ArgumentCaptor.forClass(String.class);
        TextChannel mockTextChannel = createMockTextChannelWhereTextIsSentNoTyping(textChannelArgumentCaptor);

        CommandClient mockCommandClient = mock(CommandClient.class);
        AliasCommandEventListener aliasCommandEventListener = new AliasCommandEventListener();
        aliasCommandEventListener.setCommandClient(mockCommandClient);

        AliasCreateCommand aliasCreateCommand = new AliasCreateCommand(aliasCommandEventListener,
                mock(GuildAliasHolderEntityRepository.class));
        aliasCreateCommand.setAllCurrentCommandNames(ALL_CURRENT_COMMAND_NAMES);
        aliasCreateCommand.setCommandNameToCommandMap(commandNameToCommandMap);

        CommandEvent mockCommandEvent = mock(CommandEvent.class);
        when(mockCommandEvent.getChannel()).thenReturn(mockTextChannel);
        when(mockCommandEvent.getArgs()).thenReturn(ALIAS_NAME + " " + ALIAS_COMMAND);

        aliasCreateCommand.execute(mockCommandEvent);

        assertEquals(textChannelArgumentCaptor.getValue(), String.format(ALIAS_CANT_BE_CREATED_COMMAND_NOT_FOUND,
                ALIAS_COMMAND));
    }
}
