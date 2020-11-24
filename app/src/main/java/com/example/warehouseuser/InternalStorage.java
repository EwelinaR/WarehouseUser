package com.example.warehouseuser;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InternalStorage {

    private final Context context;

    public InternalStorage(Context context) {
        this.context = context;
    }

    public List<Instrument> readInstruments() {
        try {
            List<Instrument> instruments = new ArrayList<>();
            FileInputStream fis = context.openFileInput(Instrument.class.getName());
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

    public List<UpdateInstrument> readUpdates() {
        List<UpdateInstrument> instruments = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(UpdateInstrument.class.getName());
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (fis.available() > 0) {
                instruments.add((UpdateInstrument) ois.readObject());
            }

            ois.close();
            fis.close();
            return instruments;
        } catch (IOException | ClassNotFoundException e) {
            return instruments;
        }
    }

    public void deleteUpdates() {
        context.deleteFile(UpdateInstrument.class.getName());
    }

    public void addInstrument(Instrument instrument)  throws IOException {
        List<Instrument> instruments = readInstruments();
        int id;
        try {
            id = instruments.get(instruments.size() - 1).getId();
        } catch (NullPointerException e) {
            id = 0;
        }

        instrument.setId(id);
        instruments.add(instrument);
        writeInstruments(instruments);

        writeUpdate(new UpdateInstrument("POST", instrument));
    }

    public void deleteInstrument(Instrument instrument) throws IOException {
        List<Instrument> instruments = readInstruments().stream()
                .filter(i -> i.getId() != instrument.getId()).collect(Collectors.toList());

        writeInstruments(instruments);
        writeUpdate(new UpdateInstrument("DELETE", instrument));
    }

    public void updateInstrument(Instrument instrument) throws IOException {
        List<Instrument> instruments = readInstruments();
        instruments.forEach(i -> {
            if (i.getId() == instrument.getId()) {
                if (instrument.getManufacturer() != null) i.setManufacturer(instrument.getManufacturer());
                if (instrument.getModel() != null) i.setModel(instrument.getModel());
                if (instrument.getPrice() > 0) i.setPrice(instrument.getPrice());
            }});

        writeInstruments(instruments);
        writeUpdate(new UpdateInstrument("PUT", instrument));
    }

    public void changeAmountOfInstrument(Instrument instrument, int amount) throws IOException {
        List<Instrument> instruments = readInstruments();
        instruments.forEach(i -> {
            if (i.getId() == instrument.getId()) {
                i.setQuantity(i.getQuantity()+amount);
            }});

        writeInstruments(instruments);
        String requestType = amount > 0 ? "INCREASE" : "DECREASE";
        writeUpdate(new UpdateInstrument(requestType, instrument, amount));
    }

    public void writeInstruments(List<Instrument> instruments) throws IOException {
        FileOutputStream fos = context.openFileOutput(Instrument.class.getName(), Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (Instrument instrument: instruments) {
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();
    }

    private void writeUpdate(UpdateInstrument updateInstrument) throws IOException {
        List<UpdateInstrument> instruments = readUpdates();
        instruments.add(updateInstrument);

        FileOutputStream fos = context.openFileOutput(UpdateInstrument.class.getName(), Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (UpdateInstrument instrument: instruments) {
            oos.writeObject(instrument);
        }
        oos.close();
        fos.close();
    }
}
