package com.auraim.engine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.auraim.engine.service.OverlayService;

public class MainActivity extends AppCompatActivity {

    Button btnPermission, btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        btnPermission = new Button(this);
        btnPermission.setText("1. Overlay Permission দাও");
        
        btnStart = new Button(this);
        btnStart.setText("2. AuraAim ON করো - Long Line Hack");

        // Simple layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(btnPermission);
        layout.addView(btnStart);
        setContentView(layout);

        btnPermission.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });

        btnStart.setOnClickListener(v -> {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "আগে Permission দাও ভাই!", Toast.LENGTH_SHORT).show();
                return;
            }
            startService(new Intent(MainActivity.this, OverlayService.class));
            Toast.makeText(this, "AuraAim ON! Carrom গেম চালু করো", Toast.LENGTH_LONG).show();
        });
    }
}
