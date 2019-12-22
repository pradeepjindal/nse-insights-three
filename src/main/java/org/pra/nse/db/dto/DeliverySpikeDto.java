package org.pra.nse.db.dto;

import java.time.LocalDate;

public class DeliverySpikeDto {
    private String symbol;
    private LocalDate tradeDate;
    private double open;
    private double high;
    private double low;
    private double close;
    private double tradedChgPrcnt;
    private double deliveredChgPrcnt;
    private double hmoPrcnt;
    private double omlPrcnt;

    public String toCsvString() {
        return  symbol + ","
                + tradeDate + ","
                + open + ","
                + high + ","
                + low + ","
                + close + ","
                + tradedChgPrcnt + ","
                + deliveredChgPrcnt + ","
                + hmoPrcnt + ","
                + omlPrcnt;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getTradedChgPrcnt() {
        return tradedChgPrcnt;
    }

    public void setTradedChgPrcnt(double tradedChgPrcnt) {
        this.tradedChgPrcnt = tradedChgPrcnt;
    }

    public double getDeliveredChgPrcnt() {
        return deliveredChgPrcnt;
    }

    public void setDeliveredChgPrcnt(double deliveredChgPrcnt) {
        this.deliveredChgPrcnt = deliveredChgPrcnt;
    }

    public double getHmoPrcnt() {
        return hmoPrcnt;
    }

    public void setHmoPrcnt(double hmoPrcnt) {
        this.hmoPrcnt = hmoPrcnt;
    }

    public double getOmlPrcnt() {
        return omlPrcnt;
    }

    public void setOmlPrcnt(double omlPrcnt) {
        this.omlPrcnt = omlPrcnt;
    }
}
