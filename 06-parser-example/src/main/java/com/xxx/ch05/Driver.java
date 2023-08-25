package com.xxx.ch05;
public class Driver extends org.apache.calcite.avatica.remote.Driver {
    static {
        new Driver().register();
    }

    @Override
    protected String getConnectStringPrefix() {
        return "jdbc:cdm:remote:";
    }
}
