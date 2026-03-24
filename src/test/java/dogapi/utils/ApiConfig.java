package com.dogapi.utils;

public final class ApiConfig {

    private ApiConfig() {}

    // ── Base ──────────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://dog.ceo/api";

    // ── Endpoints ─────────────────────────────────────────────────────────────
    public static final String BREEDS_LIST_ALL    = "/breeds/list/all";
    public static final String BREED_IMAGES       = "/breed/{breed}/images";
    public static final String BREEDS_IMAGE_RANDOM = "/breeds/image/random";

    // ── Expected API response values ──────────────────────────────────────────
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR   = "error";

    // ── HTTP Status Codes ─────────────────────────────────────────────────────
    public static final int HTTP_OK          = 200;
    public static final int HTTP_NOT_FOUND   = 404;

    // ── Known breeds for positive test cases ─────────────────────────────────
    public static final String BREED_LABRADOR   = "labrador";
    public static final String BREED_HOUND      = "hound";
    public static final String BREED_BULLDOG    = "bulldog";
    public static final String BREED_RETRIEVER  = "retriever";
    public static final String BREED_POODLE     = "poodle";

    // ── Invalid breeds for negative test cases ────────────────────────────────
    public static final String BREED_INVALID    = "invalidbreedxyz";
    public static final String BREED_EMPTY      = " ";

    // ── URL pattern for CDN images ────────────────────────────────────────────
    public static final String IMAGE_URL_PATTERN =
            "^https://images\\.dog\\.ceo/breeds/.+\\.(jpg|jpeg|png|gif)$";

    // ── Timeouts (ms) ─────────────────────────────────────────────────────────
    public static final int CONNECTION_TIMEOUT = 10_000;
    public static final int READ_TIMEOUT       = 15_000;
}
