package com.example.httpproxy;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public final class HttpProxyTester {
    public interface Callback { void onResult(boolean success, String message); }

    public static void test(ProxyProfile profile, String targetUrl, Callback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                if (!profile.username.isEmpty()) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(profile.username, profile.password.toCharArray());
                        }
                    });
                }
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(profile.host, profile.port));
                connection = (HttpURLConnection) new URL(targetUrl).openConnection(proxy);
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setRequestMethod("HEAD");
                int code = connection.getResponseCode();
                callback.onResult(code >= 200 && code < 500, "HTTP status: " + code);
            } catch (IOException error) {
                callback.onResult(false, error.getClass().getSimpleName() + ": " + error.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }
}
