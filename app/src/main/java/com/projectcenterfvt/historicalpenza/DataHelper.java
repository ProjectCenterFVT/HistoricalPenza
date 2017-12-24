package com.projectcenterfvt.historicalpenza;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Filter;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dmitry on 22.12.2017.
 */

class DataHelper {

    private static List<PlaceSuggestion> sPlaceSuggestions =
            new ArrayList<>(Arrays.asList(
                    new PlaceSuggestion("green"),
                    new PlaceSuggestion("blue"),
                    new PlaceSuggestion("pink"),
                    new PlaceSuggestion("purple"),
                    new PlaceSuggestion("brown"),
                    new PlaceSuggestion("gray"),
                    new PlaceSuggestion("Granny Smith Apple"),
                    new PlaceSuggestion("Indigo"),
                    new PlaceSuggestion("Periwinkle"),
                    new PlaceSuggestion("Mahogany"),
                    new PlaceSuggestion("Maize"),
                    new PlaceSuggestion("Mahogany"),
                    new PlaceSuggestion("Outer Space"),
                    new PlaceSuggestion("Melon"),
                    new PlaceSuggestion("Yellow"),
                    new PlaceSuggestion("Orange"),
                    new PlaceSuggestion("Red"),
                    new PlaceSuggestion("Orchid")));

    public interface OnFindPlacesListener {
        void onResults(List<PlaceSuggestion> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<PlaceSuggestion> results);
    }

    public static List<PlaceSuggestion> getHistory(FloatingSearchView.OnFocusChangeListener context, int count) {

        List<PlaceSuggestion> suggestionList = new ArrayList<>();
        PlaceSuggestion placeSuggestion;
        for (int i = 0; i < sPlaceSuggestions.size(); i++) {
            placeSuggestion = sPlaceSuggestions.get(i);
            placeSuggestion.setIsHistory(true);
            suggestionList.add(placeSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (PlaceSuggestion placeSuggestion : sPlaceSuggestions) {
            placeSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(final FloatingSearchView.OnQueryChangeListener context, String query, final int limit, final long simulatedDelay,
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
                List<PlaceSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {


//                    for (PlaceSuggestion suggestion : sPlaceSuggestions) {
//                        if (suggestion.getBody().toUpperCase()
//                                .startsWith(constraint.toString().toUpperCase())) {
//
//                            suggestionList.add(suggestion);
//                            if (limit != -1 && suggestionList.size() == limit) {
//                                break;
//                            }
//                        }
//                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<PlaceSuggestion>() {
                    @Override
                    public int compare(PlaceSuggestion lhs, PlaceSuggestion rhs) {
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
                    listener.onResults((List<PlaceSuggestion>) results.values);
                }
            }
        }.filter(query);

    }
}
