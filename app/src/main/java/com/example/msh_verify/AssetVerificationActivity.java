package com.example.msh_verify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AssetVerificationActivity extends AppCompatActivity {

    private MaterialToolbar topAppBar;
    private TextView txtRoomHeader;
    private RecyclerView recyclerAssets;
    private MaterialButton btnFinishVerification;

    private DatabaseHelper dbHelper;
    private String roomBarcode;
    private String roomName;
    private List<DatabaseHelper.AssetModel> assetList;
    private AssetAdapter assetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_verification);

        dbHelper = new DatabaseHelper(this);

        roomBarcode = getIntent().getStringExtra("ROOM_BARCODE");
        roomName = getIntent().getStringExtra("ROOM_NAME");

        topAppBar = findViewById(R.id.topAppBar);
        txtRoomHeader = findViewById(R.id.txtRoomHeader);
        recyclerAssets = findViewById(R.id.recyclerAssets);
        btnFinishVerification = findViewById(R.id.btnFinishVerification);

        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());

        if (roomName != null && roomBarcode != null) {
            txtRoomHeader.setText("Room: " + roomName + " (" + roomBarcode + ")");
        }

        recyclerAssets.setLayoutManager(new LinearLayoutManager(this));
        loadAssets();

        btnFinishVerification.setOnClickListener(v -> {
            dbHelper.updateRoomStatus(roomBarcode, "Verified");
            Toast.makeText(this, "Room verification completed successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadAssets() {
        assetList = dbHelper.getAssetsForRoom(roomBarcode);
        assetAdapter = new AssetAdapter(assetList);
        recyclerAssets.setAdapter(assetAdapter);
    }

    private class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

        private List<DatabaseHelper.AssetModel> items;

        public AssetAdapter(List<DatabaseHelper.AssetModel> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false);
            return new AssetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
            DatabaseHelper.AssetModel asset = items.get(position);
            holder.txtAssetName.setText(asset.getName());
            holder.txtAssetBarcode.setText(asset.getBarcode());
            holder.txtAssetStatus.setText(asset.getStatus());

            if ("Verified".equalsIgnoreCase(asset.getStatus())) {
                holder.txtAssetStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                holder.btnVerifyAsset.setText("Verified");
                holder.btnVerifyAsset.setEnabled(false);
            } else {
                holder.txtAssetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                holder.btnVerifyAsset.setText("Verify");
                holder.btnVerifyAsset.setEnabled(true);
            }

            holder.btnVerifyAsset.setOnClickListener(v -> {
                dbHelper.updateAssetStatus(asset.getBarcode(), "Verified");
                asset.setStatus("Verified");
                notifyItemChanged(position);
                Toast.makeText(AssetVerificationActivity.this, "Asset verified: " + asset.getName(), Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class AssetViewHolder extends RecyclerView.ViewHolder {
            TextView txtAssetName, txtAssetBarcode, txtAssetStatus;
            MaterialButton btnVerifyAsset;

            public AssetViewHolder(@NonNull View itemView) {
                super(itemView);
                txtAssetName = itemView.findViewById(R.id.txtAssetName);
                txtAssetBarcode = itemView.findViewById(R.id.txtAssetBarcode);
                txtAssetStatus = itemView.findViewById(R.id.txtAssetStatus);
                btnVerifyAsset = itemView.findViewById(R.id.btnVerifyAsset);
            }
        }
    }
}
