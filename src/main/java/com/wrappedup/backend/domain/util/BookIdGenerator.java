package com.wrappedup.backend.domain.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BookIdGenerator {
    
    private BookIdGenerator() {}
    

    public static UUID generateUUIDFromKey(String openLibraryKey) {
        if (openLibraryKey == null || openLibraryKey.isEmpty()) {
            throw new IllegalArgumentException("OpenLibrary key cannot be null or empty");
        }
        
        String normalizedKey = normalizeOpenLibraryKey(openLibraryKey);
        
        return UUID.nameUUIDFromBytes(normalizedKey.getBytes(StandardCharsets.UTF_8));
    }
    

    private static String normalizeOpenLibraryKey(String key) {
        if (!key.startsWith("/works/")) {
            if (key.startsWith("OL") && key.contains("W")) {
                return "/works/" + key;
            }
            return key;
        }
        return key;
    }
} 