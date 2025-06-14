package org.example.yuzuchat.yuzu;

import org.json.JSONArray;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * GoogleIME クラスは、Google日本語入力APIを使用して
 * ローマ字の文字列をひらがな・漢字に変換する機能を提供します。
 */
public class GoogleIME {

    /**
     * Google日本語入力APIを用いてローマ字から日本語に変換する。
     *
     * @param input ユーザーが入力したローマ字の文字列
     * @return 変換後の日本語文字列。失敗した場合は元の入力を返す。
     */
    public static String convertWithGoogleIME(String input) {
        try {
            // 入力が全て小文字でない（＝大文字を含む）場合は変換対象外とし、そのまま返す
            // → コマンドや略語、顔文字などの誤変換を防ぐため
            if (!input.equals(input.toLowerCase())) {
                return input;
            }

            // URLエンコード（例："konnichiwa" → "konnichiwa"）※特殊文字対策
            String encodedText = URLEncoder.encode(input, "UTF-8");

            // Googleの日本語IME変換API（非公式）
            String apiUrl = "https://www.google.com/inputtools/request?text=" + encodedText
                    + "&itc=ja-t-i0-und&num=1&cp=0&cs=1&ie=utf-8&oe=utf-8&app=demopage";

            // Java 11+ の HttpClient を使用してリクエスト送信
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Mozilla/5.0") // Googleがブロックしないように適当なUser-Agentを指定
                    .build();

            // レスポンスを文字列として受け取る
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("[YuzuIME] API Response: " + responseBody); // デバッグ用

            // レスポンスは JSON 形式の配列
            // 例: ["SUCCESS", [["konnichiwa", ["こんにちは", ...]]]]
            JSONArray root = new JSONArray(responseBody);
            String status = root.getString(0); // "SUCCESS" かどうかを確認

            if (!"SUCCESS".equals(status)) {
                return input; // 変換失敗 → 元の文字列をそのまま返す
            }

            // 実際の変換候補が入っている配列にアクセス
            JSONArray results = root.getJSONArray(1);          // [["konnichiwa", ["こんにちは", ...]]]
            JSONArray firstResult = results.getJSONArray(0);   // ["konnichiwa", ["こんにちは", ...]]
            JSONArray candidates = firstResult.getJSONArray(1);// ["こんにちは", ...]
            String converted = candidates.getString(0);        // 最初の候補を採用

            return converted;

        } catch (Exception e) {
            // ネットワークエラー、JSON構文エラーなどが起きた場合
            e.printStackTrace();
            return input; // 変換不能 → 入力をそのまま返す
        }
    }
}
