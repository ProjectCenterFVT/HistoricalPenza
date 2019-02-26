package com.projectcenterfvt.historicalpenza.DataBases;

import android.widget.Filter;

import com.projectcenterfvt.historicalpenza.PlaceSuggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для работы с поиском
 * Нужно, чтобы Дима пояснил еще раз
 * @author Dmitry
 * @version 1.0.0
 * @since 1.0.0
 */

public class DataHelper {

    private static List<PlaceSuggestion> sPlaceSuggestions;

    public static void setsPlaceSuggestions(List<PlaceSuggestion> sPlaceSuggestions) {
        DataHelper.sPlaceSuggestions = sPlaceSuggestions;
    }

    public static List<PlaceSuggestion> getHistory(int count) {

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

    public static void findSuggestions(final String query, final int limit, final long simulatedDelay,
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
                Pattern pattern = Pattern.compile(query.toLowerCase());
                if (!(constraint == null || constraint.length() == 0)) {
                    for (PlaceSuggestion suggestion : sPlaceSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                        Matcher matcher = pattern.matcher(suggestion.getBody().toLowerCase());
                        if (matcher.find()){
                            suggestionList.add(suggestion);
                        }
                    }
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

    public interface OnFindPlacesListener {
        void onResults(List<PlaceSuggestion> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<PlaceSuggestion> results);
    }
}
