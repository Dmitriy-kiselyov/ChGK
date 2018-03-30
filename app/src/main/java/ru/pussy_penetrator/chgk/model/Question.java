package ru.pussy_penetrator.chgk.model;

import java.io.Serializable;

/**
 * Created by Pussy_penetrator on 15.10.2016.
 */
public class Question implements Serializable {

    private final String  mNumber;
    private final String  mText;
    private final String  mAnswer;
    private       String  mComment;
    private       Answer  mAnswerResult;
    private       boolean mAnsweredDuringThisSession;

    public Question(String number, String text, String answer) {
        this(number, text, answer, Answer.NOT_DEFINED);
    }

    public Question(String number, String text, String answer, Answer answerResult) {
        mNumber = number;
        mText = text;
        mAnswer = answer;
        mComment = null;
        mAnswerResult = answerResult;
        mAnsweredDuringThisSession = false;
    }

    public String getNumber() {
        return mNumber;
    }

    public String getText() {
        return mText;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public Answer getAnswerResult() {
        return mAnswerResult;
    }

    public void setAnswerResult(Answer answerResult, boolean answeredDuringThisSession) {
        mAnswerResult = answerResult;
        mAnsweredDuringThisSession = answeredDuringThisSession;
    }

    public boolean isAnsweredDuringThisSession() {
        return mAnsweredDuringThisSession;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    @Override
    public String toString() {
        return "Question{" +
               "mNumber='" + mNumber + '\'' +
               ", mText='" + mText + '\'' +
               ", mAnswer='" + mAnswer + '\'' +
               ", mComment='" + mComment + '\'' +
               '}';
    }

    public enum Answer {NOT_DEFINED, CORRECT, WRONG}
}
