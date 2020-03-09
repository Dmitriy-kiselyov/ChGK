package ru.pussy_penetrator.chgk;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class QuestionGridActivity extends SingleFragmentActivity {

    private static final String SELECTED_QUESTION_GROUP_ARG = "selected_group";

    public static Intent newIntent(Context context, int selectedQuestionGroup) {
        Intent intent = new Intent(context, QuestionGridActivity.class);
        intent.putExtra(SELECTED_QUESTION_GROUP_ARG, selectedQuestionGroup);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return QuestionGridFragment
                .newInstance(getIntent().getIntExtra(SELECTED_QUESTION_GROUP_ARG, -1));
    }
}
