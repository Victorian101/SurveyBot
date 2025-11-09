package org.example;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;


public class ChatApi {
    private static final String BASE_URL = "https://app.seker.live/fm1/";
    private String lastQuestion;
    private String[] lastOptions;

    public ChatApi() {
        this.lastQuestion = null;
        this.lastOptions = new String[0];
    }


    public String sendMessage(String id, String text) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "send-message")
                    .queryString("id", id)
                    .queryString("text", text)
                    .asJson();

            System.out.println("🌍 URL: " + response.getRequestSummary());
            System.out.println("📡 Status: " + response.getStatus());
            System.out.println("📦 Body:\n" + response.getBody());

            try {
                JSONObject obj = response.getBody().getObject();
                if (obj.has("question")) {
                    this.lastQuestion = obj.optString("question", null);
                }
                if (obj.has("options")) {
                    JSONArray arr = obj.optJSONArray("options");
                    if (arr != null) {
                        String[] opts = new String[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            opts[i] = arr.optString(i, "");
                        }
                        this.lastOptions = opts;
                    }
                }
                if (this.lastQuestion == null && obj.has("extra")) {
                    String extra = obj.optString("extra", "");
                    String[] lines = extra.split("\\r?\\n");

                    for (String line : lines) {
                        line = line.trim();
                        if (line.startsWith("###") || line.startsWith("סקר") || line.startsWith("**סקר")) {
                            this.lastQuestion = line
                                    .replaceAll("###", "")
                                    .replaceAll("[*#:]", "")
                                    .trim();
                            break;
                        }
                    }
                    if (this.lastQuestion == null && lines.length > 0) {
                        this.lastQuestion = lines[0].replaceAll("[*#]", "").trim();
                    }

                    java.util.List<String> optionsList = new java.util.ArrayList<>();
                    for (String line : lines) {
                        line = line.trim();
                        if (line.matches("^[0-9]+[.)].*")) { // לדוגמה: 1. או 2)
                            String opt = line.replaceFirst("^[0-9]+[.)]\\s*", "").trim();
                            if (!opt.isEmpty()) optionsList.add(opt);
                        } else if (line.startsWith("-") || line.startsWith("–")) {
                            String opt = line.replaceFirst("[-–]\\s*", "").trim();
                            if (!opt.isEmpty()) optionsList.add(opt);
                        }
                    }

                    if (optionsList.size() > 4) {
                        optionsList = optionsList.subList(0, 4);
                    }

                    if (optionsList.isEmpty()) {
                        optionsList.add("כן");
                        optionsList.add("לא");
                        optionsList.add("לא בטוח");
                    }

                    this.lastOptions = optionsList.toArray(new String[0]);
                }

            } catch (Exception parseEx) {
                parseEx.printStackTrace();

                String raw = response.getBody().toString();
                this.lastQuestion = extractQuestion(raw);
                this.lastOptions = extractOptions(raw);
            }

            return response.getBody().toPrettyString();

        } catch (UnirestException e) {
            e.printStackTrace();
            return "❌ Error sending message: " + e.getMessage();
        }
    }


    public String clearHistory(String id) {
        try {
            HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "clear-history")
                    .header("Content-Type", "application/json")
                    .body("{\"id\":\"" + id + "\"}")
                    .asJson();

            this.lastQuestion = null;
            this.lastOptions = new String[0];

            return response.getBody().toPrettyString();

        } catch (UnirestException e) {
            e.printStackTrace();
            return "Error clearing history: " + e.getMessage();
        }


    }

    private String extractQuestion(String body) {
        if (body.contains("?")) {
            return body.split("\\?")[0] + "?";
        }
        return "Suggested question about your topic.";
    }

    private String[] extractOptions(String body) {
        return new String[]{"Option 1", "Option 2", "Option 3"};
    }


    public String getLastQuestion() {
        return lastQuestion;
    }

    public String[] getLastOptions() {
        return lastOptions;
    }
}
