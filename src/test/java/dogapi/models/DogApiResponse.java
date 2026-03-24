package com.dogapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

public final class DogApiResponse {

    private DogApiResponse() {}

    // ── GET /breeds/list/all ──────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreedsListAll {
        public Map<String, List<String>> message;
        public String status;
    }

    // ── GET /breed/{breed}/images ─────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BreedImages {
        public List<String> message;
        public String status;
    }

    // ── GET /breeds/image/random ──────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RandomImage {
        public String message;
        public String status;
    }

    // ── Error response ────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorResponse {
        public String message;
        public String status;
        public Integer code;
    }
}
