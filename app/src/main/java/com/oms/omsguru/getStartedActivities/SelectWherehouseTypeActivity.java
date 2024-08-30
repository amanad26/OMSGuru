package com.oms.omsguru.getStartedActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oms.omsguru.MainActivity;
import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ActivitySelectUserTypeBinding;
import com.oms.omsguru.models.VerifyOtpModel;
import com.oms.omsguru.session.Session;

import java.util.ArrayList;

public class SelectWherehouseTypeActivity extends AppCompatActivity {

    ActivitySelectUserTypeBinding binding;
    Activity activity;
    Session session;

    ArrayList<String> warehouseNamesList = new ArrayList<>();
    ArrayList<String> warehouseIdList = new ArrayList<>();
    ArrayList<String> companyNamesList = new ArrayList<>();
    ArrayList<String> companyIdList = new ArrayList<>();
    VerifyOtpModel.Results result = null;
    String selectedCompany = "0", selectedWarehouse = "", selectedWarehouseName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);

        result = session.getVerifyModel(activity);

        binding.icBack.setOnClickListener(v -> finish());

        binding.btnContinue.setOnClickListener(v -> {
            if (selectedWarehouse.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Please Select Warehouse", Toast.LENGTH_SHORT).show();
            } else {
                session.setWarehouseId(selectedWarehouse);
                session.setCompanyId(selectedCompany);
                session.setWarehouseName(selectedWarehouseName);
                startActivity(new Intent(activity, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (result != null) {
            setWarehouseSpinner();
            setCompanySpinner();
        } else {
            Toast.makeText(activity, "No Warehouse selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setWarehouseSpinner() {

        warehouseNamesList.clear();
        warehouseIdList.clear();
        for (int i = 0; i < result.getWarehouses().size(); i++) {
            warehouseIdList.add(result.getWarehouses().get(i).getId());
            warehouseNamesList.add(result.getWarehouses().get(i).getName());
        }

        ArrayAdapter<String> AreaAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_spinner_item, warehouseNamesList);
        AreaAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);


        binding.spinnerWarehouse.setHint("Select Warehouse");
        binding.spinnerWarehouse.setAdapter(AreaAdapter);


        binding.spinnerWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedWarehouse = warehouseIdList.get(position);
                selectedWarehouseName = warehouseNamesList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void setCompanySpinner() {

        companyNamesList.clear();
        companyIdList.clear();
        for (int i = 0; i < result.getCompanies().size(); i++) {
            companyIdList.add(result.getCompanies().get(i).getId());
            companyNamesList.add(result.getCompanies().get(i).getName());
        }

        ArrayAdapter<String> AreaAdapter = new ArrayAdapter<String>
                (activity, android.R.layout.simple_spinner_item, companyNamesList);
        AreaAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerCompany.setHint("Select Company");
        binding.spinnerCompany.setAdapter(AreaAdapter);


        binding.spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCompany = companyIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }


}