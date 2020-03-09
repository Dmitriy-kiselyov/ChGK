package ru.pussy_penetrator.chgk;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.pussy_penetrator.chgk.model.Question;
import ru.pussy_penetrator.chgk.model.QuestionDatabase;
import ru.pussy_penetrator.chgk.model.QuestionDatabaseLab;

public class QuestionGridFragment extends Fragment {

    private static final String SELECTED_QUESTION_GROUP_ARG = "selected";
    private static final int    PAGER_ACTIVITY_REQUEST_CODE = 0;

    private int mSelectedQuestionGroup;

    private RecyclerView mRecyclerView;

    public static QuestionGridFragment newInstance(int selectedIndex) {
        Bundle args = new Bundle();
        args.putInt(SELECTED_QUESTION_GROUP_ARG, selectedIndex);
        QuestionGridFragment fragment = new QuestionGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mSelectedQuestionGroup = getArguments().getInt(SELECTED_QUESTION_GROUP_ARG);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grid_menu, menu);

        ActionBar actionBar = ((SingleFragmentActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(QuestionDatabaseLab.get(getContext()).getName(mSelectedQuestionGroup));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.menu_statistics:
                StatisticsDialog dialog = StatisticsDialog.newInstance(mSelectedQuestionGroup);
                dialog.show(getFragmentManager(), "STATS");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.question_grid, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.question_grid_recycler_view);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        else
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        QuestionDatabase database = QuestionDatabaseLab.get(getContext());
        mRecyclerView
                .setAdapter(new QuestionAdapter(database.getQuestions(mSelectedQuestionGroup)));

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAGER_ACTIVITY_REQUEST_CODE)
            mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private class QuestionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int      mPosition;
        private View     mView;
        private TextView mNumberTextView;
        private TextView mQuestionTextView;

        public QuestionHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mView = itemView;
            mNumberTextView = (TextView) itemView.findViewById(R.id.question_grid_item_text1);
            mQuestionTextView = (TextView) itemView.findViewById(R.id.question_grid_item_text2);
        }

        public void bind(int position, String number, String question, Question.Answer answer) {
            mPosition = position;
            mNumberTextView.setText("# " + number);
            mQuestionTextView.setText(question);

            if (answer == Question.Answer.CORRECT)
                mView.setBackgroundColor(getResources().getColor(R.color.green));
            else if (answer == Question.Answer.WRONG)
                mView.setBackgroundColor(getResources().getColor(R.color.red));
            else
                mView.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        }

        @Override
        public void onClick(View v) {
            Intent intent = QuestionPagerActivity
                    .newIntent(getContext(), mSelectedQuestionGroup, mPosition);
            startActivityForResult(intent, PAGER_ACTIVITY_REQUEST_CODE);
        }
    }

    private class QuestionAdapter extends RecyclerView.Adapter<QuestionHolder> {

        private ArrayList<Question> mQuestions;

        public QuestionAdapter(ArrayList<Question> questions) {
            mQuestions = questions;
        }

        @Override
        public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.question_grid_item, parent, false);
            return new QuestionHolder(view);
        }

        @Override
        public void onBindViewHolder(QuestionHolder holder, int position) {
            Question question = mQuestions.get(position);
            holder.bind(position, question.getNumber(), question.getText(),
                        question.getAnswerResult());
        }

        @Override
        public int getItemCount() {
            return mQuestions.size();
        }
    }
}
