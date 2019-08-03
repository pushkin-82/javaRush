package com.javarush.task.task33.task3310.strategy;

public class FileStorageStrategy implements StorageStrategy {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final long DEFAULT_BUCKET_SIZE_LIMIT = 10000;
    private FileBucket[] table;
    private int size;
    private long bucketSizeLimit = DEFAULT_BUCKET_SIZE_LIMIT;
    private long maxBucketSize;

    public FileStorageStrategy() {
        table = new FileBucket[DEFAULT_INITIAL_CAPACITY];

        for (int i = 0; i < table.length; i++) {
            table[i] = new FileBucket();
        }
    }

    public long getBucketSizeLimit() {
        return bucketSizeLimit;
    }

    public void setBucketSizeLimit(long bucketSizeLimit) {
        this.bucketSizeLimit = bucketSizeLimit;
    }

    public int hash(Long k) {
        int h;
        return (k == null) ? 0 : (h = k.hashCode()) ^ (h >>> 16);
    }

    public int indexFor(int hash, int length) {
        return hash & (length-1);
    }

    public Entry getEntry(Long key) {
        int hash = hash(key);

        for (Entry e = table[indexFor(hash, table.length)].getEntry(); e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }

    public void resize(int newCapacity) {
        FileBucket[] newTable = new FileBucket[newCapacity];
        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = new FileBucket();
        }

        transfer(newTable);
        table = newTable;
    }

    public void transfer(FileBucket[] newTable) {
        FileBucket[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            if (src[j] != null) {
                Entry e = src[j].getEntry();
                if (e != null) {
                    src[j] = null;
                    do {
                        Entry next = e.next;
                        int i = indexFor(e.hash, newCapacity);
                        e.next = newTable[i].getEntry();
                        newTable[i].putEntry(e);
                        e = next;
                    } while (e != null);
                }
                table[j].remove();
                table[j] = null;
            }
        }
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));
        size++;
        if (table[bucketIndex].getFileSize() > bucketSizeLimit) {
            resize(2 * table.length);
        }
    }

    public void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(hash, key, value, e));
        size++;
    }




    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        FileBucket[] tab;
        String str;
        if ((tab = table) != null && size > 0) {
            for (FileBucket bucket : tab) {
                Entry e = bucket.getEntry();
                for (; e != null; e = e.next) {
                    if ((str = e.value) == value ||
                            (value != null && value.equals(str)))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void put(Long key, String value) {
        if (key == null) {
            for (Entry e = table[0].getEntry(); e != null; e = e.next) {
                if (e.key == null) {
                    e.value = value;
                }
            }
            addEntry(0, null, value, 0);
        } else {

            int hash = hash(key);
            int i = indexFor(hash, table.length);
            for (Entry e = table[i].getEntry(); e != null; e = e.next) {
                Object k;
                if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                    e.value = value;
                }
            }
            addEntry(hash, key, value, i);
        }
    }

    @Override
    public Long getKey(String value) {
        if (containsValue(value)) {
            for (FileBucket bucket : table) {
                Entry entry = bucket.getEntry();
                while (entry != null) {
                    if (entry.getValue().equals(value)) {
                        return entry.getKey();
                    }
                    entry = entry.next;
                }
            }
        }
        return null;
    }

    @Override
    public String getValue(Long key) {
        return getEntry(key).getValue();
    }
}
