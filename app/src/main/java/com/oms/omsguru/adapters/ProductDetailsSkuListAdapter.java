package com.oms.omsguru.adapters;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oms.omsguru.R;
import com.oms.omsguru.databinding.ProdutDetailSkuLayoutBinding;
import com.oms.omsguru.models.FetchDetailsModel;
import com.oms.omsguru.utils.SkuInterface;

import java.util.List;

public class ProductDetailsSkuListAdapter extends RecyclerView.Adapter<ProductDetailsSkuListAdapter.ViewHolder> {

    Context context;
    List<FetchDetailsModel.Result.Sku> models;
    SkuInterface skuInterface;
    int added;
    int i;
    boolean isPlusAdded = false;

    public ProductDetailsSkuListAdapter(Context context, List<FetchDetailsModel.Result.Sku> models, SkuInterface skuInterface, int i) {
        this.context = context;
        this.models = models;
        this.skuInterface = skuInterface;
        this.i = i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.produt_detail_sku_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.sku.setText(models.get(position).getSkuCode());
        holder.binding.qty.setText(models.get(position).getQty());


        int qty = Integer.parseInt(models.get(position).getQty());
        added = 0;

        if (i == 0) {
            holder.binding.badQTYTv.setFocusable(true);
            holder.binding.goodQTYTv.setFocusable(true);
        } else {
            holder.binding.badQTYTv.setFocusable(false);
            holder.binding.goodQTYTv.setFocusable(false);
        }

//        if (qty == 1) {
//            if (models.get(position).isGood())
//                holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
//            else holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty1()));
//
//        } else {
//            holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
//            holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty2()));
//
//        }

        holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
        holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty2()));

        holder.binding.acceptedBaadQTYTv.setText(models.get(position).getBad_accepted_qty());
        holder.binding.acceptedGoodQTYTv.setText(models.get(position).getGood_accepted_qty());


        holder.binding.goodQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("0")) {
                    holder.binding.goodQTYTv.setText("");
                    holder.binding.goodQTYTv.requestFocus();
                    holder.binding.badQTYTv.clearFocus();
                    if (holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                        holder.binding.badQTYTv.setText("0");
                    }
                    showKeyboard(context, holder.binding.goodQTYTv);
                }

                holder.binding.goodQTYTv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {

                                if (Integer.parseInt(s.toString()) > 0) {
                                    int max = Integer.parseInt(models.get(position).getQty());
                                    int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                                    int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                                    max = max - (maxBaad + maxGood);

                                    int gq = Integer.parseInt(s.toString());

                                    int gqbad = 0;
                                    if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                                        gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());


                                    max = max - gqbad;
                                    if (max >= Integer.parseInt(holder.binding.goodQTYTv.getText().toString())) {
                                        models.get(position).setQty1(Integer.parseInt(holder.binding.goodQTYTv.getText().toString()));
                                        Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();

                                    } else {
                                        holder.binding.goodQTYTv.setText("0");
                                        Toast.makeText(context, "Quantity must be less than max quantity", Toast.LENGTH_SHORT).show();
                                        models.get(position).setQty1(0);
                                        holder.binding.goodQTYTv.requestFocus();
                                    }

                                    holder.binding.goodQTYTv.clearFocus();
                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }

                        //
                    }
                });
            }
        });

        holder.binding.badQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("0")) {
                    holder.binding.badQTYTv.setText("");
                    holder.binding.badQTYTv.requestFocus();
                    holder.binding.goodQTYTv.clearFocus();
                    if (holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                        holder.binding.goodQTYTv.setText("0");
                    }
                    showKeyboard(context, holder.binding.badQTYTv);
                }

                holder.binding.badQTYTv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {

                            if (Integer.parseInt(s.toString()) > 0) {

                                int max = Integer.parseInt(models.get(position).getQty());
                                int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                                int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                                max = max - (maxBaad + maxGood);

                                int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                                int gqbad = 0;
                                if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                                    gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                                max = max - gqbad;
                                if (max >= Integer.parseInt(holder.binding.badQTYTv.getText().toString())) {
                                    models.get(position).setQty2(Integer.parseInt(holder.binding.badQTYTv.getText().toString()));
                                    //notifyItemChanged(position);
                                    //hideKeyboardFrom(context, holder.binding.badQTYTv);

                                    Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();

                                } else {
                                    holder.binding.badQTYTv.setText("0");
                                    models.get(position).setQty2(0);
                                    Toast.makeText(context, "Quantity must be less than max quantity", Toast.LENGTH_SHORT).show();
                                    holder.binding.badQTYTv.requestFocus();
                                }

                                holder.binding.badQTYTv.clearFocus();

                            }
                        }
                    }
                });
            }
        });

        holder.binding.goodQTYTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                        int max = Integer.parseInt(models.get(position).getQty());
                        int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                        int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                        max = max - (maxBaad + maxGood);

                        int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                        int gqbad = 0;
                        if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                            gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                        max = max - gqbad;
                        if (max >= Integer.parseInt(holder.binding.goodQTYTv.getText().toString())) {
                            models.get(position).setQty1(Integer.parseInt(holder.binding.goodQTYTv.getText().toString()));
                            //notifyItemChanged(position);
                            hideKeyboardFrom(context, holder.binding.goodQTYTv);
                            Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                        } else {
                            holder.binding.goodQTYTv.setText("0");
                            holder.binding.goodQTYTv.requestFocus();
                            models.get(position).setQty1(0);
                        }

                    } else {
                        holder.binding.goodQTYTv.setText("0");
                    }
                }
                return false;
            }
        });

        holder.binding.badQTYTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {

                        int max = Integer.parseInt(models.get(position).getQty());
                        int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                        int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                        max = max - (maxBaad + maxGood);

                        int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());
//                            int gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());
                        int gqbad = 0;
                        if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                            gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                        max = max - gqbad;
                        if (max >= Integer.parseInt(holder.binding.badQTYTv.getText().toString())) {
                            models.get(position).setQty2(Integer.parseInt(holder.binding.badQTYTv.getText().toString()));
                            //notifyItemChanged(position);
//                             holder.binding.badQTYTv.setText(String.valueOf(gq));
                            hideKeyboardFrom(context, holder.binding.badQTYTv);
                            Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                        } else {
                            holder.binding.badQTYTv.setText("0");
                            holder.binding.badQTYTv.requestFocus();
                            models.get(position).setQty2(0);
                        }

                    } else {
                        holder.binding.badQTYTv.setText("0");
                    }
                }
                return false;
            }
        });

        holder.binding.goodQTYPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                    isPlusAdded = true;
                    int max = Integer.parseInt(models.get(position).getQty());
                    int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                    int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                    max = max - (maxBaad + maxGood);

                    int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                    int gqbad = 0;
                    if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                        gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                    max = max - gqbad;

                    if (gq < max) {
                        gq++;
                        Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                        models.get(position).setQty1(gq);
                        holder.binding.goodQTYTv.setText(String.valueOf(gq));
//                        notifyItemChanged(position);
                    }

                } else {
                    holder.binding.goodQTYTv.setText("0");
                }


            }
        });

        holder.binding.goodQTYMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                    isPlusAdded = true;
                    int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());
                    if (gq > 0) {
                        gq--;
                        models.get(position).setQty1(gq);
                        Toast.makeText(context, "Quantity Removed", Toast.LENGTH_SHORT).show();
                        holder.binding.goodQTYTv.setText(String.valueOf(gq));
                        //notifyItemChanged(position);
                    }
                }
            }
        });

        //// bad started

        holder.binding.baadQTYPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                    isPlusAdded = true;
                    int max = Integer.parseInt(models.get(position).getQty());
                    int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                    int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                    max = max - (maxBaad + maxGood);

                    int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                    int gqbad = 0;
                    if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                        gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                    max = max - gqbad;

                    if (gq < max) {
                        gq++;
                        models.get(position).setQty2(gq);
                        Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                        holder.binding.badQTYTv.setText(String.valueOf(gq));
                        //notifyItemChanged(position);
                    }
                } else {
                    holder.binding.badQTYTv.setText("0");
                }

            }
        });

        holder.binding.badQTYMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                    isPlusAdded = true;
                    int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());
                    if (gq > 0) {
                        gq--;
                        models.get(position).setQty2(gq);
                        Toast.makeText(context, "Quantity Removed", Toast.LENGTH_SHORT).show();
                        holder.binding.badQTYTv.setText(String.valueOf(gq));
//                        notifyItemChanged(position);
                    }
                }

            }
        });


      /*  if (models.get(position).getQty().equalsIgnoreCase("1")) {

            holder.binding.goodQTYPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (models.get(position).getQty1() == 0) {
                        models.get(position).setQty1(1);
                        models.get(position).setGood(true);
                        skuInterface.onChange(true, position);
                        notifyItemChanged(position);
                    }
                }
            });


            holder.binding.goodQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("0")) {
                        holder.binding.goodQTYTv.setText("");
                        holder.binding.goodQTYTv.requestFocus();
                        showKeyboard(context, holder.binding.goodQTYTv);
                    }

                    holder.binding.goodQTYTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                                    if (holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("1")) {
                                        if (models.get(position).getQty1() == 0) {
                                            models.get(position).setQty1(1);
                                            models.get(position).setGood(true);
                                            skuInterface.onChange(true, position);
                                            //notifyItemChanged(position);
                                        }
                                    } else {
                                        holder.binding.goodQTYTv.setText("");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }

                            //
                        }
                    });


                }
            });


            holder.binding.badQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("0")) {
                        holder.binding.badQTYTv.setText("");
                        holder.binding.badQTYTv.requestFocus();
                        showKeyboard(context, holder.binding.badQTYTv);
                    }

                    holder.binding.badQTYTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                                if (holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("1")) {
                                    if (models.get(position).getQty1() == 0) {
                                        models.get(position).setQty1(1);
                                        holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty1()));
                                        //notifyItemChanged(position);
                                        models.get(position).setGood(false);
                                        skuInterface.onChange(false, position);
                                    } else {
                                        holder.binding.badQTYTv.setText("");
                                    }
                                }

                            }
                        }
                    });
                }
            });


            holder.binding.baadQTYPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (models.get(position).getQty1() == 0) {
                        models.get(position).setQty1(1);
                        holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty1()));
                        notifyItemChanged(position);
                        models.get(position).setGood(false);
                        skuInterface.onChange(false, position);
                    }
                }
            });

            holder.binding.goodQTYMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (models.get(position).isGood()) {
                        if (models.get(position).getQty1() == 1) {
                            models.get(position).setQty1(0);
                            models.get(position).setGood(false);
                            skuInterface.onChange(false, position);
                            holder.binding.goodQTYTv.setText(String.valueOf(models.get(position).getQty1()));
                            notifyItemChanged(position);
                        }
                    }
                }
            });

            holder.binding.badQTYMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!models.get(position).isGood()) {
                        if (models.get(position).getQty1() == 1) {
                            models.get(position).setQty1(0);
                            holder.binding.badQTYTv.setText(String.valueOf(models.get(position).getQty1()));
                            notifyItemChanged(position);
                        }
                    }
                }
            });


        } else {

            holder.binding.goodQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("0")) {
                        holder.binding.goodQTYTv.setText("");
                        holder.binding.goodQTYTv.requestFocus();
                        showKeyboard(context, holder.binding.goodQTYTv);
                    }

                    holder.binding.goodQTYTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //Toast.makeText(context, "change", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {

                                    if (Integer.parseInt(s.toString()) > 0) {
                                        int max = Integer.parseInt(models.get(position).getQty());
                                        int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                                        int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                                        max = max - (maxBaad + maxGood);

                                        int gq = Integer.parseInt(s.toString());

                                        int gqbad = 0;
                                        if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                                            gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());


                                        max = max - gqbad;
                                        if (max >= Integer.parseInt(holder.binding.goodQTYTv.getText().toString())) {
                                            models.get(position).setQty1(Integer.parseInt(holder.binding.goodQTYTv.getText().toString()));
                                            Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();

                                        } else {
                                            holder.binding.goodQTYTv.setText("");
                                            Toast.makeText(context, "Quantity must be less than max quantity", Toast.LENGTH_SHORT).show();
                                            models.get(position).setQty1(0);
                                            holder.binding.goodQTYTv.requestFocus();
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }


                            //
                        }
                    });
                }
            });

            holder.binding.badQTYTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("0")) {
                        holder.binding.badQTYTv.setText("");
                        holder.binding.badQTYTv.requestFocus();
                        showKeyboard(context, holder.binding.badQTYTv);
                    }

                    holder.binding.badQTYTv.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {

                                if (Integer.parseInt(s.toString()) > 0) {

                                    int max = Integer.parseInt(models.get(position).getQty());
                                    int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                                    int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                                    max = max - (maxBaad + maxGood);

                                    int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                                    int gqbad = 0;
                                    if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                                        gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                                    max = max - gqbad;
                                    if (max >= Integer.parseInt(holder.binding.badQTYTv.getText().toString())) {
                                        models.get(position).setQty2(Integer.parseInt(holder.binding.badQTYTv.getText().toString()));
                                        //notifyItemChanged(position);
                                        //hideKeyboardFrom(context, holder.binding.badQTYTv);
                                        Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();

                                    } else {
                                        holder.binding.badQTYTv.setText("");
                                        models.get(position).setQty2(0);
                                        Toast.makeText(context, "Quantity must be less than max quantity", Toast.LENGTH_SHORT).show();
                                        holder.binding.badQTYTv.requestFocus();
                                    }

                                }
                            }
                        }
                    });
                }
            });

            holder.binding.goodQTYTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                            int max = Integer.parseInt(models.get(position).getQty());
                            int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                            int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                            max = max - (maxBaad + maxGood);

                            int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                            int gqbad = 0;
                            if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                                gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                            max = max - gqbad;
                            if (max >= Integer.parseInt(holder.binding.goodQTYTv.getText().toString())) {
                                models.get(position).setQty1(Integer.parseInt(holder.binding.goodQTYTv.getText().toString()));
                                notifyItemChanged(position);
                                hideKeyboardFrom(context, holder.binding.goodQTYTv);
                                Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.binding.goodQTYTv.setText("0");
                                holder.binding.goodQTYTv.requestFocus();
                                models.get(position).setQty1(0);
                            }

                        } else {
                            holder.binding.goodQTYTv.setText("0");
                        }
                    }
                    return false;
                }
            });

            holder.binding.badQTYTv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {

                            int max = Integer.parseInt(models.get(position).getQty());
                            int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                            int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                            max = max - (maxBaad + maxGood);

                            int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());
//                            int gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());
                            int gqbad = 0;
                            if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                                gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                            max = max - gqbad;
                            if (max >= Integer.parseInt(holder.binding.badQTYTv.getText().toString())) {
                                models.get(position).setQty2(Integer.parseInt(holder.binding.badQTYTv.getText().toString()));
                                notifyItemChanged(position);
                                hideKeyboardFrom(context, holder.binding.badQTYTv);
                                Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                            } else {
                                holder.binding.badQTYTv.setText("0");
                                holder.binding.badQTYTv.requestFocus();
                                models.get(position).setQty2(0);
                            }


                        } else {
                            holder.binding.badQTYTv.setText("0");
                        }
                    }
                    return false;
                }
            });

            holder.binding.goodQTYPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                        isPlusAdded = true;
                        int max = Integer.parseInt(models.get(position).getQty());
                        int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                        int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                        max = max - (maxBaad + maxGood);

                        int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                        int gqbad = 0;
                        if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase(""))
                            gqbad = Integer.parseInt(holder.binding.badQTYTv.getText().toString());


                        max = max - gqbad;

                        if (gq < max) {
                            gq++;
                            Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                            models.get(position).setQty1(gq);
                            notifyItemChanged(position);
                        }

                    }

                }
            });

            holder.binding.goodQTYMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase("")) {
                        isPlusAdded = true;
                        int gq = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());
                        if (gq > 0) {
                            gq--;
                            models.get(position).setQty1(gq);
                            Toast.makeText(context, "Quantity Removed", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        }
                    }
                }
            });

            //// bad started

            holder.binding.baadQTYPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                        isPlusAdded = true;
                        int max = Integer.parseInt(models.get(position).getQty());
                        int maxGood = Integer.parseInt(models.get(position).getGood_accepted_qty());
                        int maxBaad = Integer.parseInt(models.get(position).getBad_accepted_qty());
                        max = max - (maxBaad + maxGood);

                        int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());

                        int gqbad = 0;
                        if (!holder.binding.goodQTYTv.getText().toString().equalsIgnoreCase(""))
                            gqbad = Integer.parseInt(holder.binding.goodQTYTv.getText().toString());

                        max = max - gqbad;

                        if (gq < max) {
                            gq++;
                            models.get(position).setQty2(gq);
                            Toast.makeText(context, "Quantity Added", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        }
                    }

                }
            });

            holder.binding.badQTYMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.binding.badQTYTv.getText().toString().equalsIgnoreCase("")) {
                        isPlusAdded = true;
                        int gq = Integer.parseInt(holder.binding.badQTYTv.getText().toString());
                        if (gq > 0) {
                            gq--;
                            models.get(position).setQty2(gq);
                            Toast.makeText(context, "Quantity Removed", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(position);
                        }
                    }

                }
            });

        }*/

        try {
            if (!models.get(position).getImage_url().equalsIgnoreCase("")) {
                Glide.with(context).load(models.get(position).getImage_url()).placeholder(R.drawable.baseline_image_24).into(holder.binding.image);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void showKeyboard(Context activity, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ProdutDetailSkuLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ProdutDetailSkuLayoutBinding.bind(itemView);
        }
    }


}
