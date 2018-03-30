package ru.pussy_penetrator.chgk.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Sex_predator on 15.10.2016.
 */
public class QuestionDatabaseLab {

    private static QuestionDatabase sQuestionDatabase;
    private static boolean sIsLoaded = false;

    private QuestionDatabaseLab() {
        //do nothing
    }

    public static boolean isLoaded() {
        return sIsLoaded;
    }

    public static QuestionDatabase get(Context context) {
        if (sQuestionDatabase == null) {
            sQuestionDatabase = new QuestionDatabase();

            //load database
            try {
                loadDatabase(context);
                Log.d("DATABASE", "Database is loaded!");
            }
            catch (IOException e) {
                Log.d("DATABASE", "Database failed to load!");
            }
        }

        return sQuestionDatabase;
    }

    private static void loadDatabase(Context context) throws IOException {
        for (String fileName : context.getAssets().list("questions")) {
            try {
                loadFromFile(context, new File("questions", fileName));
                Log.d("DATABASE", fileName + " loaded!");
            }
            catch (JSONException | IOException e) {
                Log.d("DATABASE", fileName + " failed to load!");
            }
        }

    }

    private static void loadFromFile(Context context, File file)
            throws IOException, JSONException {
        InputStream in = context.getAssets().open(file.getPath());

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) > 0)
            out.write(buffer, 0, bytesRead);
        in.close();

        String jsonString = new String(out.toByteArray());

        JSONObject jsonObject = new JSONObject(jsonString);
        String name = jsonObject.getString("name");

        JSONArray questions = jsonObject.getJSONArray("questions");
        ArrayList<Question> list = new ArrayList<>(questions.length());
        for (int i = 0; i < questions.length(); i++) {
            jsonObject = questions.getJSONObject(i);
            String number = jsonObject.getString("number");
            String text = jsonObject.getString("text");
            String ans = jsonObject.getString("answer");

            Question question = new Question(number, text, ans);
            list.add(question);

            if (jsonObject.has("comment"))
                question.setComment(jsonObject.getString("comment"));
        }

        String saveFilePath = file.getName().substring(0, file.getName().length() - 3) + "save";
        sQuestionDatabase.add(context, name, saveFilePath, list);
    }

}
