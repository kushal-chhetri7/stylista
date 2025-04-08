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

public class FAQFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.faqRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<FAQItem> faqItems = Arrays.asList(
                new FAQItem("Can I track my order's delivery status?", "Yes, once your order is shipped, you can track it from the order section."),
                new FAQItem("Is there a return policy?", "Yes, you can return products within 30 days of delivery."),
                new FAQItem("Can I save my favorite items for later?", "Yes, simply tap the heart icon to add them to your wishlist."),
                new FAQItem("Can I share products with my friends?", "Yes, use the share button on the product page."),
                new FAQItem("How do I contact customer support?", "You can reach out via the 'Contact Us' tab or email us at support@example.com."),
                new FAQItem("What payment methods are accepted?", "We accept credit/debit cards, PayPal, and more."),
                new FAQItem("How to add review?", "Go to the product page and tap 'Write a review'.")
        );

        FAQAdapter adapter = new FAQAdapter(faqItems);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
