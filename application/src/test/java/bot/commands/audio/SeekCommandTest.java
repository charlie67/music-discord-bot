package bot.commands.audio;

import bot.utils.TextChannelResponses;
import bot.utils.TimeUtils;
import bot.utils.command.CommandEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import testUtils.SeekCommandTestMocker;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeekCommandTest
{

    @Test
    public void seekWithHoursMinutesSeconds()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        CommandEvent mockCommandEvent = SeekCommandTestMocker.createMockCommandEventWithTime(stringArgumentCaptor,
                longArgumentCaptor, "3:02:02");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);

        assertEquals(10922000, longArgumentCaptor.getValue().longValue());
        assertEquals(String.format(TextChannelResponses.SEEKING_TO_INFORMATION, TimeUtils.timeString(10922)),
                stringArgumentCaptor.getValue());
    }

    @Test
    public void seekWithMinutesSeconds()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        CommandEvent mockCommandEvent = SeekCommandTestMocker.createMockCommandEventWithTime(stringArgumentCaptor,
                longArgumentCaptor, "02:02");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);

        assertEquals(122000, longArgumentCaptor.getValue().longValue());
        assertEquals(String.format(TextChannelResponses.SEEKING_TO_INFORMATION, TimeUtils.timeString(122)),
                stringArgumentCaptor.getValue());
    }

    @Test
    public void seekWithSeconds()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        CommandEvent mockCommandEvent = SeekCommandTestMocker.createMockCommandEventWithTime(stringArgumentCaptor,
                longArgumentCaptor, "02");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);

        assertEquals(2000, longArgumentCaptor.getValue().longValue());
        assertEquals(String.format(TextChannelResponses.SEEKING_TO_INFORMATION, TimeUtils.timeString(2)),
                stringArgumentCaptor.getValue());
    }

    @Test
    public void seekWithInvalidFormatFails()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = SeekCommandTestMocker.createMockCommandEventThatFailsWithTime(stringArgumentCaptor,
                "3:4:5:6");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);
        assertEquals(TextChannelResponses.SEEK_COMMAND_FORMAT, stringArgumentCaptor.getValue());
    }

    @Test
    public void seekWithStringFails()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent = SeekCommandTestMocker.createMockCommandEventThatFailsWithTime(stringArgumentCaptor,
                "string");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);
        assertEquals(TextChannelResponses.SEEK_COMMAND_FORMAT, stringArgumentCaptor.getValue());
    }

    @Test
    public void seekFailsWhereSeekPointLongerThanSong()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent =
                SeekCommandTestMocker.createMockCommandEventThatFailsSongTooLong(stringArgumentCaptor,
                        "32:45");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);
        assertEquals(TextChannelResponses.SEEK_POINT_LONGER_THAN_SONG, stringArgumentCaptor.getValue());
    }

    @Test
    public void seekFailsWhereSongNotSeekable()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent =
                SeekCommandTestMocker.createMockCommandEventThatFailsSongNotSeekable(stringArgumentCaptor,
                        "33:22");

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);
        assertEquals(TextChannelResponses.SEEK_POINT_LONGER_THAN_SONG, stringArgumentCaptor.getValue());
    }


    @Test
    public void failsWhenNotConnectedToVoice()
    {
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        CommandEvent mockCommandEvent =
                SeekCommandTestMocker.createMockCommandEventWhereBotNotConnected(stringArgumentCaptor);

        SeekCommand seekCommand = new SeekCommand();
        seekCommand.execute(mockCommandEvent);

        assertEquals(stringArgumentCaptor.getValue(), TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE);
    }
}