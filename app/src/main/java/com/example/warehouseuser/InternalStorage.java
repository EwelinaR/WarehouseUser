package com.example.warehouseuser;

import android.content.Context;

import com.example.warehouseuser.data.DetailedInstrument;
import com.example.warehouseuser.data.Instrument;

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

    public void saveInstrumentsFromServer(List<Instrument> instruments) throws IOException {
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
        for (Instrument instrument: instruments) {
            oos.writeObject(instrument.getDetailedInstrument());
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
        List<DetailedInstrument> instruments = readUpdatedInstruments().stream()
                .peek(i -> { if (i.getId() == id) i.setAsDeleted(); })
                .filter(i -> i.getId() != id || !i.isNew())
                .collect(Collectors.toList());
        saveUpdate(instruments);
    }

    public void updateInstrument(Instrument instrument) throws IOException {
        List<DetailedInstrument> instruments = readUpdatedInstruments();
        instruments.forEach(i -> {
            if (i.getId() == instrument.getId()) {
                String newManufacturer = instrument.getManufacturer();
                if (!newManufacturer.equals(i.getManufacturer())) i.setManufacturer(instrument.getManufacturer());
                if (!instrument.getModel().equals(i.getModel())) i.setModel(instrument.getModel());
                if (instrument.getPrice() != i.getPrice()) i.setPrice(instrument.getPrice());
            }});
        saveUpdate(instruments);
    }

    public void updateQuantity(int id, int amount) throws IOException {
        List<DetailedInstrument> instruments = readUpdatedInstruments();
        instruments.forEach(i -> { if (i.getId() == id) i.changeQuantity(amount); });

        saveUpdate(instruments);
    }

    public void addInstrument(Instrument instrument) throws IOException {
        List<DetailedInstrument> instruments = readUpdatedInstruments();
        int id;
        try {
            id = Collections.max(
                    instruments.stream().map(Instrument::getId).collect(Collectors.toList())) + 1;
        } catch (NoSuchElementException e) {
            id = 0;
        }

        instrument.setId(id);
        DetailedInstrument i = instrument.getDetailedInstrument();
        i.setAsNew(true);
        instruments.add(i);
        saveUpdate(instruments);
    }

    public void saveUpdate(List<DetailedInstrument> instruments) throws IOException {
        FileOutputStream fos = context.openFileOutput(UPDATED_INSTRUMENTS, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (DetailedInstrument instrument: instruments) {
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();
    }

    public List<DetailedInstrument> readUpdatedInstruments() {
        try {
            List<DetailedInstrument> instruments = new ArrayList<>();
            FileInputStream fis = context.openFileInput(UPDATED_INSTRUMENTS);
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (fis.available() > 0) {
                instruments.add((DetailedInstrument) ois.readObject());
            }
            ois.close();
            fis.close();
            return instruments;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public List<Instrument> readInstrumentsForDisplay() {
        try {
            List<DetailedInstrument> instruments = new ArrayList<>();
            FileInputStream fis = context.openFileInput(UPDATED_INSTRUMENTS);
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (fis.available() > 0) {
                instruments.add((DetailedInstrument) ois.readObject());
            }
            ois.close();
            fis.close();
            List<Instrument> ins = new ArrayList<>();
            for (DetailedInstrument instrument: instruments) {
                if (!instrument.isDeleted()) ins.add(instrument);
            }
            return ins;
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
