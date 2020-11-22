package bot.commands.audio;

import bot.commands.audio.utils.AudioPlayerSendHandler;
import bot.utils.TimeUtils;
import com.google.common.collect.EvictingQueue;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import static bot.utils.EmbedUtils.createEmbedBuilder;
import static bot.utils.TextChannelResponses.BOT_NOT_CONNECTED_TO_VOICE;
import static bot.utils.TextChannelResponses.CANT_DISPLAY_QUEUE_PAGE;
import static bot.utils.TextChannelResponses.NO_HISTORY_TO_SHOW;

public class HistoryCommand extends Command
{
        public HistoryCommand()
        {
            this.name = "history";
            this.help = "Show the play history";
        }

        @Override
        protected void execute(CommandEvent event)
        {
            AudioManager audioManager = event.getGuild().getAudioManager();

            AudioPlayerSendHandler audioPlayerSendHandler = (AudioPlayerSendHandler) audioManager.getSendingHandler();

            if (audioPlayerSendHandler == null)
            {
                event.getChannel().sendMessage(BOT_NOT_CONNECTED_TO_VOICE).queue();
                return;
            }

            EvictingQueue<AudioTrack> history = audioPlayerSendHandler.getTrackScheduler().getHistory();

            if (history.size() == 0)
            {
                event.getChannel().sendMessage(NO_HISTORY_TO_SHOW).queue();
                return;
            }

            try{
                EmbedBuilder eb = createEmbedBuilder(event, null, new ArrayList<>(history), false);
                event.getChannel().sendMessage(eb.build()).queue();
            } catch(NumberFormatException e) {
                event.getChannel().sendMessage(CANT_DISPLAY_QUEUE_PAGE).queue();
            }
        }
}
