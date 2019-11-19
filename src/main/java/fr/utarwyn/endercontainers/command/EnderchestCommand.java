package fr.utarwyn.endercontainers.command;

import fr.utarwyn.endercontainers.Managers;
import fr.utarwyn.endercontainers.configuration.Files;
import fr.utarwyn.endercontainers.enderchest.EnderChestManager;
import fr.utarwyn.endercontainers.util.MiscUtil;
import fr.utarwyn.endercontainers.util.PluginMsg;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand extends AbstractCommand {

    private EnderChestManager manager;

    public EnderchestCommand() {
        super("enderchest", "ec", "endchest");

        this.manager = Managers.get(EnderChestManager.class);

        this.addParameter(Parameter.integer().optional());
    }

    @Override
    public void performPlayer(Player player) {
        if (Files.getConfiguration().getDisabledWorlds().contains(player.getWorld().getName())) {
            PluginMsg.errorMessage(player, Files.getLocale().getPluginWorldDisabled());
            return;
        }

        Integer argument = this.readArgOrDefault(null);
        int chestNumber = (argument != null) ? argument - 1 : -1;

        if (argument != null && (chestNumber < 0 || chestNumber >= Files.getConfiguration().getMaxEnderchests())) {
            PluginMsg.accessDenied(player);
            return;
        }

        MiscUtil.runAsync(() -> {
            if (argument == null) {
                if (MiscUtil.playerHasPerm(player, "cmd.enderchests")) {
                    this.manager.openHubMenuFor(player);
                } else {
                    PluginMsg.accessDenied(player);
                }
            } else {
                if (MiscUtil.playerHasPerm(player, "cmd.enderchests") || MiscUtil.playerHasPerm(player, "cmd.enderchest." + chestNumber)) {
                    if (!this.manager.openEnderchestFor(player, chestNumber)) {
                        this.sendTo(player, ChatColor.RED + Files.getLocale().getNopermOpenChest());
                    }
                } else {
                    PluginMsg.accessDenied(player);
                }
            }
        });
    }

    @Override
    public void performConsole(CommandSender sender) {
        PluginMsg.errorMessage(sender, Files.getLocale().getNopermConsole());
    }

}
