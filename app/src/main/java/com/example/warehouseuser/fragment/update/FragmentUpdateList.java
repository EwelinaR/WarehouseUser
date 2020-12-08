package com.example.warehouseuser.fragment.update;

import com.example.warehouseuser.data.Instrument;
import com.example.warehouseuser.RequestResponseStatus;

import java.util.List;

public interface FragmentUpdateList {
    void updateView(RequestResponseStatus status, List<Instrument> instruments);
}
