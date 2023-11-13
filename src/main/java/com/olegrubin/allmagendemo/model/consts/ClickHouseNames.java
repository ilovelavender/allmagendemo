package com.olegrubin.allmagendemo.model.consts;

public interface ClickHouseNames {
    String EVENTS = "events";
    String VIEWS = "views";
    String MTV_POSTFIX = "_stats";

    static String viewName(String prefix) {
        return prefix + MTV_POSTFIX;
    }
}
