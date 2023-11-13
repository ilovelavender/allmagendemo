package com.olegrubin.allmagendemo.config.converter;

import com.olegrubin.allmagendemo.model.enums.StatsPeriod;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StatsPeriodConverter implements Converter<String, StatsPeriod> {

    @Override
    public StatsPeriod convert(@NonNull String str) {
        return StatsPeriod.forValue(str);
    }
}
