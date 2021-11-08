package com.app.wingmate;

import androidx.lifecycle.MutableLiveData;

import com.app.wingmate.models.Quotes;

import java.util.ArrayList;
import java.util.List;

public class GlobalArray {

    public static List<Quotes> quotes = new ArrayList<>();

    public static MutableLiveData<String> liveQuote = new MutableLiveData<>();

}
