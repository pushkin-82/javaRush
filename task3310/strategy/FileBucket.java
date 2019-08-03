package com.javarush.task.task33.task3310.strategy;

import com.javarush.task.task33.task3310.ExceptionHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBucket {
    private Path path;

    public FileBucket() {
        try {
            path = Files.createTempFile(null, null);
            Files.deleteIfExists(path);
            Files.createFile(path);
        } catch (IOException e){
            ExceptionHandler.log(e);
        }
        path.toFile().deleteOnExit();
    }

    public long getFileSize() {
        long size = 0;

        try {
            size = Files.size(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }

        return size;
    }

    public void putEntry(Entry entry) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(path));
            outputStream.writeObject(entry);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }

    }

    public Entry getEntry() {
        Entry entry = null;

        if (getFileSize() > 0) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(path));
                entry = (Entry) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                ExceptionHandler.log(e);
            }
        }
        return entry;
    }

    public void remove() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }

    }
}
