package com.olegrubin.allmagendemo.dao.api;

public interface CsvAwareStorage {

    long insertFromCsvFile(String fileUrl, String tableName);
}
