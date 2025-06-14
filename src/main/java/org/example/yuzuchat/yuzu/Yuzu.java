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
        // チャットイベントなどを監視するリスナーとしてこのクラスを登録
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
        // プレイヤーが入力した元のメッセージ（ローマ字など）
        String original = event.getMessage();

        // GoogleIMEクラスを用いて変換を実行（例：「konnichiwa」→「こんにちは」）
        String converted = GoogleIME.convertWithGoogleIME(original);

        // ログに変換前後の内容を出力（デバッグ用）
        getLogger().info("Original: " + original);
        getLogger().info("Converted: " + converted);

        // 変換結果が null または空でない場合のみ処理（つまり一応変換成功したとき）
        if (converted != null && !converted.isEmpty()) {
            // 標準のチャット処理（Bukkitのフォーマットなど）をキャンセル
            event.setCancelled(true);

            // プレイヤー名を白で表示（例: Player:）
            Component playerName = Component.text(event.getPlayer().getName() + ": ", NamedTextColor.WHITE);

            // 変換結果を黄緑（ライトグリーン）で表示
            TextColor chatColor = TextColor.color(152, 251, 152);
            // ローマ字（元の入力）をグレーで表示
            TextColor romanColor = TextColor.color(105, 105, 105);

            // 表示形式：「こんにちは (konnichiwa)」のように、変換結果と元を両方表示
            Component message = Component.text(converted + " ", chatColor)
                    .append(Component.text("(" + original + ")", romanColor));

            // プレイヤー名＋メッセージを結合
            Component finalMessage = playerName.append(message);

            // サーバーに接続中の全プレイヤーにチャットメッセージとして送信
            event.getPlayer().getServer().getOnlinePlayers().forEach(p -> p.sendMessage(finalMessage));
        }
    }
}
