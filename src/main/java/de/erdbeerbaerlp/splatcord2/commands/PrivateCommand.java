package de.erdbeerbaerlp.splatcord2.commands;

import de.erdbeerbaerlp.splatcord2.Main;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.Splat2Profile;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.GameRule;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon2.translations.Locale;
import de.erdbeerbaerlp.splatcord2.storage.json.splatoon3.Splat3Profile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.TimeUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public class PrivateCommand extends BaseCommand {

    public PrivateCommand(Locale l) {
        super("private", l.botLocale.cmdPrivateDesc);

        final OptionData splVersions = new OptionData(OptionType.INTEGER, "version", l.botLocale.cmdRandomModeVersion, true);
        splVersions.addChoice("Splatoon 3", 3);
        splVersions.addChoice("Splatoon 2", 2);

        final SubcommandData join = new SubcommandData("join", l.botLocale.cmdPrivateJoinDesc);
        join.addOption(OptionType.STRING, "id", l.botLocale.cmdPrivateRoomID, true);
        final SubcommandData leave = new SubcommandData("leave", l.botLocale.cmdPrivateLeaveDesc);
        final OptionData userOption = new OptionData(OptionType.USER, "user", l.botLocale.targetUser, true);
        final SubcommandData add = new SubcommandData("add", l.botLocale.cmdPrivateAddDesc);
        add.addOptions(userOption);
        final SubcommandData remove = new SubcommandData("remove", l.botLocale.cmdPrivateRemoveDesc);
        remove.addOptions(userOption);
        final SubcommandData create = new SubcommandData("create", l.botLocale.cmdPrivateCreateDesc);
        create.addOptions(splVersions);
        final SubcommandData delete = new SubcommandData("delete", l.botLocale.cmdPrivateDeleteDesc);
        final SubcommandData generate = new SubcommandData("generate", l.botLocale.cmdPrivateGenerateDesc);
        addSubcommands(join, leave, add, remove, create, delete, generate);
    }

    public static void generatePrivate(GenericInteractionCreateEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        InteractionHook cmdmsg = null;
        if (ev instanceof SlashCommandInteractionEvent e) {
            cmdmsg = e.deferReply().complete();
        } else if (ev instanceof ButtonInteractionEvent e) {
            cmdmsg = e.deferEdit().complete();
        }
        final long roomID = Main.iface.getUserRoom(ev.getUser().getIdLong());
        final int roomVersion = Main.iface.getGameVersionOfRoom(roomID);
        final ArrayList<Long> playersInRoom = Main.iface.getPlayersInRoom(roomID);
        if (playersInRoom.size() <= 1) {
            cmdmsg.editOriginal(lang.botLocale.cmdPrivateNotEnoughPlayers).queue();
            return;
        }
        Long[] playerArray = playersInRoom.toArray(new Long[0]);
        RandomCommand.shuffleArray(playerArray);
        int players, specs = 0, curPlayer = 0;
        if (playerArray.length == 3 || playerArray.length == 5 || playerArray.length == 7 || playerArray.length >= 9)
            specs = 1;
        if (playerArray.length == 10) specs = 2;
        players = playerArray.length - specs;

        final EmbedBuilder b = new EmbedBuilder();

        final StringBuilder alpha = new StringBuilder();
        final StringBuilder bravo = new StringBuilder();
        final StringBuilder spectator = new StringBuilder();
        if(roomVersion == 2) {
            for (int i = 0; i < players / 2; i++) {
                final Splat2Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat2Profile;
                alpha.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            for (int i = 0; i < players / 2; i++) {
                final Splat2Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat2Profile;
                bravo.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            for (int i = 0; i < specs; i++) {
                final Splat2Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat2Profile;
                spectator.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            b.setTitle(lang.botLocale.mode + ": " + lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name);
            b.addField(lang.botLocale.cmdRandomPrivateAlpha, alpha.toString(), true);
            b.addField(lang.botLocale.cmdRandomPrivateBravo, bravo.toString(), true);
            if (!spectator.toString().isEmpty())
                b.addField(lang.botLocale.cmdRandomPrivateSpec, spectator.toString(), true);
        }else if(roomVersion == 3){
            for (int i = 0; i < players / 2; i++) {
                final Splat3Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat3Profile;
                alpha.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            for (int i = 0; i < players / 2; i++) {
                final Splat3Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat3Profile;
                bravo.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            for (int i = 0; i < specs; i++) {
                final Splat3Profile profile = Main.getUserProfile(playerArray[curPlayer]).splat3Profile;
                spectator.append((profile.getName() != null && !profile.getName().isBlank()) ? profile.getName() : ev.getGuild().retrieveMemberById(playerArray[curPlayer]).complete().getEffectiveName());
                curPlayer++;
            }
            b.setTitle(lang.botLocale.mode + ": " + lang.rules.values().toArray(new GameRule[0])[new Random().nextInt(lang.rules.size())].name);
            b.addField(lang.botLocale.cmdRandomPrivateAlpha, alpha.toString(), true);
            b.addField(lang.botLocale.cmdRandomPrivateBravo, bravo.toString(), true);
            if (!spectator.toString().isEmpty())
                b.addField(lang.botLocale.cmdRandomPrivateSpec, spectator.toString(), true);
        }
        cmdmsg.editOriginalEmbeds(b.build()).setActionRow(Button.primary("regenprivate", lang.botLocale.regenerateButton), Button.danger("delete", Emoji.fromUnicode("U+1F5D1"))).submit();
    }

    @Override
    public boolean requiresManageServer() {
        return false;
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev) {
        final Locale lang = Main.translations.get(Main.iface.getServerLang(ev.getGuild().getIdLong()));
        if (ev.getSubcommandName() != null)
            switch (ev.getSubcommandName()) {
                case "join":
                    try {
                        final long roomToJoin = Long.parseLong(ev.getOption("id").getAsString());
                        if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                            ev.reply(lang.botLocale.cmdPrivateCannotLeaveOwn).setEphemeral(true).queue();
                        } else {
                            if (!Main.iface.roomExists(roomToJoin)) {
                                ev.reply(lang.botLocale.cmdPrivateNonExisting).setEphemeral(true).queue();
                            } else {
                                if (Main.iface.getPlayersInRoom(roomToJoin).size() >= 10) {
                                    ev.reply(lang.botLocale.cmdPrivateFull).setEphemeral(true).queue();
                                } else {
                                    Main.iface.setPlayerRoom(roomToJoin, ev.getUser().getIdLong());
                                    ev.reply(lang.botLocale.cmdPrivateJoin).setEphemeral(true).queue();
                                }

                            }
                        }
                    } catch (NumberFormatException e) {
                        ev.reply(lang.botLocale.cmdPrivateInvalidIDFormat).queue();
                    }
                    break;
                case "leave":
                    if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                        ev.reply(lang.botLocale.cmdPrivateCannotLeaveOwn).setEphemeral(true).queue();
                    } else {
                        Main.iface.setPlayerRoom(0, ev.getUser().getIdLong());
                        ev.reply(lang.botLocale.cmdPrivateLeave).setEphemeral(true).queue();
                    }
                    break;
                case "create":
                    short splVer = 3;
                    final OptionMapping versionOption = ev.getOption("version");
                    if (versionOption != null) try {
                        splVer = Short.parseShort(versionOption.getAsString());
                    } catch (NumberFormatException ignored) {
                    }
                    if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                        ev.reply(lang.botLocale.cmdPrivateCannotLeaveOwn).setEphemeral(true).queue();
                    } else {
                        long newRoom = TimeUtil.getDiscordTimestamp(Instant.now().toEpochMilli());
                        boolean newPBRoom = Main.iface.createNewPBRoom(newRoom, splVer, ev.getMember().getUser().getIdLong());
                        if (newPBRoom) {
                            ev.reply(lang.botLocale.cmdPrivateRoomCreated).addEmbeds(new EmbedBuilder().setTitle(lang.botLocale.cmdPrivateRoomID).setDescription(newRoom + "").build()).queue();
                        } else {
                            ev.reply(lang.botLocale.cmdPrivateError).setEphemeral(true).queue();
                        }
                    }
                    break;
                case "delete":
                    long ownedRoom = Main.iface.getOwnedRoom(ev.getUser().getIdLong());
                    if (ownedRoom != 0) {
                        ev.reply(Main.iface.deleteRoom(ownedRoom) ? lang.botLocale.cmdPrivateDeleted : lang.botLocale.cmdPrivateError + "").setEphemeral(true).queue();
                    } else {
                        ev.reply(lang.botLocale.cmdPrivateNotOwning).setEphemeral(true).queue();
                    }
                    break;
                case "generate":
                    generatePrivate(ev);
                    break;
                case "add":
                    try {
                        final User usr = ev.getOption("user").getAsUser();
                        final long roomToJoin = Main.iface.getUserRoom(ev.getUser().getIdLong());
                        if (Main.iface.getOwnedRoom(usr.getIdLong()) != 0) {
                            ev.reply(lang.botLocale.cmdPrivateCannotLeaveOwn).setEphemeral(true).queue();
                        } else {
                            if (!Main.iface.roomExists(roomToJoin)) {
                                ev.reply(lang.botLocale.cmdPrivateNonExisting).setEphemeral(true).queue();
                            } else {
                                if (Main.iface.getPlayersInRoom(roomToJoin).size() >= 10) {
                                    ev.reply(lang.botLocale.cmdPrivateFull).setEphemeral(true).queue();
                                } else {
                                    Main.iface.setPlayerRoom(roomToJoin, usr.getIdLong());
                                    ev.reply(lang.botLocale.cmdPrivateAdded).setEphemeral(true).queue();
                                }

                            }
                        }
                    } catch (NumberFormatException e) {
                        ev.reply(lang.botLocale.cmdPrivateInvalidIDFormat).queue();
                    }
                    break;
                case "remove":
                    final User usr = ev.getOption("user").getAsUser();
                    if (Main.iface.getOwnedRoom(ev.getUser().getIdLong()) != 0) {
                        ev.reply(lang.botLocale.cmdPrivateCannotLeaveOwn).setEphemeral(true).queue();
                    } else {
                        Main.iface.setPlayerRoom(0, usr.getIdLong());
                        ev.reply(lang.botLocale.cmdPrivateRemove).setEphemeral(true).queue();
                    }
                    break;
            }
        else System.err.println("Umm... subcommand is null...");
    }
}
