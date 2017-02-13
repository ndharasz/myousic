package com.myousic.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

import com.myousic.models.SearchResult;

/**
 * Created by brian on 2/12/17.
 *
 * I extended this class so that it could hold a SearchResult object.
 * This way when it's clicked on we can easily get the information from the view
 */

public class ResultRow extends TableRow {
    private SearchResult searchResult;

    public ResultRow(Context context) {
        super(context);
    }

    public ResultRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }
}