package com.example.studylinksa

import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizActivity : AppCompatActivity() {

    private lateinit var questionText: TextView
    private lateinit var questionNumber: TextView
    private lateinit var scoreText: TextView

    private lateinit var answer1: Button
    private lateinit var answer2: Button
    private lateinit var answer3: Button
    private lateinit var answer4: Button

    private var questions =
        listOf<QuizQuestion>()

    private var currentQuestion = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz)

        questionText =
            findViewById(R.id.tvQuestion)

        questionNumber =
            findViewById(R.id.tvQuestionNumber)

        scoreText =
            findViewById(R.id.tvScore)

        answer1 =
            findViewById(R.id.btnAnswer1)

        answer2 =
            findViewById(R.id.btnAnswer2)

        answer3 =
            findViewById(R.id.btnAnswer3)

        answer4 =
            findViewById(R.id.btnAnswer4)

        loadQuestions()
    }

    private fun loadQuestions() {

        questionText.text =
            "Loading questions..."

        RetrofitClient.api
            .getQuestions(
                amount = 10,
                difficulty = "easy",
                type = "multiple"
            )
            .enqueue(
                object : Callback<QuizResponse> {

                    override fun onResponse(
                        call: Call<QuizResponse>,
                        response: Response<QuizResponse>
                    ) {

                        if (response.isSuccessful &&
                            response.body() != null
                        ) {

                            questions =
                                response.body()!!.results

                            if (questions.isNotEmpty()) {
                                showQuestion()
                            }

                        } else {

                            questionText.text =
                                "Unable to load questions."
                        }
                    }

                    override fun onFailure(
                        call: Call<QuizResponse>,
                        t: Throwable
                    ) {

                        questionText.text =
                            "Connection error. Please check your internet connection."

                        Toast.makeText(
                            this@QuizActivity,
                            t.message ?: "API error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }

    private fun showQuestion() {

        if (currentQuestion >= questions.size) {

            questionText.text =
                "Quiz completed!"

            questionNumber.text =
                "Final Score"

            scoreText.text =
                "You scored $score out of ${questions.size}"

            answer1.isEnabled = false
            answer2.isEnabled = false
            answer3.isEnabled = false
            answer4.isEnabled = false

            return
        }

        val question =
            questions[currentQuestion]

        questionNumber.text =
            "Question ${currentQuestion + 1} of ${questions.size}"

        questionText.text =
            Html.fromHtml(
                question.question,
                Html.FROM_HTML_MODE_LEGACY
            )

        val answers =
            (question.incorrect_answers +
                    question.correct_answer)
                .shuffled()

        answer1.text =
            Html.fromHtml(
                answers[0],
                Html.FROM_HTML_MODE_LEGACY
            )

        answer2.text =
            Html.fromHtml(
                answers[1],
                Html.FROM_HTML_MODE_LEGACY
            )

        answer3.text =
            Html.fromHtml(
                answers[2],
                Html.FROM_HTML_MODE_LEGACY
            )

        answer4.text =
            Html.fromHtml(
                answers[3],
                Html.FROM_HTML_MODE_LEGACY
            )

        answer1.isEnabled = true
        answer2.isEnabled = true
        answer3.isEnabled = true
        answer4.isEnabled = true

        answer1.setOnClickListener {
            checkAnswer(
                answers[0],
                question.correct_answer
            )
        }

        answer2.setOnClickListener {
            checkAnswer(
                answers[1],
                question.correct_answer
            )
        }

        answer3.setOnClickListener {
            checkAnswer(
                answers[2],
                question.correct_answer
            )
        }

        answer4.setOnClickListener {
            checkAnswer(
                answers[3],
                question.correct_answer
            )
        }

        scoreText.text =
            "Score: $score"
    }

    private fun checkAnswer(
        selectedAnswer: String,
        correctAnswer: String
    ) {

        if (selectedAnswer == correctAnswer) {

            score++

            Toast.makeText(
                this,
                "Correct!",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            Toast.makeText(
                this,
                "Incorrect",
                Toast.LENGTH_SHORT
            ).show()
        }

        currentQuestion++

        showQuestion()
    }
}