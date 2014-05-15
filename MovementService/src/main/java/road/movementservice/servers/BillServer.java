package road.movementservice.servers;

//import aidas.usersystem.IUserManager;

import aidas.userservice.IUserManager;
import aidas.userservice.dto.UserDto;
import road.billdts.connections.IBillQuery;
import road.movementdts.connections.MovementConnection;
import road.movementdts.helpers.DateHelper;
import road.movementdts.helpers.Pair;
import road.movemententities.entities.VehicleMovement;
import road.movemententityaccess.dao.InvoiceDAO;
import road.movemententityaccess.dao.MovementDAO;
import road.movemententities.entities.City;
import road.movemententities.entities.CityRate;
import road.movementservice.connections.ServerConnection;

import java.util.Calendar;
import java.util.List;

import java.util.Date;
import java.util.List;

/**
 * Created by geh on 11-4-14.
 */
public class BillServer extends ServerConnection implements IBillQuery
{
    private InvoiceDAO invoiceDAO;
    private MovementDAO movementDAO;
    private IUserManager userManager;

    public BillServer(InvoiceDAO invoiceDAO, IUserManager userManager, MovementDAO movementDAO)
    {
        super(MovementConnection.FactoryName, MovementConnection.BillSystemQueue);

        this.invoiceDAO = invoiceDAO;
        this.userManager = userManager;
        this.movementDAO = movementDAO;
    }

    /**
     * Called when RPC is initialized.
     */
    public void init()
    {
        super.initRpc(IBillQuery.class, this);
        this.start();
    }

    @Override
    public UserDto authenticate(String user, String password)
    {
        return new UserDto(1, user + " @ bill system");
    }

    @Override
    public boolean adjustKilometerRate(City city, Date addDate, String price)
    {
        new CityRate(city, addDate, price);
        return true;
    }

    @Override
    public List<City> getCities() {
        return null;
    }

    @Override
    public Integer generateMonthlyInvoices()
    {
        Pair<Calendar, Calendar> invoiceDateRange = DateHelper.getDateRange();

        System.out.println("Executing query");
        List<VehicleMovement> movements = movementDAO.getMovementsForVehicleInRange(invoiceDateRange.getFirst(), invoiceDateRange.getSecond());

        System.out.println(movements.size());
        return null;
    }
}
