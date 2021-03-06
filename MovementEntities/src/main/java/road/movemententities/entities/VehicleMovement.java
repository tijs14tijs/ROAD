package road.movemententities.entities;

import javax.persistence.*;

/**
 * The VehicleMovement class is the entity that maps vehicles to movements, including some more details
 *
 * Created by Niek  on 4/03/14..
 *  Aidas 2014
 */
@Entity
public class VehicleMovement implements MovementEntity<Integer>
{
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    Movement movement;

    /* The moving vehicle */
    @ManyToOne
    VehicleOwnership vehicleOwnership;

    /* Vehicle position on the lane */
    private float position;
    /* Vehicle speed in m/s */
    private float speed;
    /* WGS84 latitude of vehicle */
    private double latitude;
    /* WGS84 longitude of vehicle */
    private double longitude;


    /**
     * Empty constructor for JPA
     */
    public VehicleMovement()
    {
    }

    /**
     * Create a new instance of this
     * @param movement the {@link Movement} of this VehicleMovement
     * @param vehicle the {@link Vehicle} belonging to this movement
     * @param position the current position of the vehicle
     * @param speed the current speed of the vehicle
     */
    public VehicleMovement(Movement movement, Vehicle vehicle, float position, float speed)
    {
        this.movement = movement;
        this.vehicleOwnership = vehicle.getCurrentOwner();
        this.position = position;
        this.speed = speed;
    }


    /**
     * Create a new instance of this
     * @param movement the {@link Movement} of this {@link VehicleMovement}
     * @param vehicleOwnership the {@link road.movemententities.entities.VehicleOwnership} of this Movement
     * @param position the current position of the vehicle
     * @param speed the current speed of the vehicle
     */
    public VehicleMovement(Movement movement, VehicleOwnership vehicleOwnership, float position, float speed)
    {
        this.movement = movement;
        this.vehicleOwnership = vehicleOwnership;
        this.position = position;
        this.speed = speed;
    }

    /**
     * Create a new instance of this
     * @param movement the current {@link Movement} of this {@link road.movemententities.entities.VehicleMovement}
     * @param vehicle  the {@link Vehicle} belonging to this movement     *
     * @param position the current position of the vehicle
     * @param speed the current speed of the vehicle
     * @param latitude the current latitude of the vehicle
     * @param longitude the current longitude of the vehicle
     */
    public VehicleMovement(Movement movement, Vehicle vehicle, float position, float speed, double latitude, double longitude)
    {
        this(movement, vehicle, position, speed);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Create a new instance of this
     * @param movement the current {@link Movement} of this {@link road.movemententities.entities.VehicleMovement}
     * @param vehicleOwnership the {@link road.movemententities.entities.VehicleOwnership} of this Movement
     * @param position the current position of the vehicle
     * @param speed the current speed of the vehicle
     * @param latitude the current latitude of the vehicle
     * @param longitude the current longitude of the vehicle
     */
    public VehicleMovement(Movement movement, VehicleOwnership vehicleOwnership, float position, float speed, double latitude, double longitude)
    {
        this(movement, vehicleOwnership, position, speed);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //region Properties
    public Integer getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public VehicleOwnership getVehicleOwnership()
    {
        return this.vehicleOwnership;
    }

    public void setVehicle(VehicleOwnership vehicleOwnership)
    {
        this.vehicleOwnership = vehicleOwnership;
    }

    public float getPosition()
    {
        return position;
    }

    public void setPosition(float position)
    {
        this.position = position;
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }
    public Movement getMovement()
    {
        return movement;
    }
}
