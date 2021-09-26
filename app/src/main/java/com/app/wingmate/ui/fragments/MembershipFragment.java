package com.app.wingmate.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.MembershipModel;
import com.app.wingmate.ui.adapters.PackageOptionsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import android.widget.Toast;

import android.app.ProgressDialog;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.CardParams;
import com.stripe.android.model.Token;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;


public class MembershipFragment extends BaseFragment implements BaseView{

    public static final String TAG = MembershipFragment.class.getName();
    Unbinder unbinder;
    @BindView(R.id.recycler_view)
    RecyclerView packageList;
    PackageOptionsAdapter adapter;
    List<MembershipModel> packageListData;
    public static final String PUBLISHABLE_KEY = "pk_test_51JZI8NDYkhNTsMduK9vcOVV7nPpZpsrdVB8sP15HiXItHO2hA2YNlGp7Rih9D8C3HASbzOhb26PvJbXT1NPWAxbN00mEMiK21I";
    private Card card;
    private ProgressDialog progress;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    private CardParams cardParams;

    public MembershipFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        cardParams = new CardParams("4242424242424242",12,2030,"123");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_membership, container, false);
        unbinder = ButterKnife.bind(this,view);
        packageListData = new ArrayList<MembershipModel>();
        prepareList();
        adapter = new PackageOptionsAdapter(getContext(),packageListData);
        packageList.setAdapter(adapter);
        packageList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        progress = new ProgressDialog(getActivity());
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });
        return view;
    }

    public void prepareList(){
        packageListData.add(new MembershipModel("7 days trial period free","Free",false));
        packageListData.add(new MembershipModel("1 month","69USD",false));
        packageListData.add(new MembershipModel("3 months","XXUSD",false));
        packageListData.add(new MembershipModel("6 months","XXXUSD",false));
        packageListData.add(new MembershipModel("12 months","XXXUSD",false));

    }
    private void startProgress(String title){
        progress.setTitle(title);
        progress.setMessage("Please Wait");
        progress.show();
    }
    private void finishProgress(){
        progress.dismiss();
    }
    private void buy(){
        boolean validation =true;
        if(validation){
            startProgress("Validating Credit Card");
            new Stripe(getContext(),PUBLISHABLE_KEY).createCardToken(cardParams,
                    new ApiResultCallback<Token>() {
                @Override
                public void onSuccess(@NonNull Token token) {
                    progress.dismiss();
                    charge(token);
                }

                @Override
                public void onError(@NonNull Exception e) {
                    Log.e("Stripe error",e.getMessage());
                }
            });
        }
    }

    private void charge(Token cardToken){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ItemName", "test");
        params.put("cardToken", cardToken.getId());
        params.put("name","Dominic Wong");
        params.put("email","dominwong4@gmail.com");
        params.put("address","HIHI");
        params.put("zip","99999");
        params.put("city_state","CA");
        startProgress("Purchasing Item");
        ParseCloud.callFunctionInBackground("purchaseItem", params, new FunctionCallback<Object>() {
            public void done(Object response, ParseException e) {
                finishProgress();
                if (e == null) {
                    Log.d("Cloud Response", "There were no exceptions! " + response.toString());
                    Toast.makeText(getContext(),
                            "Item Purchased Successfully ",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("Cloud Response", "Exception: " + e);
                    Toast.makeText(getContext(),
                            e.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}