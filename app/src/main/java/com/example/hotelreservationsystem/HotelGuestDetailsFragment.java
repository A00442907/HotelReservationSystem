package com.example.hotelreservationsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

public class HotelGuestDetailsFragment extends Fragment {

    View view;
    SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(myPreference, Context.MODE_PRIVATE);
    public static final String myPreference = "myPref";
    public static final String guestsCount = "guestsCount";
    public static final String checkIn = "checkIn";
    public static final String checkOut = "checkOut";
    RecyclerView recyclerView;
    String hotelName, hotelAvailability, hotelPrice;
    LinearLayoutManager lineLayoutManager;
    Button send;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.hotel_guest_details_fragment, container, false);
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView hotelRecapTextView = view.findViewById(R.id.hotel_recap_text_view);
        send = view.findViewById(R.id.send);

        if (getArguments() != null) {
            hotelName = getArguments().getString("hotel name");
            hotelPrice = getArguments().getString("hotel price");
            hotelAvailability = getArguments().getString("hotel availability");
            hotelRecapTextView.setText(getString(R.string.selected_hotel) + hotelName + getString(R.string.cost_string) + hotelPrice + getString(R.string.availability_string) + hotelAvailability);
        }
        setupRecyclerView();
        send.setOnClickListener(new View.OnClickListener() {
            JSONObject obj;
            int viewFlag = 0;
            TypedInput in;

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(myPreference, Context.MODE_PRIVATE);
                recyclerView = view.findViewById(R.id.guest_list_recyclerView);
                JSONArray array = new JSONArray();

                for (int i = 0; i < (lineLayoutManager.getItemCount()); i++) {
                    View firstViewItem = lineLayoutManager.findViewByPosition(i);
                    EditText guestName = firstViewItem != null ? firstViewItem.findViewById(R.id.guestName) : null;
                    EditText guestGender = firstViewItem != null ? firstViewItem.findViewById(R.id.gender) : null;
                    obj = new JSONObject();
                    if (Objects.requireNonNull(guestName).getText().toString().isEmpty() && guestGender.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Please enter the Guest details", Toast.LENGTH_LONG).show();
                        viewFlag = 1;
                    } else {
                        try {
                            obj.put("guest_name", guestName.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            obj.put("gender", guestGender.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        array.put(obj);
                    }
                }
                obj = new JSONObject();
                try {
                    obj.put("hotel_name", hotelName);
                    if (sharedPreferences.contains(checkIn)) {
                        obj.put("checkin", sharedPreferences.getString(checkIn, ""));
                    }
                    if (sharedPreferences.contains(checkOut)) {
                        obj.put("checkout", sharedPreferences.getString(checkOut, ""));
                    }
                    obj.put("guests_list", array);
                    in = new TypedByteArray("application/json", obj.toString().getBytes(StandardCharsets.UTF_8));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (viewFlag == 0) {
                    Api.getClient().addGuestLists(in, new Callback<ConfirmationNumber>() {

                        @Override
                        public void success(ConfirmationNumber confirmation_number, Response response) {
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.confirmation_number), confirmation_number.getconfirmation_number());
                            GuestAddedFragment guestAddedFragment = new GuestAddedFragment();
                            guestAddedFragment.setArguments(bundle);

                            FragmentTransaction fragmentTransaction = getFragmentManager() != null ? getFragmentManager().beginTransaction() : null;
                            if (fragmentTransaction != null) {
                                fragmentTransaction.replace(R.id.main_layout, guestAddedFragment);
                            }
                            if (fragmentTransaction != null) {
                                fragmentTransaction.remove(HotelGuestDetailsFragment.this);
                            }
                            if (fragmentTransaction != null) {
                                fragmentTransaction.addToBackStack(null);
                            }
                            if (fragmentTransaction != null) {
                                fragmentTransaction.commit();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setupRecyclerView() {
        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        int guestNumber = 0;
        if (sharedPreferences.contains(guestsCount)) {
            guestNumber = Integer.parseInt(sharedPreferences.getString(guestsCount, ""));
        }
        recyclerView = view.findViewById(R.id.guest_list_recyclerView);
        lineLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lineLayoutManager);
        GuestListAdapter guestAdapter = new GuestListAdapter(getActivity(), guestNumber);
        recyclerView.setAdapter(guestAdapter);
    }
}