package com.example.puzzle15;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.puzzle15.databinding.ActivityGameOverBinding;

public class GameOverActivity extends AppCompatActivity {
    ActivityGameOverBinding binding;
    Animation rotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGameOverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.timeSecond.stop();
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
         binding.imageCircle.startAnimation(rotation);

        int moves = getIntent().getIntExtra("moves", 0);
        long time = getIntent().getLongExtra("time", 0);

        //region secondActivity binding buttons
        binding.timeSecond.setBase(time);

        binding.stepsSecond.setText(String.valueOf(moves));

        binding.restartGame.setOnClickListener(v->{
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);

        });
        binding.exitGame.setOnClickListener(v->{
            finishAffinity();
        });
//endregion

    }
}