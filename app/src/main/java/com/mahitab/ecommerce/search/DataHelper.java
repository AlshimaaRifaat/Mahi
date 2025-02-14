package com.mahitab.ecommerce.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahitab.ecommerce.activities.SearchResultActivity;
import com.mahitab.ecommerce.models.ProductModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static ArrayList<ProductModel> sColorWrappers = new ArrayList<>();



    private static ArrayList<ColorSuggestion> sColorSuggestions;
    public interface OnFindColorsListener {
        void onResults(ArrayList<ProductModel> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<ColorSuggestion> results);
    }

    public static List<ColorSuggestion> getHistory(SharedPreferences defaultPreferences, Context context, int count) {
        if (defaultPreferences.getString("recentlySearchedList", null) == null)
            sColorSuggestions = new ArrayList<>();
        else
            sColorSuggestions = new Gson().fromJson(defaultPreferences.getString("recentlySearchedList", null), new TypeToken<List<ColorSuggestion>>() {
            }.getType());

        ArrayList<ColorSuggestion> suggestionList=new ArrayList<>();
        ColorSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (ColorSuggestion colorSuggestion : sColorSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<ColorSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (ColorSuggestion suggestion : sColorSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<ColorSuggestion>() {
                    @Override
                    public int compare(ColorSuggestion lhs, ColorSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<ColorSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                ArrayList<ProductModel> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (ProductModel color : sColorWrappers) {
                        if (color.getTitle()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((ArrayList<ProductModel>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sColorWrappers==null) {
            String jsonString = loadJson(context);
            sColorWrappers = deserializeColors(jsonString);
        }else
        {

        }
    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static ArrayList<ProductModel> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<ArrayList<ProductModel>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}
