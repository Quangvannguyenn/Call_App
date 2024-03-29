package com.android.call_app.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.call_app.Adapters.AllUserInSystemAdapter;
import com.android.call_app.Adapters.listContactUserAdapter;
import com.android.call_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private DatabaseReference dbListUserContact;
    private DatabaseReference dbAllUserSystem;
    private FirebaseAuth firebaseAuth;
    private RecyclerView listUserContactView;
    private RecyclerView listUserSystem;
    private TextView noItem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        listUserContactView = view.findViewById(R.id.listUserContactView);
        noItem = view.findViewById(R.id.noItem);
        listUserSystem = view.findViewById(R.id.listUserSystem);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.dbListUserContact = FirebaseDatabase.getInstance().getReference("chat");
        this.dbAllUserSystem = FirebaseDatabase.getInstance().getReference("dataUser");
        this.firebaseAuth = FirebaseAuth.getInstance();
        getListUserContact();
        getAllUserSystem();
    }

    private void getAllUserSystem() {
        this.dbAllUserSystem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listUserSystem = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String user = dataSnapshot.getKey().split("_")[0];
                    listUserSystem.add(user);
                }
                ChatFragment.this.listUserSystem.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                ChatFragment.this.listUserSystem.setAdapter(new AllUserInSystemAdapter(listUserSystem, getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getListUserContact() {
        String username = firebaseAuth.getCurrentUser().getEmail().split("@")[0];
        DatabaseReference dataSnapshotUser = dbListUserContact.child("room_"+username);
        final int[] lengthListUser = {0};
        dataSnapshotUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listUserContact = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    listUserContact.add(dataSnapshot.getKey());
                }
                ChatFragment.this.listUserContactView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                ChatFragment.this.listUserContactView.setAdapter(new listContactUserAdapter(listUserContact, getContext()));
                if(listUserContact.size() != lengthListUser[0]){
                    ChatFragment.this.listUserContactView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    ChatFragment.this.listUserContactView.setAdapter(new listContactUserAdapter(listUserContact, getContext()));
                    lengthListUser[0] = listUserContact.size();
                }
                if(lengthListUser[0] == 0){
                    noItem.setText("Users Contact are Empty");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}