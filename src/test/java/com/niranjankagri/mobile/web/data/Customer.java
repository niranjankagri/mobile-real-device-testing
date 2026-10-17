package com.niranjankagri.mobile.web.data;

/**
 * Checkout details as one value, so tests say who is buying instead of passing four strings. The sample customers
 * are made up; none of the data is real.
 */
public record Customer(String name, String email, String phone, String postcode) {

    /** Valid in every field. */
    public static Customer valid() {
        return new Customer("Mia Wagner", "mia@example.com", "+49 30 1234567", "10115");
    }

    /** A name and an email without "@"; phone and postcode left empty. */
    public static Customer withInvalidEmailAndMissingFields() {
        return new Customer("Mia Wagner", "mia-at-example", "", "");
    }
}
