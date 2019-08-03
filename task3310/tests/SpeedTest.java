package com.javarush.task.task33.task3310.tests;

import com.javarush.task.task33.task3310.Helper;
import com.javarush.task.task33.task3310.Shortener;
import com.javarush.task.task33.task3310.strategy.HashBiMapStorageStrategy;
import com.javarush.task.task33.task3310.strategy.HashMapStorageStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class SpeedTest {

    public long getTimeToGetIds(Shortener shortener, Set<String> strings, Set<Long> ids) {
        long startTime = new Date().getTime();
        for (String str : strings) {
            ids.add(shortener.getId(str));
        }
        long finishTime = new Date().getTime();

        return finishTime - startTime;
    }

    public long getTimeToGetStrings(Shortener shortener, Set<Long> ids, Set<String> strings) {
        long startTime = new Date().getTime();
        for (Long id : ids) {
            strings.add(shortener.getString(id));
        }
        long finishTime = new Date().getTime();

        return finishTime - startTime;
    }

    @Test
    public void testHashMapStorage() {
        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();
        Set<Long> ids1 = new HashSet<>();
        Set<Long> ids2 = new HashSet<>();
        Set<String> strings1 = new HashSet<>();
        Set<String> strings2 = new HashSet<>();

        for (int i = 0; i < 10_000; i++) {
            origStrings.add(Helper.generateRandomString());
        }

        long timeIdsShortener1 = getTimeToGetIds(shortener1, origStrings, ids1);
        long timeIdsShortener2 = getTimeToGetIds(shortener2, origStrings, ids2);

        Assert.assertTrue(timeIdsShortener1 > timeIdsShortener2);

        long timeStringsShortener1 = getTimeToGetStrings(shortener1, ids1, strings1);
        long timeStringsShortener2 = getTimeToGetStrings(shortener2, ids2, strings2);

        Assert.assertEquals(timeStringsShortener1, timeStringsShortener2, 30);
    }
}
