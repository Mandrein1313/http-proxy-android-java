package com.example.httpproxy;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int NAVY = Color.rgb(15, 18, 26);
    private static final int ACTIVE = Color.rgb(168, 200, 255);

    private ProfileStore profileStore;
    private EditText nameInput;
    private EditText hostInput;
    private EditText portInput;
    private TextView statusText;
    private TextView logPanel;
    private View mainScroll;
    private View profilesPanel;
    private boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(27, 30, 37));
        getWindow().setNavigationBarColor(Color.rgb(27, 30, 37));
        setContentView(R.layout.activity_main);

        profileStore = new ProfileStore(this);
        bindViews();
        loadProfile();
        setupInteractions();
    }

    private void bindViews() {
        nameInput = findViewById(R.id.name_input);
        hostInput = findViewById(R.id.host_input);
        portInput = findViewById(R.id.port_input);
        statusText = findViewById(R.id.connection_status);
        logPanel = findViewById(R.id.log_panel);
        mainScroll = findViewById(R.id.main_scroll);
        profilesPanel = findViewById(R.id.profiles_panel);
    }

    private void setupInteractions() {
        findViewById(R.id.tab_main).setOnClickListener(view -> showMainTab());
        findViewById(R.id.tab_log).setOnClickListener(view -> showLogTab());
        findViewById(R.id.profiles_button).setOnClickListener(view -> toggleProfilesPanel());
        findViewById(R.id.nav_home).setOnClickListener(view -> showMainTab());
        findViewById(R.id.nav_profiles).setOnClickListener(view -> {
            showMainTab();
            profilesPanel.setVisibility(View.VISIBLE);
            profilesPanel.post(() -> ((ScrollView) mainScroll).smoothScrollTo(0, profilesPanel.getBottom()));
        });
        findViewById(R.id.nav_settings).setOnClickListener(view -> Toast.makeText(this, "Settings พร้อมต่อยอดในเวอร์ชันถัดไป", Toast.LENGTH_SHORT).show());
        findViewById(R.id.nav_guide).setOnClickListener(view -> Toast.makeText(this, "Guide: บันทึกโปรไฟล์ แล้วกดปุ่มเล่นเพื่อเริ่มทดสอบ", Toast.LENGTH_LONG).show());

        findViewById(R.id.ssh_button).setOnClickListener(view -> comingSoon("SSH"));
        findViewById(R.id.v2ray_button).setOnClickListener(view -> comingSoon("V2Ray"));
        findViewById(R.id.psiphon_button).setOnClickListener(view -> comingSoon("Psiphon"));
        findViewById(R.id.openvpn_button).setOnClickListener(view -> comingSoon("OpenVPN"));
        findViewById(R.id.udp_button).setOnClickListener(view -> comingSoon("UDP Custom"));

        findViewById(R.id.save_button).setOnClickListener(view -> saveProfile());
        findViewById(R.id.test_button).setOnClickListener(view -> testProxy());
        findViewById(R.id.play_button).setOnClickListener(view -> toggleConnection());
    }

    private void loadProfile() {
        ProxyProfile profile = profileStore.load();
        if (profile == null) return;
        nameInput.setText(profile.name);
        hostInput.setText(profile.host);
        portInput.setText(String.valueOf(profile.port));
    }

    private void saveProfile() {
        String host = hostInput.getText().toString().trim();
        String rawPort = portInput.getText().toString().trim();
        if (host.isEmpty() || rawPort.isEmpty()) {
            Toast.makeText(this, "กรุณาระบุ Host และ Port", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int port = Integer.parseInt(rawPort);
            if (port < 1 || port > 65535) throw new NumberFormatException();
            ProxyProfile old = profileStore.load();
            String username = old == null ? "" : old.username;
            String password = old == null ? "" : old.password;
            profileStore.save(new ProxyProfile(nameInput.getText().toString().trim(), host, port, username, password));
            appendLog("Profile saved: " + host + ":" + port);
            Toast.makeText(this, "บันทึกโปรไฟล์แล้ว", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException error) {
            Toast.makeText(this, "Port ต้องอยู่ระหว่าง 1-65535", Toast.LENGTH_SHORT).show();
        }
    }

    private void testProxy() {
        ProxyProfile profile = profileStore.load();
        if (profile == null) {
            Toast.makeText(this, "บันทึกโปรไฟล์ก่อนทดสอบ", Toast.LENGTH_SHORT).show();
            return;
        }
        statusText.setText("●  Testing");
        appendLog("Testing HTTP Proxy: " + profile.host + ":" + profile.port);
        HttpProxyTester.test(profile, "https://example.com", (success, message) -> runOnUiThread(() -> {
            statusText.setText(success ? "●  Connected" : "●  Failed");
            appendLog(message);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }));
    }

    private void toggleConnection() {
        if (!running && profileStore.load() == null) {
            profilesPanel.setVisibility(View.VISIBLE);
            ((ScrollView) mainScroll).smoothScrollTo(0, profilesPanel.getBottom());
            Toast.makeText(this, "เพิ่มและบันทึก Proxy profile ก่อนเริ่ม", Toast.LENGTH_SHORT).show();
            return;
        }
        running = !running;
        statusText.setText(running ? "●  Connected" : "●  Idle");
        appendLog(running ? "Connection started" : "Connection stopped");
        Toast.makeText(this, running ? "เริ่มการเชื่อมต่อ" : "หยุดการเชื่อมต่อ", Toast.LENGTH_SHORT).show();
    }

    private void toggleProfilesPanel() {
        profilesPanel.setVisibility(profilesPanel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void showMainTab() {
        mainScroll.setVisibility(View.VISIBLE);
        logPanel.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tab_main)).setTextColor(ACTIVE);
        ((TextView) findViewById(R.id.tab_log)).setTextColor(Color.WHITE);
    }

    private void showLogTab() {
        mainScroll.setVisibility(View.GONE);
        logPanel.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tab_main)).setTextColor(Color.WHITE);
        ((TextView) findViewById(R.id.tab_log)).setTextColor(ACTIVE);
    }

    private void appendLog(String line) {
        String current = logPanel.getText().toString();
        if (current.startsWith("No connection log yet.")) current = "";
        logPanel.setText(current + "\n• " + line);
    }

    private void comingSoon(String feature) {
        Toast.makeText(this, feature + " module ยังไม่เปิดใช้งานใน MVP", Toast.LENGTH_SHORT).show();
    }
}
