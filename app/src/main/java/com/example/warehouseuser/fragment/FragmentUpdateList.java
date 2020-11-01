package com.example.warehouseuser.fragment;

import com.example.warehouseuser.Instrument;
import com.example.warehouseuser.RequestResponseStatus;

import java.util.List;

public interface FragmentUpdateList {
    void updateView(RequestResponseStatus status, List<Instrument> instruments);
}
