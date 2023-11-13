package com.olegrubin.allmagendemo.config.converter;

import com.olegrubin.allmagendemo.model.enums.AggregationField;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class AggregationFieldConverter implements Converter<String, AggregationField> {

    @Override
    public AggregationField convert(@NonNull String source) {
        return AggregationField.forValue(source);
    }
}
