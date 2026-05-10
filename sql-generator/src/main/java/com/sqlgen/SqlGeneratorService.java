package com.sqlgen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class SqlGeneratorService {

    @Value("${gemini.api.key}")
    private String apiKey;

    public SqlResponse generateSql(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=" + apiKey;

        String fullPrompt = "For the following request, generate a MySQL query. " +
                "Respond in exactly this format:\n" +
                "QUERY: <only the SQL query here>\n" +
                "EXPLANATION: <one line explanation in simple English>\n" +
                "Request: " + prompt;

        Map<String, Object> part = new HashMap<>();
        part.put("text", fullPrompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        Map response = restTemplate.postForObject(url, entity, Map.class);

        String rawResponse = extractText(response);
        return parseResponse(rawResponse);
    }

    private String extractText(Map response) {
        try {
            List candidates = (List) response.get("candidates");
            Map candidate = (Map) candidates.get(0);
            Map contentMap = (Map) candidate.get("content");
            List parts = (List) contentMap.get("parts");
            Map part = (Map) parts.get(0);
            return part.get("text").toString().trim();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private SqlResponse parseResponse(String rawResponse) {
        String query = "";
        String explanation = "";

        for (String line : rawResponse.split("\n")) {
            if (line.startsWith("QUERY:")) {
                query = line.replace("QUERY:", "").trim();
            } else if (line.startsWith("EXPLANATION:")) {
                explanation = line.replace("EXPLANATION:", "").trim();
            }
        }

        return new SqlResponse(query, explanation);
    }
}