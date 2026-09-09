package com.example.httpproxy;

public final class ProxyProfile {
    public final String name;
    public final String host;
    public final int port;
    public final String username;
    public final String password;

    public ProxyProfile(String name, String host, int port, String username, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
