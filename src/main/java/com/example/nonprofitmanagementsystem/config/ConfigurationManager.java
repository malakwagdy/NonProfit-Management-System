package com.example.nonprofitmanagementsystem.config;

public class ConfigurationManager {
    private static ConfigurationManager instance;

    private String organizationName;
    private String organizationEmail;
    private String organizationAddress;
    private String defaultCurrency;
    private String defaultLanguage;
    private String receiptPrefix;
    private double taxDeductibleRate;
    private int reminderHoursBeforeEvent;

    private ConfigurationManager() {}

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {instance = new ConfigurationManager();}
        return instance;
    }

    public String getOrganizationName() {return organizationName;}
    public void setOrganizationName(String name) {this.organizationName = name;}

    public String getOrganizationEmail() {return organizationEmail;}
    public void setOrganizationEmail(String email) {this.organizationEmail = email;}

    public String getOrganizationAddress() {return organizationAddress;}
    public void setOrganizationAddress(String address) {this.organizationAddress = address;}

    public String getDefaultCurrency() {return defaultCurrency;}
    public void setDefaultCurrency(String currency) {this.defaultCurrency = currency;}

    public String getDefaultLanguage() {return defaultLanguage;}
    public void setDefaultLanguage(String language) {this.defaultLanguage = language;}

    public String getReceiptPrefix() {return receiptPrefix;}
    public void setReceiptPrefix(String prefix) {this.receiptPrefix = prefix;}

    public double getTaxDeductibleRate() {return taxDeductibleRate;}
    public void setTaxDeductibleRate(double rate) {this.taxDeductibleRate = rate;}

    public int getReminderHoursBeforeEvent() {return reminderHoursBeforeEvent;}
    public void setReminderHoursBeforeEvent(int hours) {this.reminderHoursBeforeEvent = hours;}
}