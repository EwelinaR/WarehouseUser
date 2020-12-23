package com.example.warehouseuser.fragment.update;

import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.data.InstrumentWrapper;

import java.util.List;

public interface FragmentUpdateList {
    void updateView(RequestResponseStatus status, List<InstrumentWrapper> instruments);
}
