public class StockCard {
    private String stockCode;
    private String stockName;
    private int stockType;
    private String unit;
    private String barcode;
    private double kdvType;
    private String detail;
    private String createDate;

    public StockCard(String stockCode, String stockName, int stockType, String unit, String barcode, double kdvType,
                     String detail, String createDate) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.stockType = stockType;
        this.unit = unit;
        this.barcode = barcode;
        this.kdvType = kdvType;
        this.detail = detail;
        this.createDate = createDate;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getKdvType() {
        return kdvType;
    }

    public void setKdvType(double kdvType) {
        this.kdvType = kdvType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "StockCard{" +
                "stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", stockType=" + stockType +
                ", unit='" + unit + '\'' +
                ", barcode='" + barcode + '\'' +
                ", kdvType=" + kdvType +
                ", detail='" + detail + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}