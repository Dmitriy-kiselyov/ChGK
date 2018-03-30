package ru.pussy_penetrator.chgk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.pussy_penetrator.chgk.model.QuestionDatabase;
import ru.pussy_penetrator.chgk.model.QuestionDatabaseLab;

/**
 * Created by Sex_predator on 16.10.2016.
 */
public class MainFragment extends Fragment {

    private static final int GRID_ACTIVITY_REQUEST_CODE = 0;

    private ListView    mListView;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_question_list, container, false);

        mListView = (ListView) v.findViewById(R.id.question_list);
        mProgressBar = (ProgressBar) v.findViewById(R.id.question_list_progress_bar);

        mListView.setAdapter(new QuestionArrayAdapter(getContext(), R.id.question_list_item_text1,
                                                      QuestionDatabaseLab.get(getContext())));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = QuestionGridActivity.newIntent(getContext(), position);
                startActivityForResult(intent, GRID_ACTIVITY_REQUEST_CODE);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GRID_ACTIVITY_REQUEST_CODE)
            ((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
    }

    private class QuestionArrayAdapter extends ArrayAdapter<QuestionDatabase> {

        private Context          mContext;
        private LayoutInflater   mInflater;
        private QuestionDatabase mDatabase;

        public QuestionArrayAdapter(Context context, int resource, QuestionDatabase database) {
            super(context, resource);

            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDatabase = database;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null)
                view = mInflater.inflate(R.layout.question_list_item, null);

            ((TextView) view.findViewById(R.id.question_list_item_text1))
                    .setText(mDatabase.getName(position));

            TextView textView = (TextView) view.findViewById(R.id.question_list_item_text2);
            int questionCount = mDatabase.getQuestionCount(position);

            //setup item hint text
            String subText = "";

            if (questionCount % 10 == 1)
                subText = questionCount + " вопрос";
            else if (2 <= (questionCount % 10) && (questionCount % 10) <= 4)
                subText = questionCount + " вопроса";
            else
                subText = questionCount + " вопросов";

            int correct = mDatabase.getCorrectQuestionAnswered(position);
            int wrong = mDatabase.getWrongQuestionAnswered(position);

            if (correct != 0 || wrong != 0)
                subText += " (" + correct + " верно, " + wrong + " неверно)";

            textView.setText(subText);

            return view;
        }

        @Override
        public int getCount() {
            return mDatabase.getSize();
        }
    }

}
