package com.example.warehouseuser;

import java.io.Serializable;
import java.util.Date;

public class UpdateInstrument implements Serializable {
    private final long date;
    private final String requestType;
    private final Instrument instrument;
    private int amount;

    public UpdateInstrument(String requestType, Instrument instrument, int amount) {
        this.date = new Date().getTime();
        this.requestType = requestType;
        this.instrument = instrument;
        this.amount = amount;
    }

    public UpdateInstrument(String requestType, Instrument instrument) {
        this.date = new Date().getTime();
        this.requestType = requestType;
        this.instrument = instrument;
    }

    public long getDate() {
        return date;
    }

    public String getRequestType() {
        return requestType;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "UpdateInstrument{" +
                "date=" + date +
                ", requestType='" + requestType + '\'' +
                ", instrument=" + instrument +
                ", amount=" + amount +
                '}';
    }
}
