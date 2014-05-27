package road.movemententities.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Emeddable primary key for CityRates
 *
 * Created by Mitch on 12-5-2014.
 */
@Entity
public class CityRate
{
    public static double defaultKilometerRate = 0.10;

    @EmbeddedId
    @Column(unique = false, nullable = true)
    CityRateId id;

    @Column(unique = false, nullable = true)
    private double kilometerRate;

    public CityRate(){}

    public CityRate(City city, Date addDate, double kilometerRate)
    {
        id = new CityRateId();
        id.setCity(city);
        id.setAddDate(addDate);
        this.kilometerRate = kilometerRate;
    }

    public CityRateId getId() {
        return id;
    }

    public void setId(CityRateId id) {
        this.id = id;
    }

    public double getKilometerRate() {
        return kilometerRate;
    }

    public void setKilometerRate(double kilometerRate) {
        this.kilometerRate = kilometerRate;
    }
}
