package com.mobile.guarantee.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.guarantee.R;
import com.mobile.guarantee.data.Product;

import java.util.concurrent.TimeUnit;

public class ProductListFragment extends Fragment {

    private ProductViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        RecyclerView recycler = view.findViewById(R.id.recyclerProducts);
        TextView empty = view.findViewById(R.id.textEmpty);
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);

        ProductAdapter adapter = new ProductAdapter(product -> {
            Bundle args = new Bundle();
            args.putLong("productId", product.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.addEditProductFragment, args);
        });
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.submitList(products);
            empty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Temporary: adds a sample product so we can test the list.
        // It will be replaced by navigation to the add screen.
        fab.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.addEditProductFragment));
    }
}