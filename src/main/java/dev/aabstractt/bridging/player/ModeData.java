package dev.aabstractt.bridging.player;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor @Data
public final class ModeData {

    private final @NonNull String name;
    private @NonNull String schematicName;

    private final @NonNull Map<String, Object> data;

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public @NonNull ModeData putString(@NonNull String key, @NonNull String value) {
        this.data.put(key, value);

        return this;
    }

    public @NonNull String getString(@NonNull String key) {
        Object value = this.data.get(key);
        if (!(value instanceof String)) {
            throw new NullPointerException("Value for key " + key + " is not a string");
        }

        return value.toString();
    }

    public @NonNull ModeData putInt(@NonNull String key, int value) {
        this.data.put(key, value);

        return this;
    }

    public int getInt(@NonNull String key) {
        Object value = this.data.get(key);
        if (!(value instanceof Integer)) {
            throw new NullPointerException("Value for key " + key + " is not an integer");
        }

        return (int) value;
    }

    public @NonNull ModeData putDouble(@NonNull String key, double value) {
        this.data.put(key, value);

        return this;
    }

    public double getDouble(@NonNull String key) {
        Object value = this.data.get(key);
        if (!(value instanceof Double)) {
            throw new NullPointerException("Value for key " + key + " is not a double");
        }

        return (double) value;
    }

    public @NonNull ModeData putBoolean(@NonNull String key, boolean value) {
        this.data.put(key, value);

        return this;
    }

    public boolean getBoolean(@NonNull String key) {
        Object value = this.data.get(key);
        if (!(value instanceof Boolean)) {
            throw new NullPointerException("Value for key " + key + " is not a boolean");
        }

        return (boolean) value;
    }
}