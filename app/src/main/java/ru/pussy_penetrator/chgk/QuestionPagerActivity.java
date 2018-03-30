package ru.pussy_penetrator.chgk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.pussy_penetrator.chgk.model.QuestionDatabase;
import ru.pussy_penetrator.chgk.model.QuestionDatabaseLab;

/**
 * Created by Sex_predator on 16.10.2016.
 */
public class QuestionPagerActivity extends AppCompatActivity {

    private static final String SELECTED_QUESTION_GROUD_ARG = "selected_group";
    private static final String SELECTED_QUESTION_ARG       = "selected_question";

    private int              mSelectedQuestionGroup;
    private QuestionDatabase mQuestionDatabase;

    private ViewPager mViewPager;

    public static Intent newIntent(Context context, int selectedQuestionGroup,
                                   int selectedQuestion) {
        Intent intent = new Intent(context, QuestionPagerActivity.class);
        intent.putExtra(SELECTED_QUESTION_GROUD_ARG, selectedQuestionGroup);
        intent.putExtra(SELECTED_QUESTION_ARG, selectedQuestion);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager_layout);

        mSelectedQuestionGroup = getIntent().getIntExtra(SELECTED_QUESTION_GROUD_ARG, -1);
        mQuestionDatabase = QuestionDatabaseLab.get(this);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new QuestionPagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(getIntent().getIntExtra(SELECTED_QUESTION_ARG, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean f = super.onCreateOptionsMenu(menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(QuestionDatabaseLab.get(this).getName(mSelectedQuestionGroup));

        return f;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() { //save changed to internal storage
        super.onPause();

        mQuestionDatabase.saveUserAnswers(this, mSelectedQuestionGroup);
    }

    private class QuestionPagerAdapter extends FragmentPagerAdapter {

        public QuestionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(mSelectedQuestionGroup, mQuestionDatabase
                    .getQuestions(mSelectedQuestionGroup).get(position));
        }

        @Override
        public int getCount() {
            return mQuestionDatabase.getQuestions(mSelectedQuestionGroup).size();
        }

    }
}
