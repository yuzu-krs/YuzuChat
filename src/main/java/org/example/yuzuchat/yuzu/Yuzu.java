package org.example.yuzuchat.yuzu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.yuzuchat.yuzu.GoogleIME;

/**
 * メインプラグインクラス。
 * Yuzuチャットプラグインは、ユーザーのチャット入力をGoogle日本語入力APIを通じて変換し、
 * 変換後の文と元の入力（ローマ字）を両方表示することで、視認性を高めるチャット体験を提供します。
 */
public final class Yuzu extends JavaPlugin implements Listener {

    /**
     * プラグインが有効化されたときに呼び出されるメソッド。
     * イベントリスナーの登録などを行う。
     */
    @Override
    public void onEnable() {
        getLogger().info("Yuzu Chat Plugin Enabled");

        // configファイルがなければ作成（初回起動時など）
        saveDefaultConfig();

        // イベントリスナー登録
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * プラグインが無効化されたときに呼び出されるメソッド。
     * 終了処理などが必要であればここに記述。
     */
    @Override
    public void onDisable() {
        getLogger().info("Yuzu Chat Plugin Disabled");
    }

    /**
     * チャットイベントを監視するイベントハンドラ。
     * プレイヤーがメッセージを送信したときに呼び出され、内容を日本語に変換し、装飾付きで表示。
     *
     * @param event チャットイベント（非同期）
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String original = event.getMessage();
        String converted = GoogleIME.convertWithGoogleIME(original);

        getLogger().info("Original: " + original);
        getLogger().info("Converted: " + converted);

        if (converted != null && !converted.isEmpty()) {
            event.setCancelled(true);

            Component playerName = Component.text(event.getPlayer().getName() + ": ", NamedTextColor.WHITE);

            String chatColorName = getConfig().getString("chat-color", "light_green").toUpperCase();
            String romanColorName = getConfig().getString("roman-color", "gray").toUpperCase();

            NamedTextColor chatColor = NamedTextColor.NAMES.value(chatColorName.toLowerCase());
            NamedTextColor romanColor = NamedTextColor.NAMES.value(romanColorName.toLowerCase());

            // 無効な色名だった場合にフォールバックする
            if (chatColor == null) chatColor = NamedTextColor.GREEN;
            if (romanColor == null) romanColor = NamedTextColor.GRAY;


            Component message = Component.text(converted + " ", chatColor)
                    .append(Component.text("(" + original + ")", romanColor));

            Component finalMessage = playerName.append(message);
            event.getPlayer().getServer().getOnlinePlayers().forEach(p -> p.sendMessage(finalMessage));
        }
    }
}
