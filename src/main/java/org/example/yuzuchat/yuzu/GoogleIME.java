package org.example.yuzuchat.yuzu;

import org.json.JSONArray;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class GoogleIME {
    public static String convertWithGoogleIME(String input) {
        try {
            // 入力が全て大文字 or 大文字を含むなら変換せずにそのまま返す
            if (!input.equals(input.toLowerCase())) {
                return input;
            }

            String encodedText = URLEncoder.encode(input, "UTF-8");
            String apiUrl = "https://www.google.com/inputtools/request?text=" + encodedText + "&itc=ja-t-i0-und&num=1&cp=0&cs=1&ie=utf-8&oe=utf-8&app=demopage";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("[YuzuIME] API Response: " + responseBody);

            JSONArray root = new JSONArray(responseBody);
            String status = root.getString(0);
            if (!"SUCCESS".equals(status)) {
                return input;
            }

            JSONArray results = root.getJSONArray(1);
            JSONArray firstResult = results.getJSONArray(0);
            JSONArray candidates = firstResult.getJSONArray(1);
            String converted = candidates.getString(0);

            return converted;

        } catch (Exception e) {
            e.printStackTrace();
            return input;
        }
    }


}
