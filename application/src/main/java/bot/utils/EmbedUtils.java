package bot.utils;

import bot.commands.audio.utils.TrackScheduler;
import bot.utils.command.events.CommandEvent;
import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.jetbrains.annotations.NotNull;

public class EmbedUtils {
  @NotNull
  public static List<MessageEmbed> createEmbedBuilder(
      CommandEvent event, TrackScheduler trackScheduler, List<AudioTrack> queue) {

    List<MessageEmbed> embeds = new ArrayList<>();

    int counter = 1;
    for (List<AudioTrack> audioTracks : Lists.partition(queue, 10)) {
      EmbedBuilder eb = new EmbedBuilder();
      eb.setFooter(event.getMember().getAvatarUrl());

      // get a random colour for the embed
      eb.setColor(getRandomColor());

      StringBuilder sb = new StringBuilder();

      for (AudioTrack audioTrack : audioTracks) {

        sb.append(
            String.format(
                "`%d.` [%s](%s) | %s\n\n",
                counter,
                audioTrack.getInfo().title,
                audioTrack.getInfo().uri,
                TimeUtils.timeString(audioTrack.getDuration() / 1000)));
        counter++;
      }

      sb.append(
          String.format(
              "%d songs in queue | %s total duration",
              queue.size(),
              TimeUtils.timeString(trackScheduler.getQueueDurationInMilliSeconds() / 1000)));

      eb.setDescription(sb);
      embeds.add(eb.build());
    }
    return embeds;
  }

  public static void splitTextListsToSend(
      List<String> eachAliasDescription, MessageChannel channel) {
    ArrayList<String> fullMessagesToSend = new ArrayList<>();

    int index = -1;
    for (String aliasDescription : eachAliasDescription) {
      if (fullMessagesToSend.isEmpty()) {
        fullMessagesToSend.add(aliasDescription);
        index++;
      } else {
        String previousMessage = fullMessagesToSend.get(index);

        if (aliasDescription.length() + previousMessage.length() < 2000) {
          fullMessagesToSend.remove(index);
          aliasDescription = previousMessage + aliasDescription;
          fullMessagesToSend.add(aliasDescription);
        } else {
          fullMessagesToSend.add(aliasDescription);
          index++;
        }
      }
    }

    fullMessagesToSend.forEach(message -> channel.sendMessage(message).queue());
  }

  public static Color getRandomColor() {
    Random rand = new Random();
    // Java 'Color' class takes 3 floats, from 0 to 1.
    float r = rand.nextFloat();
    float g = rand.nextFloat();
    float b = rand.nextFloat();
    return new Color(r, g, b);
  }
}
