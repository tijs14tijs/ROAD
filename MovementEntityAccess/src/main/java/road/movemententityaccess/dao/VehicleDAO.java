package road.movemententityaccess.dao;

import road.movementdtos.dtos.VehicleDto;
import road.movemententities.entities.Vehicle;

import java.util.List;

/**
 * Created by Niek on 28/03/14.
 * © Aidas 2014
 */
public interface VehicleDAO
{
    /**
     * Find a vehicle by it's license plate
     * @param licensePlate The license plate number of the vehicle
     * @return The found vehicle
     */
    Vehicle findByLicensePlate(String licensePlate);

    /**
     * Get a list of all the vehicles currently belonging to a user
     * @param userID The ID of the user
     * @return List of owned vehicles
     */
    List<VehicleDto> getVehiclesFromUser(Integer userID);

    /**
     * Change the owner of a certain vehicle
     * @param vehicle The vehicle switching owners
     * @param userID The user ID of the new owner.
     */
    void changeVehicleOwnership(Vehicle vehicle, int userID);

    /**
     * Update the provided vehicle.
     * @param vehicleDto the vehicle to be updated.
     * @return if the function was successful.
     */
    Boolean updateVehicle(VehicleDto vehicleDto);

}
