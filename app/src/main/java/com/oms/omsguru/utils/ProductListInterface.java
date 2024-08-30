package com.oms.omsguru.utils;

import com.oms.omsguru.models.ProductListModel;

public interface ProductListInterface {

    void onAdded(ProductListModel model , String code , String message);
}
