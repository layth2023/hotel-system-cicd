package com.Stats;

import java.math.BigDecimal;
import java.util.Map;

public class OccupancyStatsDTO {

    private long totalRooms;
    private long occupiedRooms;
    private long availableRooms;
    private BigDecimal occupancyRate;
    private Map<String, BigDecimal> occupancyByHotel;
    private Map<String, BigDecimal> occupancyByRoomType;

    public OccupancyStatsDTO() {}

    // Getters and Setters
    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }

    public long getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(long occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public long getAvailableRooms() { return availableRooms; }
    public void setAvailableRooms(long availableRooms) { this.availableRooms = availableRooms; }

    public BigDecimal getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(BigDecimal occupancyRate) { this.occupancyRate = occupancyRate; }

    public Map<String, BigDecimal> getOccupancyByHotel() { return occupancyByHotel; }
    public void setOccupancyByHotel(Map<String, BigDecimal> occupancyByHotel) { this.occupancyByHotel = occupancyByHotel; }

    public Map<String, BigDecimal> getOccupancyByRoomType() { return occupancyByRoomType; }
    public void setOccupancyByRoomType(Map<String, BigDecimal> occupancyByRoomType) { this.occupancyByRoomType = occupancyByRoomType; }
}
