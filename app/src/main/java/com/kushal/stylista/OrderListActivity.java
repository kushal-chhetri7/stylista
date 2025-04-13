package com.kushal.stylista;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kushal.stylista.adapter.FavoriteListAdapter;
import com.kushal.stylista.adapter.OrderAdapter;
import com.kushal.stylista.model.OrderModel;
import com.kushal.stylista.services.ApiListResponseCallback;
import com.kushal.stylista.services.OrderRepository;
import com.kushal.stylista.utils.SnackbarHelper;
import com.kushal.stylista.utils.SnackbarType;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private final OrderRepository orderRepository = new OrderRepository();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private OrderAdapter orderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_list);

        orderAdapter = new OrderAdapter(new ArrayList<>(), order -> {

        });

        recyclerView = findViewById(R.id.listOrder);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);


        fetchData();
    }

    private void fetchData() {
        orderRepository.fetchOrders(new ApiListResponseCallback<OrderModel>() {
            @Override
            public void onSuccess(List<OrderModel> dataList) {
                progressBar.setVisibility(View.GONE);
                orderAdapter.setOrderList(dataList);
            }

            @Override
            public void onFailure(Exception exception) {
                progressBar.setVisibility(View.GONE);
                SnackbarHelper.showSnackbar(findViewById(android.R.id.content), exception.getMessage(), SnackbarType.ERROR);
            }
        });

    }

    public void onBackPressed(View view) {
        finish();
    }
}