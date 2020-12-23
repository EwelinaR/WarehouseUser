package com.example.warehouseuser.data;

import java.util.Date;

public class InstrumentWrapper extends DetailedInstrument {

    protected int category;
    protected Long categoryTimestamp = 0L;

    public InstrumentWrapper(int id, String manufacturer, String model, float price, int quantity, int category) {
        super(id, manufacturer, model, price, quantity);
        this.category = category;
    }

    public int getCategory() { return category; }

    public Long getCategoryTimestamp() {
        return categoryTimestamp;
    }

    public void setCategory(int category) {
        this.category = category;
        this.categoryTimestamp = new Date().getTime();
    }

    @Override
    public boolean existFieldUpdate() {
        return super.existFieldUpdate() || categoryTimestamp > 0;
    }

    @Override
    public void markAsCurrentVersion() {
        super.markAsCurrentVersion();
        categoryTimestamp = 0L;
    }
}
