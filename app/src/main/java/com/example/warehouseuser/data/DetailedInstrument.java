package com.example.warehouseuser.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailedInstrument extends Instrument {

    private Long manufacturerTimestamp = 0L;
    private Long modelTimestamp = 0L;
    private Long priceTimestamp = 0L;
    private Long quantityTimestamp = 0L;

    private boolean isDeleted = false;
    private boolean isNew = false;
    private final List<Integer> changedQuantity = new ArrayList<>();

    public DetailedInstrument(Instrument instrument) {
        super(instrument.getId(), instrument.getManufacturer(), instrument.getModel(), instrument.getPrice(), instrument.getQuantity());
    }

    public Long getManufacturerTimestamp() {
        return manufacturerTimestamp;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        this.manufacturerTimestamp = new Date().getTime();
    }

    public Long getModelTimestamp() {
        return modelTimestamp;
    }

    public void setModel(String model) {
        this.model = model;
        this.modelTimestamp = new Date().getTime();
    }

    public Long getPriceTimestamp() {
        return priceTimestamp;
    }

    public void setPrice(float price) {
        this.price = price;
        this.priceTimestamp = new Date().getTime();
    }

    public Long getQuantityTimestamp() {
        return quantityTimestamp;
    }

    public void changeQuantity(int quantityDifference) {
        this.changedQuantity.add(quantityDifference);
        this.quantity += quantityDifference;
        this.quantityTimestamp = new Date().getTime();
    }

    public boolean isQuantityChanged() {
        return !changedQuantity.isEmpty();
    }

    public List<Integer> getChangedQuantities() {
        return changedQuantity;
    }

    public int getChangedQuantity() {
        if (changedQuantity.isEmpty()) return 0;
        return changedQuantity.get(0);
    }

    public void removeLastQuantityChange() {
        changedQuantity.remove(0);
    }

    public boolean existFieldUpdate() {
        return manufacturerTimestamp > 0 || modelTimestamp > 0 || priceTimestamp > 0;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setAsDeleted() {
        isDeleted = true;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setAsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void markAsCurrentVersion() {
        manufacturerTimestamp = 0L;
        modelTimestamp = 0L;
        priceTimestamp = 0L;
        quantityTimestamp = 0L;
        isNew = false;
    }

    public boolean isAnyDataUpdated() {
        return existFieldUpdate() || isDeleted() || isNew() | isQuantityChanged();
    }
}
