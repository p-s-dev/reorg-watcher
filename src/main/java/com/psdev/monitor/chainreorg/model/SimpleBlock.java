package com.psdev.monitor.chainreorg.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigInteger;

@Data
public class SimpleBlock {
    @NonNull
    BigInteger blockNumber;
    @NonNull
    String blockHash;
}
