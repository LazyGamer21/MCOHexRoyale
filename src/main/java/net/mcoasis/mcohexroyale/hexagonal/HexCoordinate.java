package net.mcoasis.mcohexroyale.hexagonal;

import java.util.Objects;

public class HexCoordinate {
    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    int q, r;

    HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexCoordinate that = (HexCoordinate) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }
}

