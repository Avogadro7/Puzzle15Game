package com.example.puzzle15;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.puzzle15.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<Integer> numbers = new ArrayList<>();

    int emptyX = 0;
    int emptyY = 0;

    private Button emptyButton;
    int steps;
    private long onBackPressed;
    private boolean testingMode = false;

    private int moves;
    private boolean isFirstMovement = true;
    private long timeWhenPaused;
    private long timeForSecondActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadNumbers();
        initGame();


        binding.resetBtn.setOnClickListener(v -> {

            resetGame();
            initGame();
        });


//region MusicButton
        binding.pauseIv.setOnClickListener(v -> {
            binding.pauseIv.setImageResource(R.drawable.baseline_play_circle_outline_24);
            timeWhenPaused = binding.timerTv.getBase() - SystemClock.elapsedRealtime();
            binding.timerTv.stop();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(false)
                    .setTitle("GAME PAUSED")
                    .setMessage("Press resume to continue....")
                    .setPositiveButton("Resume", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        binding.pauseIv.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        binding.timerTv.setBase(timeWhenPaused + SystemClock.elapsedRealtime());
                        binding.timerTv.start();
                    }).create().show();


        });
//endregion

    }


//region start game

    private void loadNumbers() {
        for (int i = 1; i <= 16; i++) {
            numbers.add(i);

        }
    }

    private void initGame() {
        if (!testingMode) {
            do {
                Collections.shuffle(numbers);
                Log.d("checklist", numbers.toString());
            } while (!isSolvable(numbers));

        }
        for (int i = 0; i < binding.gridLayout.getChildCount(); i++) {
            if (numbers.get(i) == 16) {
                String tag = binding.gridLayout.getChildAt(i).getTag().toString();
                emptyX = tag.charAt(0) - '0';
                emptyY = tag.charAt(1) - '0';
                emptyButton = (Button) binding.gridLayout.getChildAt(i);
                emptyButton.setVisibility(View.INVISIBLE);
                timeForSecondActivity = binding.timerTv.getBase();
                binding.timerTv.stop();
            } else {
                ((Button) binding.gridLayout.getChildAt(i)).setText(String.valueOf(numbers.get(i)));
            }

        }
    }
    //endregion


    // region check game end
    private boolean isGameOver() {
        int counter = 1;

        for (int i = 0; i < 15; i++) {
            Button checker = (Button) binding.gridLayout.getChildAt(i);
            if (checker.getText().toString().isEmpty()) break;
            if (Integer.parseInt(checker.getText().toString()) != counter) {

                break;
            } else {
                counter++;
            }
        }
        return counter == 16;
    }

    private boolean canSwap(int clickedX, int clickedY) {
        return (Math.abs((clickedX + clickedY) - (emptyX + emptyY)) == 1 && clickedX - emptyX != 2 && clickedY - emptyY != 2);
    }

    private void updateMovementUi() {
        binding.stepsTv.setText(String.valueOf(moves));

    }
    //endregion

//region countDown timer this is not work , because I used a chronometer
//    private void gameTimer() {
//        new CountDownTimer(100000, 1000) {
//
//            @Override
//            public void onTick(long l) {
//                binding.timerTv.setText(String.valueOf(l / 1000));
//                if (l / 1000 == 10) {
//                    binding.timerTv.setTextColor(Color.RED);
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                showDialog();
//            }
//        }.start();
//    }
//endregion

    // region restart game
    private void resetGame() {
        binding.timerTv.stop();
        binding.timerTv.setBase(SystemClock.elapsedRealtime());
        isFirstMovement = true;
        moves = 0;
        updateMovementUi();
        for (int i = 0; i < binding.gridLayout.getChildCount(); i++) {
            binding.gridLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }
    //endregion


    //region check swap

    private void swap(Button clicked, int clickedX, int clickedY) {
        String text = clicked.getText().toString();
        clicked.setText("");
        clicked.setVisibility(View.INVISIBLE);
        emptyButton.setText(text);
        emptyButton.setVisibility(View.VISIBLE);
        emptyButton = clicked;
        emptyX = clickedX;
        emptyY = clickedY;
    }
    //endregion


    //region I use only win game method
    private boolean isSolvable(List<Integer> numbers) {
        int counter = 0;
        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) == 16) {
                counter += i / 4 + 1;
                continue;
            }

            for (int j = i + 1; j < numbers.size(); j++) {
                if (numbers.get(i) > numbers.get(j)) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;

    }
    //endregion

    @SuppressLint("ResourceAsColor")
    public void onClick(View view) {

        Button clicked = (Button) view;
        String tag = view.getTag().toString();

        int clickedX = tag.charAt(0) - '0';
        int clickedY = tag.charAt(1) - '0';


        if (canSwap(clickedX, clickedY)) {
            if (isFirstMovement) {
                binding.timerTv.setBase(SystemClock.elapsedRealtime());
                binding.timerTv.start();
                isFirstMovement = false;
            }
            moves++;
            updateMovementUi();
            swap(clicked, clickedX, clickedY);

        }
        if (isGameOver()) {

            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("moves", moves);
            intent.putExtra("time", timeForSecondActivity);
            startActivity(intent);
            finish();
        }
        // if (moves == 1) gameTimer();
    }


    @Override
    public void onBackPressed() {
        if (onBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
            onBackPressed = System.currentTimeMillis();
        }
    }
}