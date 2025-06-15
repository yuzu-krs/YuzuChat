package org.example.yuzuchat.yuzu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.example.yuzuchat.yuzu.GoogleIME;

import java.util.HashSet;
import java.util.Set;

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
            // 標準チャットの視覚的な表示を抑制したいので、Recipients から全員削除
            Set<Player> recipients = new HashSet<>(event.getRecipients());
            event.getRecipients().clear(); // 表示されなくなるが、イベント自体はキャンセルしない！

            // プレイヤー名を白で表示
            Component playerName = Component.text(event.getPlayer().getName() + ": ", NamedTextColor.WHITE);

            // 設定ファイルから色を取得
            String chatColorName = getConfig().getString("chat-color", "light_green").toUpperCase();
            String romanColorName = getConfig().getString("roman-color", "gray").toUpperCase();

            NamedTextColor chatColor = NamedTextColor.NAMES.value(chatColorName.toLowerCase());
            NamedTextColor romanColor = NamedTextColor.NAMES.value(romanColorName.toLowerCase());

            // 無効な色名のフォールバック
            if (chatColor == null) chatColor = NamedTextColor.GREEN;
            if (romanColor == null) romanColor = NamedTextColor.GRAY;

            // メッセージ構築：「こんにちは (konnichiwa)」
            Component message = Component.text(converted + " ", chatColor)
                    .append(Component.text("(" + original + ")", romanColor));

            Component finalMessage = playerName.append(message);

            // 自作チャット表示（受信対象だったプレイヤー全員に送信）
            recipients.forEach(p -> p.sendMessage(finalMessage));
        }
    }

}

