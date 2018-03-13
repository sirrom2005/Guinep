package com.trafalgartmc.guinep.Classes;

/**
 * Created by Rohan Morris
 * on 6/15/2017.
 */

public class InvoiceData {
    private int invoice_no;
    private String currency;
    private Double total;
    private String route;

    public InvoiceData(int invoice_no, String currency, Double total, String route) {
        this.invoice_no = invoice_no;
        this.currency   = currency;
        this.total      = total;
        this.route      = route;
    }

    public int getInvoiceNo() {
        return invoice_no;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getTotal() {
        return total;
    }

    public String getRoute() {
        return route;
    }

    public static final class InvoiceListData extends InvoiceData {
        public InvoiceListData(int invoice_no, String currency, Double total, String route)
        {
            super(invoice_no, currency, total, route);
        }
    }
}