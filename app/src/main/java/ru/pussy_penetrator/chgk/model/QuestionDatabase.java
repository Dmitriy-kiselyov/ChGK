package ru.pussy_penetrator.chgk.model;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Sex_predator on 15.10.2016.
 */
public class QuestionDatabase {

    private ArrayList<String>              mNames;
    private ArrayList<String>              mSaveFiles;
    private ArrayList<ArrayList<Question>> mQuestions;

    public QuestionDatabase() {
        mNames = new ArrayList<>();
        mSaveFiles = new ArrayList<>();
        mQuestions = new ArrayList<>();
    }

    protected void add(Context context, String name, String saveFilePath,
                       ArrayList<Question> questions) {
        mNames.add(name);
        mSaveFiles.add(saveFilePath);
        mQuestions.add(questions);

        //get data from save file if exists
        try {
            FileInputStream in = context.openFileInput(saveFilePath);
            for (Question question : questions)
                question.setAnswerResult(Question.Answer.values()[in.read()], false);

            Log.d("QUESTION DATABASE", "File " + saveFilePath + " is loaded!");
        }
        catch (FileNotFoundException e) {
            Log.e("QUESTION DATABASE", "File " + saveFilePath + " doe's not exist!");
        }
        catch (IOException e) {
            Log.e("QUESTION DATABASE",
                  "Something went wrong during loading data from " + saveFilePath + "!");
        }
    }

    public String getName(int index) {
        return mNames.get(index);
    }

    protected String getSaveFilePath(int index) {
        return mSaveFiles.get(index);
    }

    public boolean saveUserAnswers(Context context, int index) {
        try {
            FileOutputStream out = context
                    .openFileOutput(mSaveFiles.get(index), Context.MODE_PRIVATE);

            for (Question question : mQuestions.get(index))
                out.write(question.getAnswerResult().ordinal());

            out.close();
        }
        catch (FileNotFoundException e) {
            Log.e("QUESTION DATABASE", "Can't save data to file " + mSaveFiles.get(index) + "!");
            return false;
        }
        catch (IOException e) {
            Log.e("QUESTION DATABASE",
                  "Something went wrong during saving data to " + mSaveFiles.get(index) + "!");
            return false;
        }

        Log.d("QUESTION DATABASE", "File " + mSaveFiles.get(index) + " is updated!");
        return true;
    }

    public ArrayList<Question> getQuestions(int index) {
        return mQuestions.get(index);
    }

    public int getSize() {
        return mNames.size();
    }

    public int getQuestionCount(int index) {
        return mQuestions.get(index).size();
    }

    public int getCorrectQuestionAnswered(int index) {
        int cnt = 0;
        for (Question q : mQuestions.get(index))
            if (q.getAnswerResult() == Question.Answer.CORRECT)
                cnt++;
        return cnt;
    }

    public int getWrongQuestionAnswered(int index) {
        int cnt = 0;
        for (Question q : mQuestions.get(index))
            if (q.getAnswerResult() == Question.Answer.WRONG)
                cnt++;
        return cnt;
    }

    public int getCorrectQuestionAnswered() {
        int cnt = 0;
        for (int i = 0; i < getSize(); i++)
            cnt += getCorrectQuestionAnswered(i);
        return cnt;
    }

    public int getWrongQuestionAnswered() {
        int cnt = 0;
        for (int i = 0; i < getSize(); i++)
            cnt += getWrongQuestionAnswered(i);
        return cnt;
    }

    public int getCorrectQuestionAnsweredDuringThisSession() {
        int cnt = 0;
        for (int i = 0; i < getSize(); i++)
            for (Question q : mQuestions.get(i))
                if (q.isAnsweredDuringThisSession() &&
                    q.getAnswerResult() == Question.Answer.CORRECT)
                    cnt++;
        return cnt;
    }

    public int getWrongQuestionAnsweredDuringThisSession() {
        int cnt = 0;
        for (int i = 0; i < getSize(); i++)
            for (Question q : mQuestions.get(i))
                if (q.isAnsweredDuringThisSession() &&
                    q.getAnswerResult() == Question.Answer.WRONG)
                    cnt++;
        return cnt;
    }

}
