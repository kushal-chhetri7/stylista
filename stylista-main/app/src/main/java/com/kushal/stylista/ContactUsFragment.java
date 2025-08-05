package com.kushal.stylista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class ContactUsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.contactRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ContactItem> items = Arrays.asList(
                new ContactItem(R.drawable.ic_customer, "Customer Service", "support@yourapp.com"),
                new ContactItem(R.drawable.ic_whatsapp, "WhatsApp", "(416) 826-0732"),
                new ContactItem(R.drawable.ic_website, "Website", "www.stylista.com"),
                new ContactItem(R.drawable.ic_facebook, "Facebook", "@stylistaFB"),
                new ContactItem(R.drawable.ic_tiktok, "Tiktok", "@stylistaTK"),
                new ContactItem(R.drawable.ic_insta, "Instagram", "@stylistaIG")
        );

        recyclerView.setAdapter(new ContactAdapter(items));
        return view;
    }
}
