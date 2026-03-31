package com.Stats;

import java.math.BigDecimal;
import java.util.Map;

public class RevenueStatsDTO {

    private BigDecimal totalRevenue;
    private BigDecimal totalRefunds;
    private BigDecimal netRevenue;
    private long totalPayments;
    private long successfulPayments;
    private long failedPayments;
    private Map<String, BigDecimal> revenueByHotel;
    private Map<String, BigDecimal> revenueByPaymentMethod;

    public RevenueStatsDTO() {}

    // Getters and Setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getTotalRefunds() { return totalRefunds; }
    public void setTotalRefunds(BigDecimal totalRefunds) { this.totalRefunds = totalRefunds; }

    public BigDecimal getNetRevenue() { return netRevenue; }
    public void setNetRevenue(BigDecimal netRevenue) { this.netRevenue = netRevenue; }

    public long getTotalPayments() { return totalPayments; }
    public void setTotalPayments(long totalPayments) { this.totalPayments = totalPayments; }

    public long getSuccessfulPayments() { return successfulPayments; }
    public void setSuccessfulPayments(long successfulPayments) { this.successfulPayments = successfulPayments; }

    public long getFailedPayments() { return failedPayments; }
    public void setFailedPayments(long failedPayments) { this.failedPayments = failedPayments; }

    public Map<String, BigDecimal> getRevenueByHotel() { return revenueByHotel; }
    public void setRevenueByHotel(Map<String, BigDecimal> revenueByHotel) { this.revenueByHotel = revenueByHotel; }

    public Map<String, BigDecimal> getRevenueByPaymentMethod() { return revenueByPaymentMethod; }
    public void setRevenueByPaymentMethod(Map<String, BigDecimal> revenueByPaymentMethod) { this.revenueByPaymentMethod = revenueByPaymentMethod; }
}
