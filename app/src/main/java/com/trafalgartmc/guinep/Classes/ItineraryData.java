package com.trafalgartmc.guinep.Classes;

/**
 * Created by Rohan Morris
 * on 6/15/2017.
 */

public class ItineraryData {
    private int invoiceno;
    private String recordloca;
    private String classofsvc;
    private String departureAirport;
    private String d_latitude;
    private String d_longitude;
    private String arrivalAirport;
    private String a_latitude;
    private String a_longitude;
    private String destination;
    private String flightno;
    private String departcityname;
    private String departdate;
    private String departtime;
    private String cityname;
    private String arrivedate;
    private String arrivetime;
    private String airline;

    public ItineraryData(int invoiceNo,
                         String recordloca,
                         String classofsvc,
                         String departureAirport,
                         String d_latitude,
                         String d_longitude,
                         String arrivalAirport,
                         String a_latitude,
                         String a_longitude,
                         String destination,
                         String flightno,
                         String departcityname,
                         String departdate,
                         String departtime,
                         String cityname,
                         String arrivedate,
                         String arrivetime,
                         String airline) {
        this.invoiceno      = invoiceNo;
        this.recordloca     = recordloca;
        this.classofsvc     = classofsvc;
        this.departureAirport = departureAirport;
        this.d_latitude     = d_latitude;
        this.d_longitude    = d_longitude;
        this.arrivalAirport = arrivalAirport;
        this.a_latitude     = a_latitude;
        this.a_longitude    = a_longitude;
        this.destination    = destination;
        this.flightno       = flightno;
        this.departcityname = departcityname;
        this.departdate     = departdate;
        this.departtime     = departtime;
        this.cityname       = cityname;
        this.arrivedate     = arrivedate;
        this.arrivetime     = arrivetime;
        this.airline        = airline;
    }

    private ItineraryData(int invoiceNo, String destination, String departureDate) {
        this.invoiceno      = invoiceNo;
        this.destination    = destination;
        this.departdate     = departureDate;
    }

    public int getInvoiceNo() {
        return invoiceno;
    }

    public String getRecordLocal() { return recordloca; }

    public String getClassOfSvc() {
        return classofsvc;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getDestinationLatitude() {
        return d_latitude;
    }
    public String getDestinationLongitude() {
        return d_longitude;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public String getArrivalLatitude() {
        return a_latitude;
    }
    public String getArrivalLongitude() {
        return a_longitude;
    }

    public String getDestination() {
        return destination;
    }

    public String getFlightNo() {
        return flightno;
    }

    public String getDepartureCityName() {
        return departcityname;
    }

    public String getDepartureDate() { return departdate; }

    public String getDepartureTime() {
        return departtime;
    }

    public String getArrivalCityName() {
        return cityname;
    }

    public String getArrivalDate() {
        return arrivedate;
    }

    public String getArrivalTime() {
        return arrivetime;
    }

    public String getAirline() {
        return airline;
    }

    public final static class ItineraryListData extends ItineraryData {
        private String itineraryCode;

        public ItineraryListData(
                int invoiceNo,
                String itineraryCode,
                String destination,
                String departureDate)
        {
            super(invoiceNo,destination,departureDate);
            this.itineraryCode = itineraryCode;
        }

        public String getItineraryCode() {
            return itineraryCode;
        }
    }
}