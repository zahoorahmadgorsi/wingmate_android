package com.app.wingmate;

import androidx.lifecycle.MutableLiveData;

import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.models.Quotes;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GlobalArray {

    public static List<Quotes> quotes = new ArrayList<>();

    public static MutableLiveData<String> liveQuote = new MutableLiveData<>();

    public static List<ParseUser> myCustomersList = new ArrayList<>();

    public static Random random = new Random();
}
