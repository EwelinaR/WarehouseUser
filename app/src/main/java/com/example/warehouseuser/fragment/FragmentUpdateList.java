package com.example.warehouseuser.fragment;

import com.example.warehouseuser.Instrument;

import java.util.List;

public interface FragmentUpdateList {
    void updateView(List<Instrument> instruments, int responseStatus);
}
