package ru.pussy_penetrator.chgk;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.pussy_penetrator.chgk.model.Question;

/**
 * Created by Sex_predator on 16.10.2016.
 */
public class QuestionFragment extends Fragment {

    private static final String QUESTION_ARG                = "question";
    private static final String SELECTED_QUESTION_GROUP_ARG = "selected_group";

    private static final String CHRONOMETER_IS_RUNNING = "chronometer_running";
    private static final String SAVE_TIME              = "save_time";
    private static final String FRAGMENT_ROTATED       = "fragment_rotated";
    private static final String BEEP_MADE              = "beep_made";
    private static final String FRAGMENT_STATE         = "fragment_state";

    private Question mQuestion;
    private int      mGroupIndex;

    private RelativeLayout mParent;
    private TextView       mNumberTextView;
    private TextView       mTextTextView;
    private Button         mShowAnswerButton;
    private TextView       mAnswerTextView;
    private LinearLayout   mAnswerBlock;
    private TextView       mCommentTextView;
    private LinearLayout   mCommentBlock;

    private LinearLayout mAnswerButtons;
    private Button       mCorrectButton;
    private Button       mWrongButton;

    private Button      mTimerButton;
    private Chronometer mChronometer;
    private GridLayout  mTimerLayout;
    private long        mSaveTime;
    private boolean     mFragmentRotated;
    private boolean     mBeep50Made;
    private int         mFragmentState;

    public static QuestionFragment newInstance(int groupIndex, Question question) {
        Bundle args = new Bundle();
        args.putSerializable(QUESTION_ARG, question);
        args.putInt(SELECTED_QUESTION_GROUP_ARG, groupIndex);

        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mQuestion = (Question) getArguments().getSerializable(QUESTION_ARG);
        mGroupIndex = getArguments().getInt(SELECTED_QUESTION_GROUP_ARG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.question_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mQuestion.setAnswerResult(Question.Answer.NOT_DEFINED, false);
                updateFragmentLayout(0);
                stopTimer();
                setTimer(0);
                return true;
            case R.id.menu_statistics:
                StatisticsDialog statisticsDialog = StatisticsDialog.newInstance(mGroupIndex);
                statisticsDialog.show(getFragmentManager(), "STATS");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_layout, container, false);

        mParent = (RelativeLayout) view.findViewById(R.id.question_fragment_parent);
        mNumberTextView = (TextView) view.findViewById(R.id.question_number);
        mTextTextView = (TextView) view.findViewById(R.id.question_text);
        mShowAnswerButton = (Button) view.findViewById(R.id.show_answer_button);
        mAnswerTextView = (TextView) view.findViewById(R.id.question_answer);
        mAnswerBlock = (LinearLayout) view.findViewById(R.id.answer_block);
        mTimerButton = (Button) view.findViewById(R.id.timer_button);
        mChronometer = (Chronometer) view.findViewById(R.id.timer_chronometer);
        mTimerLayout = (GridLayout) view.findViewById(R.id.timer_layout);
        mAnswerButtons = (LinearLayout) view.findViewById(R.id.answer_buttons);
        mCorrectButton = (Button) view.findViewById(R.id.correct_button);
        mWrongButton = (Button) view.findViewById(R.id.wrong_button);
        mCommentTextView = (TextView) view.findViewById(R.id.question_comment);
        mCommentBlock = (LinearLayout) view.findViewById(R.id.comment_block);

        mNumberTextView.setText("Вопрос # " + mQuestion.getNumber());
        mTextTextView.setText(mQuestion.getText());
        mAnswerTextView.setText(mQuestion.getAnswer());
        mCommentTextView.setText(mQuestion.getComment());

        setTimer(0);

        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                updateFragmentLayout(1);
            }
        });

        //setup timer listeners
        mTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerButton.getText().equals(getString(R.string.start)))
                    startTimer();
                else
                    pauseTimer();
            }

        });

        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long millis = SystemClock.elapsedRealtime() - chronometer.getBase() + mSaveTime;
                long sec = millis / 1000;
                setTimer(sec);

                //make sound
                if (sec >= 50 && !mBeep50Made) {
                    MediaPlayer.create(getActivity(), R.raw.beep_05_sec).start();
                    mBeep50Made = true;
                }

                //and stop timer at the end
                if (sec >= 60) {
                    stopTimer();
                    mBeep50Made = false;
                    MediaPlayer.create(getActivity(), R.raw.beep_15_sec).start();
                }
            }
        });

        //setup accept decline answer listeners
        mCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestion.setAnswerResult(Question.Answer.CORRECT, true);
                updateFragmentLayout(2);
            }
        });

        mWrongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestion.setAnswerResult(Question.Answer.WRONG, true);
                updateFragmentLayout(2);
            }
        });

        //start timer if needed
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(CHRONOMETER_IS_RUNNING))
                startTimer();
            mSaveTime = savedInstanceState.getLong(SAVE_TIME, 0);
            mFragmentRotated = savedInstanceState.getBoolean(FRAGMENT_ROTATED);
            mBeep50Made = savedInstanceState.getBoolean(BEEP_MADE);
            mFragmentState = savedInstanceState.getInt(FRAGMENT_STATE, 0);
        }

        updateFragmentLayout();
        return view;
    }

    private void updateFragmentLayout() {
        switch (mFragmentState) {
            case 0:
                mTimerLayout.setVisibility(View.VISIBLE);
                mShowAnswerButton.setVisibility(View.VISIBLE);
                mAnswerBlock.setVisibility(View.GONE);
                mCommentBlock.setVisibility(View.GONE);
                mAnswerButtons.setVisibility(View.GONE);
                break;
            case 1:
                mTimerLayout.setVisibility(View.GONE);
                mShowAnswerButton.setVisibility(View.GONE);
                mAnswerBlock.setVisibility(View.VISIBLE);
                if (mQuestion.getComment() != null)
                    mCommentBlock.setVisibility(View.VISIBLE);
                mAnswerButtons.setVisibility(View.VISIBLE);
                break;
            case 2:
                mTimerLayout.setVisibility(View.GONE);
                mShowAnswerButton.setVisibility(View.GONE);
                mAnswerBlock.setVisibility(View.VISIBLE);
                if (mQuestion.getComment() != null)
                    mCommentBlock.setVisibility(View.VISIBLE);
                mAnswerButtons.setVisibility(View.GONE);
                break;
        }

        if (mQuestion.getAnswerResult() == Question.Answer.CORRECT)
            mParent.setBackgroundColor(getResources().getColor(R.color.green));
        else if (mQuestion.getAnswerResult() == Question.Answer.WRONG)
            mParent.setBackgroundColor(getResources().getColor(R.color.red));
        else mParent.setBackgroundColor(getResources().getColor(R.color.background_default));
    }

    private void updateFragmentLayout(int fragmentState) {
        mFragmentState = fragmentState;
        updateFragmentLayout();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mTimerButton != null)
            outState.putBoolean(CHRONOMETER_IS_RUNNING,
                                !mTimerButton.getText().equals(getString(R.string.start)));
        if (mChronometer != null) {
            outState.putLong(SAVE_TIME,
                             mSaveTime + SystemClock.elapsedRealtime() - mChronometer.getBase());
            outState.putBoolean(FRAGMENT_ROTATED, true);
            outState.putBoolean(BEEP_MADE, mBeep50Made);
            outState.putInt(FRAGMENT_STATE, mFragmentState);
        }
    }

    private void setTimer(long sec) {
        if (sec < 10)
            mChronometer.setText("0" + sec);
        else
            mChronometer.setText("" + Math.min(sec, 60));

        mBeep50Made = sec > 50;
    }

    private void startTimer() {
        if (mChronometer.getText() == "00" || mChronometer.getText() == "60")
            mSaveTime = 0;
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        mTimerButton.setText(getString(R.string.pause));
    }

    private void pauseTimer() {
        mChronometer.stop();
        mTimerButton.setText(getString(R.string.start));
        mSaveTime += SystemClock.elapsedRealtime() - mChronometer.getBase();
    }

    private void stopTimer() {
        mChronometer.stop();
        mTimerButton.setText(getString(R.string.start));
        mSaveTime = 0;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mChronometer != null) {
            if (mFragmentRotated)
                mFragmentRotated = false;
            else {
                stopTimer();
                setTimer(0);
            }
        }
    }
}
