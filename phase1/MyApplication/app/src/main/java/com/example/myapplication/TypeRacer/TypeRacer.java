package com.example.myapplication.TypeRacer;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.example.myapplication.R;
import com.example.myapplication.User;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.myapplication.MainActivity.USER;


public class TypeRacer extends AppCompatActivity {

    TextView question, message, score, streak, whatever;
    private TextView countDown;
    EditText answer;
    String questionInString;
    long startTime, endTime;
    private static final long COUNTDOWN_IN_MILLS = 30000;
    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;
    private User user;
    private int questionCount =0;
    Boolean timerRunning = false;
    ArrayList<String> questions = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    ArrayList<Integer> streaks = new ArrayList<>();
    ArrayList<Integer> whatevers = new ArrayList<>();

    // 3 statistics
    private int scoresCount= 0, streaksPrev= 0, streaksCurr = 0, whateverCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_racer);
        question = (TextView) findViewById(R.id.questionTextView);
        answer = findViewById(R.id.editText2);
        message = findViewById(R.id.messageTextView);
        countDown = findViewById(R.id.countDownTextView);
        questionInString = question.getText().toString();

        // set up the view for statistics
        score = findViewById(R.id.Statistic1);
        streak = findViewById(R.id.Statistic2);
        whatever = findViewById(R.id.Statistic3);


        //User setUp
        final Intent intent = getIntent();
        final User user_1 = (User) intent.getSerializableExtra(USER);
        if (user_1 != null) {
            setUser(user_1);
        }

        //set up the color of the words.
        int trBC = intent.getIntExtra("trBC", Color.WHITE);

        int textColor = intent.getIntExtra("textColor", Color.BLACK);

        question.setTextColor(textColor);
        answer.setTextColor(textColor);
        message.setTextColor(textColor);
        countDown.setTextColor(textColor);

        score.setTextColor(textColor);
        streak.setTextColor(textColor);
        whatever.setTextColor(textColor);

        //set up the background color.
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(trBC);

        //set up the difficulty.
        int difficulty = intent.getIntExtra("difficulty", 5);

        //generate a list of questions
        for (int i = 0; i < 5; i++){
            createQuestion(difficulty);
        }

        showNextQuestion();

    }

    public void createQuestion(int d){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d; i++){
            sb.append((char) ThreadLocalRandom.current().nextInt(32, 126+1));
        }
        String q = sb.toString();
        questions.add(q);
    }

    // method called to update the statistic.
        public void updateStatistics(){

    }

    //show next question, ends if all questions completed

    private void showNextQuestion() {
        if (questionCount < questions.size()) {
            countDown.setText("Countdown starts when you first type in");
            question.setText(questions.get(questionCount));
            answer.setText("");
            answer.setEnabled(true);
            message.setText("");
            checkAnswer();
            questionCount = questionCount + 1;
        } else {
            Intent goToEndGame = new Intent(getApplicationContext(), TypeRacerEnd.class);
            goToEndGame.putExtra(USER, user);
            startActivity(goToEndGame);
        }
    }


    private void checkAnswer() {
        answer.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        String response = answer.getText().toString();
                        //start counting
                        if (response.length() == 1) {
                            startTime = System.currentTimeMillis();
                            message.setText("Started");
                            if (timerRunning) return;
                            timerRunning = true;
                            countDownTimer =
                                    new CountDownTimer(COUNTDOWN_IN_MILLS, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            countDown.setText(Long.toString(millisUntilFinished / 1000));
                                        }

                                        @Override
                                        public void onFinish() {
                                            countDown.setText("0");
                                            showNextQuestion();
                                            timerRunning = false;
                                        }
                                    }.start();
                        }

                        //goes to next question if response is correct

                        if (response.equals(question.getText().toString())) {
                            endTime = System.currentTimeMillis();
                            if (countDownTimer != null) countDownTimer.cancel();
                            timerRunning = false;
                            answer.setEnabled(false);
                            answer.clearFocus();
                            //update statistics
                            scoresCount += 1;
                            if (streaksPrev == 1){
                                streaksCurr += 1;
                            }
                            streaksPrev = 1;

                            showNextQuestion();
                        }
                        else {
                            streaksPrev = 0;
                            streaksCurr = 0;
                        }
                    }


                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
    }


    private void setUser(User new_user) {
        user = new_user;

    }

}
