package com.mobile.guarantee.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mobile.guarantee.R;
import com.mobile.guarantee.data.Product;

import android.widget.Toast;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;
import java.util.function.LongConsumer;

public class AddEditProductFragment extends Fragment {

    private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

    private ProductViewModel viewModel;
    private Product editing; // null when adding a new product
    private long purchaseDate = System.currentTimeMillis();
    private long warrantyEnd = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365);

    private TextInputLayout layoutName;
    private TextInputLayout layoutWarrantyEnd;
    private TextInputEditText inputName;
    private TextInputEditText inputStore;
    private TextInputEditText inputPurchaseDate;
    private TextInputEditText inputWarrantyEnd;
    private TextInputEditText inputPrice;
    private TextInputEditText inputBarcode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_edit_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        layoutName = view.findViewById(R.id.layoutName);
        layoutWarrantyEnd = view.findViewById(R.id.layoutWarrantyEnd);
        inputName = view.findViewById(R.id.inputName);
        inputStore = view.findViewById(R.id.inputStore);
        inputPurchaseDate = view.findViewById(R.id.inputPurchaseDate);
        inputWarrantyEnd = view.findViewById(R.id.inputWarrantyEnd);
        inputPrice = view.findViewById(R.id.inputPrice);
        inputBarcode = view.findViewById(R.id.inputBarcode);
        MaterialButton buttonScan = view.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(v -> scanBarcode());
        MaterialButton buttonSave = view.findViewById(R.id.buttonSave);
        MaterialButton buttonDelete = view.findViewById(R.id.buttonDelete);

        showDates();

        inputPurchaseDate.setOnClickListener(v -> pickDate(purchaseDate, millis -> {
            purchaseDate = millis;
            showDates();
        }));
        inputWarrantyEnd.setOnClickListener(v -> pickDate(warrantyEnd, millis -> {
            warrantyEnd = millis;
            showDates();
        }));

        long id = getArguments() != null ? getArguments().getLong("productId", -1) : -1;
        if (id != -1) {
            buttonDelete.setVisibility(View.VISIBLE);
            viewModel.getProduct(id).observe(getViewLifecycleOwner(), product -> {
                // Fill the form only once, so later updates don't overwrite typing.
                if (product == null || editing != null) {
                    return;
                }
                editing = product;
                inputName.setText(product.name);
                inputStore.setText(product.store);
                inputPrice.setText(String.valueOf(product.price));
                inputBarcode.setText(product.barcode == null ? "" : product.barcode);
                purchaseDate = product.purchaseDate;
                warrantyEnd = product.warrantyEndDate;
                showDates();
            });
        }

        buttonSave.setOnClickListener(v -> save());
        buttonDelete.setOnClickListener(v -> {
            if (editing != null) {
                viewModel.delete(editing);
            }
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    private void showDates() {
        inputPurchaseDate.setText(dateFormat.format(purchaseDate));
        inputWarrantyEnd.setText(dateFormat.format(warrantyEnd));
    }

    private void pickDate(long current, LongConsumer onPicked) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(current)
                .build();
        picker.addOnPositiveButtonClickListener(selection -> onPicked.accept(selection));
        picker.show(getParentFragmentManager(), "date_picker");
    }

    private void save() {
        String name = inputName.getText() == null
                ? "" : inputName.getText().toString().trim();
        if (name.isEmpty()) {
            layoutName.setError("Enter a name");
            return;
        }
        layoutName.setError(null);

        if (warrantyEnd < purchaseDate) {
            layoutWarrantyEnd.setError("Must be after the purchase date");
            return;
        }
        layoutWarrantyEnd.setError(null);

        double price = 0;
        String priceText = inputPrice.getText() == null
                ? "" : inputPrice.getText().toString().trim();
        if (!priceText.isEmpty()) {
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                price = 0;
            }
        }

        Product product = editing != null ? editing : new Product();
        product.name = name;
        product.store = inputStore.getText() == null
                ? "" : inputStore.getText().toString().trim();
        product.purchaseDate = purchaseDate;
        product.warrantyEndDate = warrantyEnd;
        product.price = price;
        String barcode = inputBarcode.getText() == null
                ? "" : inputBarcode.getText().toString().trim();
        product.barcode = barcode.isEmpty() ? null : barcode;

        if (editing != null) {
            viewModel.update(product);
        } else {
            viewModel.insert(product);
        }
        NavHostFragment.findNavController(this).navigateUp();
    }
    private void scanBarcode() {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(requireContext(), options);

        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    String value = barcode.getRawValue();
                    if (value != null) {
                        inputBarcode.setText(value);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(),
                        "Scan failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}