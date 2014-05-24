package road.policesystem.beans;

import road.movementdtos.dtos.VehicleDto;
import road.movementdtos.dtos.VehicleMovementDto;
import road.movementdtos.dtos.VehicleOwnerDto;
import road.policesystem.service.PoliceService;

import javax.faces.event.ActionEvent;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitch on 21-5-2014.
 */
@ManagedBean
public class StolenCarBean
{
    // Maximum number of suggestions for the vehicle search box.
    public static final int MAX_SUGGESTIONS = 15;

    @Inject
    private PoliceService policeService;

    private String licensePlate = "";

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public List<VehicleDto> getStolenCars()
    {
        return policeService.getAllStolenCars();
    }

    /**
     * Auto complete method for searching vehicles by license plate.
     * @param searchLicensePlate
     * @return
     */
    public List<String> completeStolenCars(String searchLicensePlate) {
        // Remove separators from license plate.
        searchLicensePlate = searchLicensePlate.replace("-", "").toUpperCase();

        // Retrieve all license plates, then check which ones match.
        List<VehicleDto> allStolenCars = getStolenCars();
        List<String> matchingStolenCars = new ArrayList();
        int numberOfPlates = 0;
        for(VehicleDto vehicle : allStolenCars)
        {
            String vehicleLicensePlate = vehicle.getLicensePlate();
            // Check if partially matching
            if(vehicleLicensePlate != null && vehicleLicensePlate.startsWith(searchLicensePlate))
            {
                matchingStolenCars.add(vehicleLicensePlate);

                // Stop searching when we have reached the maximum number of suggestions.
                if(++numberOfPlates > MAX_SUGGESTIONS) break;
            }
        }

        return matchingStolenCars;
    }

    /**
     * Check if we found a license plate, so that we can show detailed vehicle info.
     * @return
     */
    public boolean isActive()
    {
        return this.getStolenCarByLicensePlate() != null;
    }

    public VehicleDto getStolenCarByLicensePlate()
    {
        return policeService.getStolenCarByLicensePlate(licensePlate);
    }

    public List<VehicleOwnerDto> getVehicleOwners()
    {
        if(licensePlate == null) return new ArrayList();
        return policeService.getVehicleOwners(licensePlate);
    }

    public List<VehicleMovementDto> getVehicleMovements()
    {
        if(licensePlate == null) return new ArrayList();
        return policeService.getVehicleMovements(licensePlate);
    }
}
