package ru.pussy_penetrator.chgk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ru.pussy_penetrator.chgk.model.QuestionDatabase;
import ru.pussy_penetrator.chgk.model.QuestionDatabaseLab;

public class StatisticsDialog extends DialogFragment {

    private static final String INDEX_ARG = "group_index";

    private TextView mGroupNameTextView;
    private TextView mGroupTextView;
    private TextView mCommonTextView;
    private TextView mSessionTextView;

    private int mGroupIndex;

    public static StatisticsDialog newInstance(int groupIndex) {
        Bundle args = new Bundle();
        args.putInt(INDEX_ARG, groupIndex);

        StatisticsDialog dialog = new StatisticsDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupIndex = getArguments().getInt(INDEX_ARG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Статистика");
        View view = inflater.inflate(R.layout.statistics, container, false);

        mGroupNameTextView = view.findViewById(R.id.statistics_group_name);
        mGroupTextView = view.findViewById(R.id.statistics_group);
        mCommonTextView = view.findViewById(R.id.statistics_common);
        mSessionTextView = view.findViewById(R.id.statistics_current);

        QuestionDatabase database = QuestionDatabaseLab.get(getActivity());
        mGroupNameTextView.setText(database.getName(mGroupIndex) + ":");
        String format = getResources().getString(R.string.statistics_format);

        int correct = database.getCorrectQuestionAnswered(mGroupIndex);
        int all = correct + database.getWrongQuestionAnswered(mGroupIndex);
        double percent = (double) correct / all * 100;
        mGroupTextView.setText(String.format(format, correct, all, (all == 0) ? 0 : percent));

        correct = database.getCorrectQuestionAnswered();
        all = correct + database.getWrongQuestionAnswered();
        percent = (double) correct / all * 100;
        mCommonTextView.setText(String.format(format, correct, all, (all == 0) ? 0 : percent));

        correct = database.getCorrectQuestionAnsweredDuringThisSession();
        all = correct + database.getWrongQuestionAnsweredDuringThisSession();
        percent = (double) correct / all * 100;
        mSessionTextView.setText(String.format(format, correct, all, (all == 0) ? 0 : percent));

        return view;
    }
}
