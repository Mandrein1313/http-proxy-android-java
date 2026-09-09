package com.example.httpproxy;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class ProfileStore {
    private static final String FILE = "proxy_profile_secure";
    private final SharedPreferences preferences;

    public ProfileStore(Context context) {
        try {
            MasterKey key = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            preferences = EncryptedSharedPreferences.create(
                    context,
                    FILE,
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception error) {
            throw new IllegalStateException("Cannot initialize encrypted profile storage", error);
        }
    }

    public void save(ProxyProfile profile) {
        preferences.edit()
                .putString("name", profile.name)
                .putString("host", profile.host)
                .putInt("port", profile.port)
                .putString("username", profile.username)
                .putString("password", profile.password)
                .apply();
    }

    public ProxyProfile load() {
        String host = preferences.getString("host", "");
        if (host.isEmpty()) return null;
        return new ProxyProfile(
                preferences.getString("name", "Proxy หลัก"),
                host,
                preferences.getInt("port", 8080),
                preferences.getString("username", ""),
                preferences.getString("password", "")
        );
    }
}
