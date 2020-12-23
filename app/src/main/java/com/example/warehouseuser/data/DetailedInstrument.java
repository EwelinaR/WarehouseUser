package com.example.warehouseuser.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailedInstrument extends Instrument {

    private static final long serialVersionUID = 7245612247752958479L;

    private Long manufacturerTimestamp = 0L;
    private Long modelTimestamp = 0L;
    private Long priceTimestamp = 0L;
    private Long quantityTimestamp = 0L;

    private boolean isDeleted = false;
    private boolean isNew = false;
    private List<Integer> changedQuantity;

    public DetailedInstrument(Instrument instrument) {
        super(instrument.getId(), instrument.getManufacturer(), instrument.getModel(), instrument.getPrice(), instrument.getQuantity());
        changedQuantity = new ArrayList<>();
    }

    public DetailedInstrument(int id, String manufacturer, String model, float price, int quantity) {
        super(id, manufacturer, model, price, quantity);
        changedQuantity = new ArrayList<>();
    }

    public InstrumentWrapper createInstrumentWrapper() {
        InstrumentWrapper i = new InstrumentWrapper(id, manufacturer, model, price, quantity, 0);
        i.setAsNew(isNew);
        if(isDeleted) i.setAsDeleted();
        i.setChangeQuantity(changedQuantity);
        i.setTimestamps(manufacturerTimestamp, modelTimestamp, priceTimestamp);
        return i;
    }

    public void setTimestamps(Long manufacturerTimestamp, Long modelTimestamp, Long priceTimestamp) {
        this.manufacturerTimestamp = manufacturerTimestamp;
        this.modelTimestamp = modelTimestamp;
        this.priceTimestamp = priceTimestamp;
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
        if (changedQuantity == null) changedQuantity = new ArrayList<>();
        this.changedQuantity.add(quantityDifference);
        this.quantity += quantityDifference;
    }

    public boolean isQuantityChanged() {
        return changedQuantity != null && !changedQuantity.isEmpty();
    }

    public List<Integer> getChangedQuantities() {
        return changedQuantity;
    }

    public int getChangedQuantity() {
        if (changedQuantity == null ||changedQuantity.isEmpty()) return 0;
        return changedQuantity.get(0);
    }

    public void setChangeQuantity(List<Integer> changedQuantity) {
        this.changedQuantity = changedQuantity;
    }

    public void removeLastQuantityChange() {
        quantityTimestamp = 0L;
        if (changedQuantity != null) changedQuantity.remove(0);
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
