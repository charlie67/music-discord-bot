package bot.utils.command.impl;

import java.util.Collection;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApiTextChannel implements TextChannel {

  @NotNull
  @Override
  public ChannelAction<TextChannel> createCopy(@NotNull Guild guild) {
    return null;
  }

  @Override
  public int getSlowmode() {
    return 0;
  }

  @NotNull
  @Override
  public Guild getGuild() {
    return null;
  }

  @NotNull
  @Override
  public TextChannelManager getManager() {
    return null;
  }

  @NotNull
  @Override
  public String getName() {
    return null;
  }

  @NotNull
  @Override
  public ChannelType getType() {
    return null;
  }

  @NotNull
  @Override
  public JDA getJDA() {
    return null;
  }

  @NotNull
  @Override
  public AuditableRestAction<Void> delete() {
    return null;
  }

  @NotNull
  @Override
  public IPermissionContainer getPermissionContainer() {
    return null;
  }

  @Override
  public int getPositionRaw() {
    return 0;
  }

  @Nullable
  @Override
  public PermissionOverride getPermissionOverride(@NotNull IPermissionHolder iPermissionHolder) {
    return null;
  }

  @NotNull
  @Override
  public List<PermissionOverride> getPermissionOverrides() {
    return null;
  }

  @NotNull
  @Override
  public PermissionOverrideAction upsertPermissionOverride(
      @NotNull IPermissionHolder iPermissionHolder) {
    return null;
  }

  @Override
  public long getParentCategoryIdLong() {
    return 0;
  }

  @Override
  public boolean isSynced() {
    return false;
  }

  @Nullable
  @Override
  public String getTopic() {
    return null;
  }

  @Override
  public boolean isNSFW() {
    return false;
  }

  @NotNull
  @Override
  public InviteAction createInvite() {
    return null;
  }

  @NotNull
  @Override
  public RestAction<List<Invite>> retrieveInvites() {
    return null;
  }

  @NotNull
  @Override
  public List<Member> getMembers() {
    return null;
  }

  @Override
  public int getDefaultThreadSlowmode() {
    return 0;
  }

  @NotNull
  @Override
  public ThreadChannelAction createThreadChannel(@NotNull String s, boolean b) {
    return null;
  }

  @NotNull
  @Override
  public ThreadChannelAction createThreadChannel(@NotNull String s, long l) {
    return null;
  }

  @NotNull
  @Override
  public ThreadChannelPaginationAction retrieveArchivedPublicThreadChannels() {
    return null;
  }

  @NotNull
  @Override
  public ThreadChannelPaginationAction retrieveArchivedPrivateThreadChannels() {
    return null;
  }

  @NotNull
  @Override
  public ThreadChannelPaginationAction retrieveArchivedPrivateJoinedThreadChannels() {
    return null;
  }

  @NotNull
  @Override
  public RestAction<List<Webhook>> retrieveWebhooks() {
    return null;
  }

  @NotNull
  @Override
  public WebhookAction createWebhook(@NotNull String s) {
    return null;
  }

  @NotNull
  @Override
  public AuditableRestAction<Void> deleteWebhookById(@NotNull String s) {
    return null;
  }

  @Override
  public boolean canTalk(@NotNull Member member) {
    return false;
  }

  @NotNull
  @Override
  public RestAction<Void> removeReactionById(
      @NotNull String s, @NotNull Emoji emoji, @NotNull User user) {
    return null;
  }

  @NotNull
  @Override
  public RestAction<Void> deleteMessagesByIds(@NotNull Collection<String> collection) {
    return null;
  }

  @NotNull
  @Override
  public RestAction<Void> clearReactionsById(@NotNull String s) {
    return null;
  }

  @NotNull
  @Override
  public RestAction<Void> clearReactionsById(@NotNull String s, @NotNull Emoji emoji) {
    return null;
  }

  @NotNull
  @Override
  public MessageCreateAction sendStickers(
      @NotNull Collection<? extends StickerSnowflake> collection) {
    return null;
  }

  @Override
  public int compareTo(@NotNull GuildChannel guildChannel) {
    return 0;
  }

  @Override
  public long getLatestMessageIdLong() {
    return 0;
  }

  @Override
  public long getIdLong() {
    return 0;
  }
}
