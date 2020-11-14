package com.example.warehouseuser;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class InternalStorage<T> {

    private final String key;
    private final Context context;

    public InternalStorage(Context context, String key) {
        this.key = key;
        this.context = context;
    }

    public void removeFileContent() throws IOException {
        context.openFileOutput(key, Context.MODE_PRIVATE).close();
    }

    public void writeObject(T object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE | Context.MODE_APPEND);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public List<T> readAllObjects() throws IOException, ClassNotFoundException {
        List<T> objects = new ArrayList<>();

        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);

        while (fis.available() > 0) {
            objects.add((T) ois.readObject());
        }
        return objects;
    }

    public T readObject() throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        T object = (T) ois.readObject();
        return object;
    }
}
