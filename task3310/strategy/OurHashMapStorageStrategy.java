package com.javarush.task.task33.task3310.strategy;

public class OurHashMapStorageStrategy implements StorageStrategy {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Entry[] table = new Entry[DEFAULT_INITIAL_CAPACITY];
    private int size;
    private int threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    private float loadFactor = DEFAULT_LOAD_FACTOR;


    public int hash(Long k) {
        int h;
        return (k == null) ? 0 : (h = k.hashCode()) ^ (h >>> 16);
    }

    public int indexFor(int hash, int length) {
        return hash & (length-1);
    }

    public Entry getEntry(Long key) {
        int hash = hash(key);

        for (Entry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }

    public void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == 1 << 30) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(newCapacity * loadFactor);
    }

    public void transfer(Entry[] newTable) {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    public void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(hash, key, value, e);
        size++;
    }


    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        Entry[] tab;
        String str;
        if ((tab = table) != null && size > 0) {
            for (Entry e : tab) {
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
            for (Entry e = table[0]; e != null; e = e.next) {
                if (e.key == null) {
                    e.value = value;
                }
            }
            addEntry(0, null, value, 0);
        } else {

            int hash = hash(key);
            int i = indexFor(hash, table.length);
            for (Entry e = table[i]; e != null; e = e.next) {
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
            for (Entry e : table) {
                while (e != null) {
                    if (e.getValue().equals(value)) {
                        return e.getKey();
                    }
                    e = e.next;
                }
            }
        }
        return null;
    }

    @Override
    public String getValue(Long key) {
        if (key == null) {
            for (Entry e = table[0]; e != null; e = e.next) {
                if (e.key == null)
                    return e.value;
            }
            return null;
        }

        int hash = hash(key);
        for (Entry e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
            Long k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }
        return null;

    }
}
