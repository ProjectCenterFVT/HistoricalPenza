package com.projectcenterfvt.historicalpenza.Managers;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.Marker;
import com.projectcenterfvt.historicalpenza.DataBases.DB_Position;
import com.projectcenterfvt.historicalpenza.DataBases.DataHelper;
import com.projectcenterfvt.historicalpenza.DataBases.Sight;
import com.projectcenterfvt.historicalpenza.PlaceSuggestion;
import com.projectcenterfvt.historicalpenza.Server.BaseAsyncTask;
import com.projectcenterfvt.historicalpenza.Server.ClientServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Работа со списком
 * @author Dmitry
 * @version 1.0.0
 * @since 1.0.0
 * @see com.projectcenterfvt.historicalpenza.Activity.MapActivity
 */

public class SearchManager {

    private static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private static final String LOG_SEARCH = "searchView";
    private FloatingSearchView searchView;
    private String lastQuery = "";
    private CameraManager cameraManager;
    private DrawerLayout mDrawerLayout;
    private Context myContext;
    private DB_Position database;
    private HashMap<Integer, Marker> stackMarkers = new HashMap<>();

    public SearchManager(Context myContext, DrawerLayout mDrawerLayout, DB_Position database) {
        this.myContext = myContext;
        this.cameraManager = cameraManager;
        this.mDrawerLayout = mDrawerLayout;
        this.database = database;

    }

    public void setStackMarkers(HashMap<Integer, Marker> stackMarkers) {
        this.stackMarkers = stackMarkers;
    }

    public void setupSearch() {
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    searchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<PlaceSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    searchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    searchView.hideProgress();
                                }
                            });
                }

                Log.d(LOG_SEARCH, "onSearchTextChanged()");
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                PlaceSuggestion placeSuggestion = (PlaceSuggestion) searchSuggestion;
                int id = placeSuggestion.getId();
//                DataHelper.findSuggestions(this, PlaceSuggestion.getBody(),
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<PlaceSuggestion> results) {
//                                //show search results
//                            }
//
//                        });
                Sight sight = database.getCell(id);
                cameraManager.setCameraPosition(sight.getLocation());
                InputMethodManager imm = (InputMethodManager) myContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                Log.d(LOG_SEARCH, "onSuggestionClicked()");
//                Sight sight = database.getCell(id);
//                cameraManager.setCameraPosition(sight.getLocation());
//                Marker marker = placeSuggestion.getMarker();
//                if (sight1.getFlag()) {
//                    switch (sight1.getType()) {
//                        case 0:
//                            Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
//                                    getIdentifier("unlock", "drawable", myContext.getPackageName()));
//                            bitmap = Bitmap.createScaledBitmap(bitmap, 104, 160, false);
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            break;
//                        case 1:
//                            Bitmap bitmapHomestead = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
//                                    getIdentifier("homestead", "drawable", myContext.getPackageName()));
//                            bitmap = Bitmap.createScaledBitmap(bitmapHomestead, 104, 160, false);
//                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                            break;
//                    }
//                } else {
//                    Bitmap bitmap = BitmapFactory.decodeResource(myContext.getResources(), myContext.getResources().
//                            getIdentifier("lock", "drawable", myContext.getPackageName()));
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 104, 160, false);
//                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
//                }

                lastQuery = searchSuggestion.getBody();
                searchView.setSearchBarTitle(lastQuery);
                searchView.clearSuggestions();
            }

            @Override
            public void onSearchAction(String query) {
                lastQuery = query;

//                DataHelper.findColors(getActivity(), query,
//                        new DataHelper.OnFindColorsListener() {
//
//                            @Override
//                            public void onResults(List<ColorWrapper> results) {
//                                //show search results
//                            }
//
//                        });
                Log.d(LOG_SEARCH, "onSearchAction()");
                //Toast.makeText(myContext, "onSearchAction()", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                ClientServer call = new ClientServer(myContext);
                call.setOnResponseListener(new BaseAsyncTask.OnResponseListener<Sight[]>() {
                    @Override
                    public void onSuccess(Sight[] result) {
                        ArrayList<PlaceSuggestion> placeSuggestionArrayList = new ArrayList<>();
                        for (Sight item :
                                result) {
                            placeSuggestionArrayList.add(new PlaceSuggestion(item.getId(), item.getTitle(), stackMarkers.get(item.getId())));
                        }

                        DataHelper.setsPlaceSuggestions(placeSuggestionArrayList);

                        //show suggestions when search bar gains focus (typically history suggestions)
                        searchView.swapSuggestions(DataHelper.getHistory(3));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                call.getAllInfo();

                Log.d(LOG_SEARCH, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                searchView.setSearchBarTitle("Поиск по городу");

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(LOG_SEARCH, "onFocusCleared()");
            }
        });
    }

    public void setSearchView(FloatingSearchView searchView) {
        this.searchView = searchView;
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
        setupSearch();
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }
}
