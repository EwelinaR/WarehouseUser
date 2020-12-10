package com.example.warehouseuser;

import android.content.Context;

import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.data.DetailedInstrument;
import com.example.warehouseuser.data.Instrument;
import com.example.warehouseuser.fragment.update.FragmentUpdate;

import java.io.IOException;
import java.util.List;

public class Updater {

    private final List<DetailedInstrument> instruments;
    private final RestApi api;
    private String message = "";
    private final FragmentUpdate main;
    private int currentId = 0;
    private final InternalStorage storage;
    private String requestType;

    public Updater(Context context, FragmentUpdate main) {
        this.main = main;
        storage = new InternalStorage(context);
        List<Instrument> oldStatesOfInstruments = storage.readInstrumentsFromServer();
        instruments = storage.readUpdatedInstruments();
        api = new RestApi(context);

        if (instruments == null || instruments.isEmpty()) finishUpdate(null);
        else computeNextUpdate();
    }

    private void computeNextUpdate() {
        while (currentId < instruments.size() && !instruments.get(currentId).isAnyDataUpdated()) {
                currentId += 1;
        }
        if (currentId >= instruments.size()) {
            finishUpdate(RequestResponseStatus.OK);
            return;
        }
        if (instruments.get(currentId).isDeleted()) {
            api.deleteInstrument(instruments.get(currentId).getId(), this);
            requestType = "DELETE";
        }
        else if (instruments.get(currentId).isNew()) {
            api.sendNewInstrument(instruments.get(currentId), this);
            requestType = "POST";
        } else if (instruments.get(currentId).isQuantityChanged()) {
            api.updateInstrumentQuantity(instruments.get(currentId).getId(),
                    instruments.get(currentId).getChangedQuantity(), this);
            requestType = "INC/DEC";
        }  else if (instruments.get(currentId).existFieldUpdate()) {
            api.updateInstrument(instruments.get(currentId), this);
            requestType = "PUT";
        }
    }

    private void finishUpdate(RequestResponseStatus status) {
        if (status == RequestResponseStatus.OK || status == null) {
            storage.deleteData();
        } else {
            try {
                storage.saveUpdate(instruments);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        main.updateView(status, message);
    }

    public void update(RequestResponseStatus status, String message, boolean isQuantityChanged) {
        if (status == RequestResponseStatus.OK || status == RequestResponseStatus.CONFLICT) {
            if (status == RequestResponseStatus.CONFLICT) resolveConflicts(message);
            if (isQuantityChanged) instruments.get(currentId).removeLastQuantityChange();
            else if (requestType.equals("POST")) {
                instruments.get(currentId).setAsNew(false);
                instruments.get(currentId).setId(Integer.parseInt(message));
            }
            else instruments.get(currentId).markAsCurrentVersion();
            if (instruments.get(currentId).isDeleted()) instruments.remove(currentId);

            computeNextUpdate();
        }
        else if (status == RequestResponseStatus.NOT_FOUND) {
            updateNotFoundMessage();
            if (isQuantityChanged) instruments.get(currentId).removeLastQuantityChange();
            else instruments.get(currentId).markAsCurrentVersion();
            computeNextUpdate();
        }
        else if (status == RequestResponseStatus.TIMEOUT || status == RequestResponseStatus.UNAUTHORIZED) {
            finishUpdate(status);
        }
    }

    private void resolveConflicts(String conflictMessage) {
        if (requestType.equals("PUT")) {
            message += "Poniższe wartości zostały nadpisane: ";
            String[] splitedMessage = conflictMessage.split(";");
            if (!splitedMessage[0].isEmpty()) {
                message += "\"" + instruments.get(currentId).getManufacturer() + "\" ";
                instruments.get(currentId).setManufacturer(splitedMessage[0]);
            }
            if (splitedMessage.length > 1 && !splitedMessage[1].isEmpty()) {
                message += "\"" + instruments.get(currentId).getModel() + "\" ";
                instruments.get(currentId).setModel(splitedMessage[1]);
            }
            if (splitedMessage.length > 2 && !splitedMessage[2].isEmpty()) {
                message += "\"" + instruments.get(currentId).getPrice() + "\" ";
                instruments.get(currentId).setPrice(Float.parseFloat(splitedMessage[2]));
            }
            message += "\n\n";
        } else {
            String sign = instruments.get(currentId).getChangedQuantity() >= 0 ? "+" : "";
            System.out.println("SIGNN  "+sign);
            message += "Nie udało się zmienić liczby instrumentu "
                    + sign
                    + instruments.get(currentId).getChangedQuantity()
                    + " dla \""
                    + instruments.get(currentId).getModel()
                    + "\".\n\n";
        }
    }

    private void updateNotFoundMessage() {
        if (instruments.get(currentId).isDeleted()) {
            message += "Instrument " + instruments.get(currentId).getModel() + " został już usunięty.\n\n";
            instruments.remove(currentId);
        }
        else if (instruments.get(currentId).isQuantityChanged()) {
            message += "Nie udało się zmienić liczby instrumentu "
                    + (instruments.get(currentId).getChangedQuantity() >= 0 ? "+" : "")
                    + instruments.get(currentId).getChangedQuantity()
                    + " dla \""
                    + instruments.get(currentId).getModel()
                    + "\" - instrument został usumięty.\n\n";
        }
        else if (instruments.get(currentId).existFieldUpdate()) {
            message += "Nie udało się zmienić pól dla \""
                    + instruments.get(currentId).getModel()
                    + "\" - instrument został usumięty.\n\n";
        }
    }
}
