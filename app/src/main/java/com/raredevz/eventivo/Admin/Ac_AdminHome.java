package com.raredevz.eventivo.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.raredevz.eventivo.R;


public class Ac_AdminHome extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
    }

    public void ManagerRequest(View view) {
        startActivity(new Intent(getApplicationContext(),Ac_ManagerRequests.class));
    }

    public void userFeedback(View view) {
    }

    public void ManageUser(View view) {
        startActivity(new Intent(getApplicationContext(),Ac_User_accounts.class));
    }
}