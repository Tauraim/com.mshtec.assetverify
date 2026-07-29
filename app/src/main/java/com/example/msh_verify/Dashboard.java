package com.example.msh_verify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Dashboard extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private FloatingActionButton fabVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Views
        topAppBar = findViewById(R.id.topAppBar);
        fabVerify = findViewById(R.id.fabVerify);

        // Set Toolbar as Action Bar
        setSupportActionBar(topAppBar);

        // Navigation Icon (Menu)
        topAppBar.setNavigationOnClickListener(view ->
                Toast.makeText(this, "Navigation Drawer Coming Soon", Toast.LENGTH_SHORT).show());

        // Toolbar Menu Clicks
        topAppBar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (id == R.id.action_notification) {

                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                return true;

            } else if (id == R.id.action_profile) {

                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

        // Floating Action Button
        fabVerify.setOnClickListener(view ->

                Toast.makeText(this,
                        "Start Asset Verification",
                        Toast.LENGTH_SHORT).show()

        );
    }
}
