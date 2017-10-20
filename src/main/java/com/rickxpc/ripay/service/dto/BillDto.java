package com.rickxpc.ripay.service.dto;

import java.util.List;

public class BillDto {
    private List<BillLineDto> lines;
    private BillSummaryDto summary;

    public List<BillLineDto> getLines() {
        return lines;
    }

    public void setLines(List<BillLineDto> lines) {
        this.lines = lines;
    }

    public BillSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(BillSummaryDto summary) {
        this.summary = summary;
    }
}
