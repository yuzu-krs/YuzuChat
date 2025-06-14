package org.example.yuzuchat.yuzu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.yuzuchat.yuzu.GoogleIME;

public final class Yuzu extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("Yuzu Chat Plugin Enabled");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Yuzu Chat Plugin Disabled");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String original = event.getMessage();
        String converted = GoogleIME.convertWithGoogleIME(original);

        getLogger().info("Original: " + original);
        getLogger().info("Converted: " + converted);

        if (converted != null && !converted.isEmpty()) {
            event.setCancelled(true);

            Component playerName = Component.text(event.getPlayer().getName() + ": ", NamedTextColor.WHITE);
            TextColor chatColor = TextColor.color(152, 251, 152);
            TextColor romanColor = TextColor.color(105, 105, 105);

            // 変換結果が元と同じでも必ず「変換結果 (元の文字)」の形式にする
            Component message = Component.text(converted + " ", chatColor)
                    .append(Component.text("(" + original + ")", romanColor));

            Component finalMessage = playerName.append(message);

            event.getPlayer().getServer().getOnlinePlayers().forEach(p -> p.sendMessage(finalMessage));
        }

    }
}
