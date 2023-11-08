package org.jasonf;

import org.jasonf.util.DateUtil;

/**
 * @Author jasonf
 * @Date 2023/11/4
 * @Description
 */

public class Constant {
    public static final String DEFAULT_ZK_CONNECT = "123.60.86.242:2181";
    public static final int DEFAULT_TIMEOUT = 10000;

    public static final String RPC_ROOT_PATH = "/rpc";
    public static final String PROVIDERS_ROOT_PATH = RPC_ROOT_PATH + "/providers";
    public static final String INVOKERS_ROOT_PATH = RPC_ROOT_PATH + "/invokers";

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final long INITIAL_TIMESTAMP = DateUtil.parse("2022-08-01").getTime();

    public static final long DATA_CENTER_BIT = 5;
    public static final long MACHINE_BIT = 5;
    public static final long TIMESTAMP_BIT = 42;
    public static final long SEQUENCE_BIT = 12;

    public static final long TIMESTAMP_LEFT = SEQUENCE_BIT;
    public static final long MACHINE_LEFT = TIMESTAMP_LEFT + TIMESTAMP_BIT;
    public static final long DATA_CENTER_LEFT = MACHINE_LEFT + MACHINE_BIT;

    public static final long DATA_CENTER_MAX = ~(-1L << DATA_CENTER_BIT);
    public static final long MACHINE_MAX = ~(-1L << MACHINE_BIT);
    public static final long SEQUENCE_MAX = ~(-1L << SEQUENCE_BIT);

    public static final long DATA_CENTER_ID = 5;
    public static final long MACHINE_ID = 3;
}
