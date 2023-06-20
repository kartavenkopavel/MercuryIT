package community.redrover.mercuryit;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;


public class MercuryITSQLResponse extends MercuryITResponseAutoCloseable<MercuryITSQLResponse> {

    private final ResultSet resultSet;

    MercuryITSQLResponse(MercuryITConfigHolder configHolder, ResultSet resultSet) {
        super(configHolder);
        this.resultSet = resultSet;

        registerAutoCloseable(resultSet);
    }

    @SneakyThrows
    public boolean isEmpty() {
        return this.resultSet.getRow() == 0 && !this.resultSet.isBeforeFirst();
    }

    @SneakyThrows
    private Map<String, Object> getCurrentRow() {
        Map<String, Object> resultMap = new TreeMap<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            resultMap.put(metaData.getColumnLabel(i), resultSet.getObject(i));
        }

        return resultMap;
    }

    @SneakyThrows
    public Map<String, Object> getNextRow() {
        if (resultSet.next()) {
            return getCurrentRow();
        } else {
            return null;
        }
    }

    @SneakyThrows
    public <T> T getNextRow(Class<T> clazz) {
        Map<String, Object> currentRow = getNextRow();
        if (currentRow != null) {
            return config(MercuryITJsonConfig.class).fromMap(currentRow, clazz);
        } else {
            return null;
        }
    }

    @SneakyThrows
    public <T> List<T> getRows(Class<T> clazz) {
        List<T> resultList = new ArrayList<>();

        T object = getNextRow(clazz);
        while (object != null) {
            resultList.add(object);
            object = getNextRow(clazz);
        }

        return resultList;
    }
}
