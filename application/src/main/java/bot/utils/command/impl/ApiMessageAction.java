package bot.utils.command.impl;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiMessageAction implements MessageCreateAction {

  @NotNull
  @Override
  public MessageCreateAction setNonce(@Nullable String s) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setMessageReference(@Nullable String s) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction failOnInvalidReply(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setStickers(
      @Nullable Collection<? extends StickerSnowflake> collection) {
    return null;
  }

  @NotNull
  @Override
  public JDA getJDA() {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setCheck(@Nullable BooleanSupplier booleanSupplier) {
    return null;
  }

  @Override
  public void queue(
      @Nullable Consumer<? super Message> consumer,
      @Nullable Consumer<? super Throwable> consumer1) {}

  @Override
  public Message complete(boolean b) throws RateLimitedException {
    return null;
  }

  @NotNull
  @Override
  public CompletableFuture<Message> submit(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction addContent(@NotNull String s) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction addEmbeds(@NotNull Collection<? extends MessageEmbed> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction addComponents(
      @NotNull Collection<? extends LayoutComponent> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction addFiles(@NotNull Collection<? extends FileUpload> collection) {
    return null;
  }

  @NotNull
  @Override
  public String getContent() {
    return null;
  }

  @NotNull
  @Override
  public List<MessageEmbed> getEmbeds() {
    return null;
  }

  @NotNull
  @Override
  public List<LayoutComponent> getComponents() {
    return null;
  }

  @NotNull
  @Override
  public List<FileUpload> getAttachments() {
    return null;
  }

  @Override
  public boolean isSuppressEmbeds() {
    return false;
  }

  @NotNull
  @Override
  public Set<String> getMentionedUsers() {
    return null;
  }

  @NotNull
  @Override
  public Set<String> getMentionedRoles() {
    return null;
  }

  @NotNull
  @Override
  public EnumSet<Message.MentionType> getAllowedMentions() {
    return null;
  }

  @Override
  public boolean isMentionRepliedUser() {
    return false;
  }

  @NotNull
  @Override
  public MessageCreateAction setTTS(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setSuppressedNotifications(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setContent(@Nullable String s) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setEmbeds(@NotNull Collection<? extends MessageEmbed> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setComponents(
      @NotNull Collection<? extends LayoutComponent> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setSuppressEmbeds(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setFiles(@Nullable Collection<? extends FileUpload> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction mentionRepliedUser(boolean b) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction setAllowedMentions(
      @Nullable Collection<Message.MentionType> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction mention(@NotNull Collection<? extends IMentionable> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction mentionUsers(@NotNull Collection<String> collection) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction mentionRoles(@NotNull Collection<String> collection) {
    return null;
  }
}
