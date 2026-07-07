package com.voluntoptier.project.entities;

/**
 * Represents a postal address, including street, house number, city,
 * postal code, and country. Used as part of the {@link User} class to
 * store the user's home address information.
 *
 * @param street the street name
 * @param houseNumber the house number
 * @param city the city name
 * @param postalCode the postal code
 * @param country the country name
 */
public record Address(
        String street,
        int houseNumber,
        String city,
        int postalCode,
        String country
) {}
