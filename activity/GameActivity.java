package com.example.administrator.five_in_a_row.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.administrator.five_in_a_row.R;
import com.example.administrator.five_in_a_row.view.Panel;

public class GameActivity extends AppCompatActivity {
    private Panel panel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        panel = (Panel) findViewById(R.id.panel_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("再来一局")){
            panel.oneMoreGame();
        }
        return super.onOptionsItemSelected(item);
    }
}
