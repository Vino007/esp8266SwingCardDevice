package com.example.vino.learnfragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    private Button tableBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableBtn= (Button) findViewById(R.id.tablebtn);
        tableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TableActivity.class));
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.left_fragment, new PlaceholderFragment())//container为父容器
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.right_fragment, new AnotherFragment())//container为父容器
                    .commit();
        }
    }


}
