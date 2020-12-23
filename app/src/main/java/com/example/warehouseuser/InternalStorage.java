package com.example.warehouseuser;

import android.content.Context;

import com.example.warehouseuser.data.DetailedInstrument;
import com.example.warehouseuser.data.Instrument;
import com.example.warehouseuser.data.InstrumentWrapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class InternalStorage {

    private final Context context;

    private static final String INSTRUMENTS = "instruments";
    private static final String UPDATED_INSTRUMENTS = "updated_instruments";

    public InternalStorage(Context context) {
        this.context = context;
    }

    public void saveInstrumentsFromServer(List<InstrumentWrapper> instruments) throws IOException {
        // save to base version
        FileOutputStream fos = context.openFileOutput(INSTRUMENTS, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (Instrument instrument: instruments) {
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();

        // save to update version
        fos = context.openFileOutput(UPDATED_INSTRUMENTS, Context.MODE_PRIVATE);
        oos = new ObjectOutputStream(fos);
        for (InstrumentWrapper instrument: instruments) {
            instrument.markAsCurrentVersion();
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();
    }

    public List<Instrument> readInstrumentsFromServer() {
        try {
            List<Instrument> instruments = new ArrayList<>();
            FileInputStream fis = context.openFileInput(INSTRUMENTS);
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (fis.available() > 0) {
                instruments.add((Instrument) ois.readObject());
            }
            ois.close();
            fis.close();
            return instruments;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public void deleteInstrument(int id) throws IOException {
        List<InstrumentWrapper> instruments = readUpdatedInstruments().stream()
                .peek(i -> { if (i.getId() == id) i.setAsDeleted(); })
                .filter(i -> i.getId() != id || !i.isNew())
                .collect(Collectors.toList());
        saveUpdate(instruments);
    }

    public void updateInstrument(InstrumentWrapper instrument) throws IOException {
        List<InstrumentWrapper> instruments = readUpdatedInstruments();
        instruments.forEach(i -> {
            if (i.getId() == instrument.getId()) {
                if (!instrument.getManufacturer().equals(i.getManufacturer())) i.setManufacturer(instrument.getManufacturer());
                if (!instrument.getModel().equals(i.getModel())) i.setModel(instrument.getModel());
                if (instrument.getCategory() != i.getCategory()) i.setCategory(instrument.getCategory());
                if (instrument.getPrice() != i.getPrice()) i.setPrice(instrument.getPrice());
            }});
        saveUpdate(instruments);
    }

    public void updateQuantity(int id, int amount) throws IOException {
        List<InstrumentWrapper> instruments = readUpdatedInstruments();
        instruments.forEach(i -> { if (i.getId() == id) i.changeQuantity(amount); });

        saveUpdate(instruments);
    }

    public void addInstrument(InstrumentWrapper instrument) throws IOException {
        List<InstrumentWrapper> instruments = readUpdatedInstruments();
        int id;
        try {
            id = Collections.max(
                    instruments.stream().map(Instrument::getId).collect(Collectors.toList())) + 1;
        } catch (NoSuchElementException e) {
            id = 0;
        }

        instrument.setId(id);
        instrument.setAsNew(true);
        instruments.add(instrument);
        saveUpdate(instruments);
    }

    public void saveUpdate(List<InstrumentWrapper> instruments) throws IOException {
        FileOutputStream fos = context.openFileOutput(UPDATED_INSTRUMENTS, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (InstrumentWrapper instrument: instruments) {
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();
    }

    public List<InstrumentWrapper> readUpdatedInstruments() {
        try {
            List<InstrumentWrapper> instruments = new ArrayList<>();
            FileInputStream fis = context.openFileInput(UPDATED_INSTRUMENTS);
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (fis.available() > 0) {
                Object o = ois.readObject();
                if (o instanceof InstrumentWrapper) {
                    instruments.add((InstrumentWrapper) o);
                } else if (o instanceof  DetailedInstrument) {
                    DetailedInstrument i = (DetailedInstrument) o;
                    instruments.add(i.createInstrumentWrapper());
                }
            }
            ois.close();
            fis.close();
            return instruments;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteData() {
        context.deleteFile(INSTRUMENTS);
        context.deleteFile(UPDATED_INSTRUMENTS);
    }
}
