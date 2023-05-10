package bot.utils.command.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bot.utils.command.option.Option;
import bot.utils.command.option.OptionName;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.junit.jupiter.api.Test;

class TextCommandEventTest {

  @Test
  void textCommandEvent_whenCreatedWithSingleArg_thenArgsParsed() {
    String args = "this is a single arg";
    Option option =
        new Option(
            new OptionData(
                OptionType.STRING, OptionName.PLAY_AUDIO_SONG_NAME.getDisplayName(), "test"),
            0);

    TextCommandEvent textCommandEvent =
        new TextCommandEvent(null, "-", args, null, List.of(option));

    String extractedOptionString =
        textCommandEvent.getOption(OptionName.PLAY_AUDIO_SONG_NAME).getAsString();

    assertEquals(args, extractedOptionString);
  }
}
