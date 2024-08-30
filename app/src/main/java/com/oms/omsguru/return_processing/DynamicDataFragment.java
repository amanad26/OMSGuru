package com.oms.omsguru.return_processing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.oms.omsguru.MainActivity;
import com.oms.omsguru.R;
import com.oms.omsguru.adapters.ProductDetailsSkuListAdapter;
import com.oms.omsguru.adapters.ProductDetailsSkuListAdapterForTest;
import com.oms.omsguru.apis.BaseUrls;
import com.oms.omsguru.apis.RetrofitClient;
import com.oms.omsguru.databinding.FragmentDynamicDataBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.models.GetOrderModel;
import com.oms.omsguru.models.PresignedUrlModel;
import com.oms.omsguru.models.VerifyOtpModel;
import com.oms.omsguru.session.Session;
import com.oms.omsguru.utils.AbstractExample;
import com.oms.omsguru.utils.HomeInterface;
import com.oms.omsguru.utils.ProgressDialog;
import com.oms.omsguru.utils.SelectedSkuModel;
import com.oms.omsguru.utils.SkuInterface;
import com.oms.omsguru.utils.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class DynamicDataFragment extends Fragment implements SkuInterface {

    FetchDetailsModel.Result data = null;
    FragmentDynamicDataBinding binding;
    List<VerifyOtpModel.Results.ClaimTemplate> templete = new ArrayList<>();
    ArrayList<SelectedSkuModel> skuList = new ArrayList<>();

    ProgressDialog pd;

    public DynamicDataFragment(FetchDetailsModel.Result result, String barcode) {
        this.data = result;
        this.barCode = barcode;
    }

    Activity activity;
    String selectedWarehouse = "";
    String returnType = "";
    boolean isAccepted = false;
    String confirmReturn = "";

    String selectedCurrReturn = "";
    String selectedTemplate = "";
    VerifyOtpModel.Results results = null;
    Session session;
    String barCode;
    Map<String, String> params = new HashMap();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDynamicDataBinding.inflate(getLayoutInflater());

        activity = requireActivity();

        session = new Session(activity);
        results = session.getVerifyModel(activity);
        pd = new ProgressDialog(activity);

        selectedWarehouse = session.getWarehouseId();

        if (data != null) {

            binding.invioceId.setText(data.getInvoiceId());
            binding.subOrderId.setText(data.getSubOrderId());
            binding.productName.setText(data.getProductName());
            binding.coustomerName.setText(data.getCustomerName());
            binding.qty.setText(data.getQty());
            binding.invioceAmount.setText(data.getAmount());
            binding.invioceDate.setText(data.getOrderDate());

            if (!data.getOrderDate().equalsIgnoreCase("")) {
                binding.invioceDate.setText(UtilsClass.convertDateFormat(data.getOrderDate()));
            }

            binding.sattlementAmount.setText(data.getSettlementAmount());
            binding.status.setText(data.getStatus());
            binding.daySinceShiped.setText(String.valueOf(data.getDaysSinceShipped()));
            binding.returnReason.setText(String.valueOf(data.getReturnReason()));
            binding.subReason.setText(String.valueOf(data.getReturnSubReason()));
            binding.comments.setText(String.valueOf(data.getReturnComments()));
            binding.statusNew.setText(String.valueOf(data.getStatus()));

            binding.skuRecler.setLayoutManager(new LinearLayoutManager(activity));
            binding.skuRecler.setAdapter(new ProductDetailsSkuListAdapter(activity, data.getSku(), this, 0));

            if (data.getStatus().equalsIgnoreCase("Cancel Init") || data.getStatus().equalsIgnoreCase("Return Init")) {
                binding.spinnerRetrunType.setVisibility(View.GONE);
                binding.curiourText.setVisibility(View.GONE);
                binding.curiourlinear.setVisibility(View.GONE);
            }

            if (data.getStatus().equalsIgnoreCase("Cancel Init")) {
                confirmReturn = "courier";
            }
            if (data.getStatus().equalsIgnoreCase("Return Init")) {
                confirmReturn = "customer";
            }

        }

        if (data.getStatus().equalsIgnoreCase("Cancelled Return Received") || data.getStatus().equalsIgnoreCase("Return Received")) {
            Log.e("TAG", "onCreateView: ");
        } else {

            binding.acceptReturn.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    binding.anyLinear.setVisibility(View.VISIBLE);
                    binding.fileClaim.setChecked(false);
                    binding.clamLinear.setVisibility(View.GONE);
                    isAccepted = true;
                    returnType = "1";

                } else {
                    binding.anyLinear.setVisibility(View.VISIBLE);
                    binding.clamLinear.setVisibility(View.GONE);
                    isAccepted = false;
                    binding.acceptReturn.setChecked(false);
                    returnType = "0";
                }


            });
            binding.fileClaim.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    binding.anyLinear.setVisibility(View.VISIBLE);
                    binding.acceptReturn.setChecked(false);
                    binding.clamLinear.setVisibility(View.VISIBLE);
                    isAccepted = true;
                    returnType = "2";

                } else {
                    binding.anyLinear.setVisibility(View.VISIBLE);
                    binding.fileClaim.setChecked(false);
                    binding.clamLinear.setVisibility(View.GONE);
                    isAccepted = false;
                    returnType = "0";
                }
            });

            binding.btnSubmit.setOnClickListener(v -> {
                if (!returnType.equalsIgnoreCase("0")) {

                    if (data.getSku().size() != 0) {
                        boolean isChanged = false;
                        for (int i = 0; i < data.getSku().size(); i++) {
                            if (data.getSku().get(i).getQty1() != 0 || data.getSku().get(i).getQty2() != 0) {
                                isChanged = true;
                                break;
                            }
                        }

                        if (isChanged) {
                            if (data.getQty().equalsIgnoreCase("1")) {
                                submitOneQuantity();
                            } else {
                                showWarning();
                            }
                        } else {
                            Toast.makeText(activity, "Select Good or Bad Quantity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (data.getQty().equalsIgnoreCase("1")) {
                            submitOneQuantity();
                        } else {
                            showWarning();
                        }
                    }
                } else {
                    Toast.makeText(activity, "Select Any Return Type", Toast.LENGTH_SHORT).show();
                }

            });

        }


        setupSpinner();
        setupTemplateSpinner();
        setWarehouse();

        return binding.getRoot();

    }

    private void setWarehouse() {

        ArrayList<String> warehouseIdList = new ArrayList<>();
        ArrayList<String> warehouseNamesList = new ArrayList<>();
        VerifyOtpModel.Results result = null;

        result = session.getVerifyModel(activity);

        warehouseNamesList.clear();
        warehouseIdList.clear();
        for (int i = 0; i < result.getWarehouses().size(); i++) {
            warehouseIdList.add(result.getWarehouses().get(i).getId());
            warehouseNamesList.add(result.getWarehouses().get(i).getName());
        }

        ArrayAdapter<String> AreaAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, warehouseNamesList);
        AreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerRetrunWarehouse.setHint("Select Warehouse");
        binding.spinnerRetrunWarehouse.setAdapter(AreaAdapter);

        binding.spinnerRetrunWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedWarehouse = warehouseIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int pos = 0;
        for (int i = 0; i < warehouseIdList.size(); i++) {
            if (warehouseIdList.equals(session.getWarehouseId())) {
                pos = i;
                break;
            }
        }

        binding.spinnerRetrunWarehouse.setSelection(pos);
    }

    private void showWarning() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final View customLayout = getLayoutInflater().inflate(R.layout.warning_dialog_custom, null);
        builder.setView(customLayout);

        CardView submit = customLayout.findViewById(R.id.submitBtn);
        RecyclerView sku_recler = customLayout.findViewById(R.id.sku_recler);
        CardView cancel = customLayout.findViewById(R.id.cancel_action);
        SmartMaterialSpinner spinner_warehouse = customLayout.findViewById(R.id.spinner_warehouse);

        AlertDialog dialog = builder.create();
        dialog.show();


        sku_recler.setLayoutManager(new LinearLayoutManager(activity));
        sku_recler.setAdapter(new ProductDetailsSkuListAdapterForTest(activity, data.getSku(), this));

        ArrayList<String> warehouseIdList = new ArrayList<>();
        ArrayList<String> warehouseNamesList = new ArrayList<>();
        VerifyOtpModel.Results result = null;

        result = session.getVerifyModel(activity);

        warehouseNamesList.clear();
        warehouseIdList.clear();
        for (int i = 0; i < result.getWarehouses().size(); i++) {
            warehouseIdList.add(result.getWarehouses().get(i).getId());
            warehouseNamesList.add(result.getWarehouses().get(i).getName());
        }

        ArrayAdapter<String> AreaAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, warehouseNamesList);
        AreaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner_warehouse.setHint("Select Warehouse");
        spinner_warehouse.setAdapter(AreaAdapter);


        spinner_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedWarehouse = warehouseIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        int pos = 0;
        for (int i = 0; i < warehouseIdList.size(); i++) {
            if (warehouseIdList.equals(session.getWarehouseId())) {
                pos = i;
                break;
            }
        }

        spinner_warehouse.setSelection(pos);

        cancel.setOnClickListener(v -> dialog.dismiss());

        submit.setOnClickListener(v -> {

            if (returnType.equalsIgnoreCase("")) {
                Toast.makeText(activity, "Select Return Type", Toast.LENGTH_SHORT).show();
            } else {

                Log.e("TAG", "onClick: Skus " + data.getSku().toString());
                skuList.clear();

                for (int i = 0; i < data.getSku().size(); i++) {
                    String good = "0", bad = "0", sku_code;
                    sku_code = data.getSku().get(i).getSkuCode();
                    SelectedSkuModel sku = new SelectedSkuModel(sku_code, String.valueOf(data.getSku().get(i).getQty1()), String.valueOf(data.getSku().get(i).getQty2()));
                    skuList.add(sku);
                }

                params.put("data[command]", "process_return");
                params.put("data[warehouse_id]", session.getWarehouseId());
                params.put("data[company_id]", session.getCompanyId());
                params.put("data[return_type]", returnType);
                params.put("data[shipment_tracker]", barCode);
                params.put("data[invoice_id]", data.getInvoiceId());
                params.put("data[confirm_return_type]", confirmReturn);
                params.put("data[channel_id]", UtilsClass.channelId);
                params.put("data[Client][auth_user_id]", session.getUserId());
                params.put("data[Client][auth_code]", session.getAuth_code());
                params.put("data[Client][last_permission_update]", session.getPermission_code());
                params.put("data[Invoice][return_warehouse_id]", selectedWarehouse);
                params.put("data[Sku][return_comments]", binding.commentEdit.getText().toString());

                for (int i = 0; i < skuList.size(); i++) {
                    params.put("data[Sku][" + i + "][sku_code]", skuList.get(i).getSku());
                    params.put("data[Sku][" + i + "][good]", skuList.get(i).getGood());
                    params.put("data[Sku][" + i + "][bad]", skuList.get(i).getBad());
                }

                if (returnType.equalsIgnoreCase("2")) {
                    getPresignedUrl();
                } else {
                    acceptRetutn();
                }

            }

        });


    }

    private void submitOneQuantity() {
        Log.e("TAG", "onClick: Skus " + data.getSku().toString());
        skuList.clear();

        for (int i = 0; i < data.getSku().size(); i++) {
            String good = "0", bad = "0", sku_code;
            sku_code = data.getSku().get(i).getSkuCode();

            SelectedSkuModel sku = new SelectedSkuModel(sku_code, String.valueOf(data.getSku().get(i).getQty1()), String.valueOf(data.getSku().get(i).getQty2()));
            skuList.add(sku);
        }

        params.put("data[command]", "process_return");
        params.put("data[warehouse_id]", session.getWarehouseId());
        params.put("data[company_id]", session.getCompanyId());
        params.put("data[return_type]", returnType);
        params.put("data[shipment_tracker]", barCode);
        params.put("data[invoice_id]", data.getInvoiceId());
        params.put("data[confirm_return_type]", confirmReturn);
        params.put("data[channel_id]", UtilsClass.channelId);
        params.put("data[Client][auth_user_id]", session.getUserId());
        params.put("data[Client][auth_code]", session.getAuth_code());
        params.put("data[Client][last_permission_update]", session.getPermission_code());
        params.put("data[Invoice][return_warehouse_id]", selectedWarehouse);
        params.put("data[Sku][return_comments]", binding.commentEdit.getText().toString());

        for (int i = 0; i < skuList.size(); i++) {
            params.put("data[Sku][" + i + "][sku_code]", skuList.get(i).getSku());
            params.put("data[Sku][" + i + "][good]", skuList.get(i).getGood());
            params.put("data[Sku][" + i + "][bad]", skuList.get(i).getBad());
        }

        if (returnType.equalsIgnoreCase("2")) {
            getPresignedUrl();
        } else {
            acceptRetutn();
        }
    }


    private void acceptRetutn() {

        Log.e("TAG", "acceptRetutn: All Params new " + params);

        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BaseUrls.BASE_URL + BaseUrls.VALIDATE_PACK_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                pd.dismiss();
                try {
                    jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");

                    if (code == 0) {
                        Log.e("TAG", "onResponse: Response " + response);
                        Toast.makeText(activity, "Successfully returned", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        ReturnProcessingFragment.isCompleted = true;
                        activity.finish();
                    } else {
                        Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        ReturnProcessingFragment.isFailed = true;
                        ReturnProcessingFragment.errorMessageFromApi = jsonObject.getString("message");
                        activity.finish();
                    }
                } catch (JSONException e) {
                    pd.dismiss();
                    Toast.makeText(activity, "Unexpected Response ", Toast.LENGTH_SHORT).show();
                }


            }
        }, error -> {
            pd.dismiss();
            Log.e("TAG", "onErrorResponse: " + error.toString());
            ReturnProcessingFragment.isFailed = true;
            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            ReturnProcessingFragment.errorMessageFromApi = error.getLocalizedMessage().toString();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("TAG", "getParams: All Parametrs " + params);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.add(stringRequest);

    }

    private void setupSpinner() {


        String type = "";
        type = String.valueOf(data.getConfirmReturnType());


        List<String> footballPlayers = new ArrayList<>();
        footballPlayers.add("Currier Return - RTO");
        footballPlayers.add("Customer Return - RTV");

        binding.spinnerRetrunType.setItem(footballPlayers);

        binding.spinnerRetrunType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedCurrReturn = footballPlayers.get(position);
                if (position == 0) {
                    confirmReturn = "courier";
                } else {
                    confirmReturn = "customer";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    private void getPresignedUrl() {


        params.put("data[claim_title]", binding.titleEdit.getText().toString());
        params.put("data[claim_details]", binding.descriptionEdit.getText().toString());

        startActivity(new Intent(activity, UploadFileActivity.class).putExtra("data", (Serializable) params).putExtra("id", data.getId()));
        activity.finish();
    }


    private void setupTemplateSpinner() {

        List<String> footballPlayers = new ArrayList<>();

        templete.clear();

        if (results.getClaimTemplates().size() != 0) {
            for (int i = 0; i < results.getClaimTemplates().size(); i++) {
                footballPlayers.add(results.getClaimTemplates().get(i).getTemplateName());
                templete.add(results.getClaimTemplates().get(i));
            }
        }

        binding.spinnerTamplate.setItem(footballPlayers);


        binding.spinnerTamplate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTemplate = footballPlayers.get(position);
                binding.titleEdit.setText(selectedTemplate);

                binding.descriptionEdit.setText(templete.get(position).getTemplateDetails());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    @Override
    public void onChange(boolean val, int pos) {
        data.getSku().get(pos).setGood(val);
    }
}