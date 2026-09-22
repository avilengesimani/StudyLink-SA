package com.example.studylinksa

import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class FlashcardActivity : AppCompatActivity() {

    private lateinit var subjectInput: EditText
    private lateinit var questionInput: EditText
    private lateinit var answerInput: EditText

    private lateinit var addButton: Button
    private lateinit var countText: TextView
    private lateinit var cardsContainer: LinearLayout

    private val flashcards = ArrayList<Flashcard>()

    private val preferencesName = "StudyLinkFlashcards"
    private val flashcardsKey = "cards"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_flashcards)

        subjectInput =
            findViewById(R.id.etFlashcardSubject)

        questionInput =
            findViewById(R.id.etFlashcardQuestion)

        answerInput =
            findViewById(R.id.etFlashcardAnswer)

        addButton =
            findViewById(R.id.btnAddFlashcard)

        countText =
            findViewById(R.id.tvFlashcardCount)

        cardsContainer =
            findViewById(R.id.cardsContainer)

        loadFlashcards()

        displayFlashcards()

        addButton.setOnClickListener {
            addFlashcard()
        }
    }

    private fun addFlashcard() {

        val subject =
            subjectInput.text.toString().trim()

        val question =
            questionInput.text.toString().trim()

        val answer =
            answerInput.text.toString().trim()

        if (subject.isEmpty() ||
            question.isEmpty() ||
            answer.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Please complete all flashcard fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val flashcard = Flashcard(
            question = question,
            answer = answer,
            subject = subject
        )

        flashcards.add(flashcard)

        saveFlashcards()

        subjectInput.text.clear()
        questionInput.text.clear()
        answerInput.text.clear()

        displayFlashcards()

        Toast.makeText(
            this,
            "Flashcard added",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun displayFlashcards() {

        cardsContainer.removeAllViews()

        countText.text =
            "My Flashcards: ${flashcards.size}"

        for (flashcard in flashcards) {

            createFlashcardView(flashcard)
        }
    }

    private fun createFlashcardView(
        flashcard: Flashcard
    ) {

        val cardLayout = LinearLayout(this)

        cardLayout.orientation =
            LinearLayout.VERTICAL

        cardLayout.setPadding(
            20,
            20,
            20,
            20
        )

        val subjectText = TextView(this)

        subjectText.text =
            "Subject: ${flashcard.subject}"

        subjectText.textSize = 16f

        subjectText.setTypeface(
            null,
            Typeface.BOLD
        )

        val questionText = TextView(this)

        questionText.text =
            "Question:\n${flashcard.question}"

        questionText.textSize = 19f

        questionText.setPadding(
            0,
            15,
            0,
            15
        )

        val answerText = TextView(this)

        answerText.text =
            "Answer:\n${flashcard.answer}"

        answerText.textSize = 18f

        answerText.visibility =
            TextView.GONE

        answerText.setPadding(
            0,
            10,
            0,
            10
        )

        val showAnswerButton =
            Button(this)

        showAnswerButton.text =
            "Show Answer"

        val knowButton =
            Button(this)

        knowButton.text =
            "✓ Know It"

        knowButton.visibility =
            Button.GONE

        val learningButton =
            Button(this)

        learningButton.text =
            "Still Learning"

        learningButton.visibility =
            Button.GONE

        showAnswerButton.setOnClickListener {

            answerText.visibility =
                TextView.VISIBLE

            knowButton.visibility =
                Button.VISIBLE

            learningButton.visibility =
                Button.VISIBLE

            showAnswerButton.visibility =
                Button.GONE
        }

        knowButton.setOnClickListener {

            Toast.makeText(
                this,
                "Great! You know this card.",
                Toast.LENGTH_SHORT
            ).show()

            knowButton.visibility =
                Button.GONE

            learningButton.visibility =
                Button.GONE
        }

        learningButton.setOnClickListener {

            Toast.makeText(
                this,
                "Keep practising this card.",
                Toast.LENGTH_SHORT
            ).show()

            knowButton.visibility =
                Button.GONE

            learningButton.visibility =
                Button.GONE
        }

        cardLayout.addView(subjectText)

        cardLayout.addView(questionText)

        cardLayout.addView(answerText)

        cardLayout.addView(showAnswerButton)

        cardLayout.addView(knowButton)

        cardLayout.addView(learningButton)

        val params =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            0,
            10,
            0,
            20
        )

        cardLayout.layoutParams = params

        cardsContainer.addView(cardLayout)
    }

    private fun saveFlashcards() {

        val jsonArray = JSONArray()

        for (flashcard in flashcards) {

            val jsonObject = JSONObject()

            jsonObject.put(
                "subject",
                flashcard.subject
            )

            jsonObject.put(
                "question",
                flashcard.question
            )

            jsonObject.put(
                "answer",
                flashcard.answer
            )

            jsonArray.put(jsonObject)
        }

        getSharedPreferences(
            preferencesName,
            MODE_PRIVATE
        )
            .edit()
            .putString(
                flashcardsKey,
                jsonArray.toString()
            )
            .apply()
    }

    private fun loadFlashcards() {

        val preferences =
            getSharedPreferences(
                preferencesName,
                MODE_PRIVATE
            )

        val savedCards =
            preferences.getString(
                flashcardsKey,
                null
            ) ?: return

        try {

            val jsonArray =
                JSONArray(savedCards)

            flashcards.clear()

            for (i in 0 until jsonArray.length()) {

                val jsonObject =
                    jsonArray.getJSONObject(i)

                val subject =
                    jsonObject.getString("subject")

                val question =
                    jsonObject.getString("question")

                val answer =
                    jsonObject.getString("answer")

                flashcards.add(
                    Flashcard(
                        subject,
                        question,
                        answer
                    )
                )
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Could not load flashcards",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}