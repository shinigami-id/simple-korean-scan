package com.wiryaimd.korean_scan.model;

import java.util.List;

public class TlxModel {

    private List<TlxBlockModel> blockList;

    public TlxModel(List<TlxBlockModel> blockList) {
        this.blockList = blockList;
    }

    public List<TlxBlockModel> getBlockList() {
        return blockList;
    }
}
