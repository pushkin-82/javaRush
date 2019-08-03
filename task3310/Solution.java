package com.javarush.task.task33.task3310;

import com.javarush.task.task33.task3310.strategy.HashMapStorageStrategy;
import com.javarush.task.task33.task3310.strategy.StorageStrategy;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {

    public static Set<Long> getIds(Shortener shortener, Set<String> strings) {
        Set<Long> resultSet = new HashSet<>();

        for (String str : strings) {
            resultSet.add(shortener.getId(str));
        }
        return resultSet;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {
        Set<String> resultSet = new HashSet<>();

        for (Long key : keys) {
            resultSet.add(shortener.getString(key));
        }
        return resultSet;
    }

    public static void testStrategy(StorageStrategy strategy, long elementsNumber) {
        Shortener shortener = new Shortener(strategy);
        Set<String> strSet = new HashSet<>();
        Set<Long> keySet;
        Set<String> resultSet;
        long beginTime, finishTime;

        Helper.printMessage(strategy.getClass().getSimpleName());

        for (int i = 0; i < elementsNumber; i++) {
            strSet.add(Helper.generateRandomString());
        }

        beginTime = new Date().getTime();
        keySet = Solution.getIds(shortener, strSet);
        finishTime = new Date().getTime();

        Helper.printMessage((finishTime - beginTime) + "");

        beginTime = new Date().getTime();
        resultSet = Solution.getStrings(shortener, keySet);
        finishTime = new Date().getTime();

        Helper.printMessage((finishTime - beginTime) + "");

        if (resultSet.size() == strSet.size()) {
            Helper.printMessage("Тест пройден.");
        } else {
            Helper.printMessage("Тест не пройден.");
        }

    }


    public static void main(String[] args) {
        Solution.testStrategy(new HashMapStorageStrategy(), 10000);

    }
}
