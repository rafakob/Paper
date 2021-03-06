package io.paperdb;

import android.content.Context;
import com.esotericsoftware.kryo.Serializer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import rx.Observable;

public class Book {

    private final Storage mStorage;

    protected Book(Context context, String dbName, HashMap<Class, Serializer> serializers) {
        mStorage = new DbStoragePlainFile(context.getApplicationContext(), dbName, serializers);
    }

    /**
     * Destroys all data saved in Book.
     */
    public void destroy() {
        mStorage.destroy();
    }

    public Observable<Boolean> destroyRx() {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mStorage.destroy();
                return true;
            }
        });
    }

    /**
     * Saves any types of POJOs or collections in Book storage.
     *
     * @param key   object key is used as part of object's file name
     * @param value object to save, must have no-arg constructor, can't be null.
     * @param <T>   object type
     * @return this Book instance
     */
    public <T> Book write(String key, T value) {
        if (value == null) {
            throw new PaperDbException("Paper doesn't support writing null root values");
        } else {
            mStorage.insert(key, value);
        }
        return this;
    }

    public <T> Observable<Boolean> writeRx(final String key, final T value) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                write(key, value);
                return true;
            }
        });
    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key object key to read
     * @return the saved object instance or null
     */
    public <T> T read(String key) {
        return read(key, null);
    }

    public <T> Observable<T> readRx(String key) {
        return readRx(key, null);
    }

    /**
     * Instantiates saved object using original object class (e.g. LinkedList). Support limited
     * backward and forward compatibility: removed fields are ignored, new fields have their
     * default values.
     * <p/>
     * All instantiated objects must have no-arg constructors.
     *
     * @param key          object key to read
     * @param defaultValue will be returned if key doesn't exist
     * @return the saved object instance or null
     */
    public <T> T read(String key, T defaultValue) {
        T value = mStorage.select(key);
        return value == null ? defaultValue : value;
    }

    public <T> Observable<T> readRx(final String key, final T defaultValue) {
        return Observable.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return read(key, defaultValue);
            }
        });
    }


    /**
     * Check if an object with the given key is saved in Book storage.
     *
     * @param key object key
     * @return true if object with given key exists in Book storage, false otherwise
     */
    public boolean exist(String key) {
        return mStorage.exist(key);
    }

    public Observable<Boolean> existRx(final String key) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return mStorage.exist(key);
            }
        });
    }

    /**
     * Delete saved object for given key if it is exist.
     *
     * @param key object key
     */
    public void delete(String key) {
        mStorage.deleteIfExists(key);
    }

    public Observable<Boolean> deleteRx(final String key) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mStorage.deleteIfExists(key);
                return true;
            }
        });
    }

    /**
     * Returns all keys for objects in book.
     *
     * @return all keys
     */
    public List<String> getAllKeys() {
        return mStorage.getAllKeys();
    }

    public Observable<List<String>> getAllKeysRx() {
        return Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return getAllKeys();
            }
        });
    }
}
