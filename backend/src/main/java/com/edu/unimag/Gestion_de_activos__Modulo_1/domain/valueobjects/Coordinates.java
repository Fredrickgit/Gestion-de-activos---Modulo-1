package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCoordinatesException;

public final class Coordinates {

    private final double latitude;
    private final double longitude;

    private Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Coordinates of(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new InvalidCoordinatesException("La latitud debe estar entre -90 y 90");
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new InvalidCoordinatesException("La longitud debe estar entre -180 y 180");
        }
        return new Coordinates(latitude, longitude);
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Coordinates coordinates)) {
            return false;
        }
        return Double.compare(latitude, coordinates.latitude) == 0
                && Double.compare(longitude, coordinates.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Double.hashCode(latitude) + Double.hashCode(longitude);
    }

    @Override
    public String toString() {
        return latitude + "," + longitude;
    }
}
