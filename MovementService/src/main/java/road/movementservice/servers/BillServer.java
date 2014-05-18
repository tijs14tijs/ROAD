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
import road.movementservice.connections.ServerConnection;
import road.movementservice.mapper.DtoMapper;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by geh on 11-4-14.
 */
public class BillServer extends ServerConnection implements IBillQuery
{
    private InvoiceDAO invoiceDAO;
    private MovementDAO movementDAO;
    private IUserManager userManager;
    private DtoMapper dtoMapper;

    public BillServer(InvoiceDAO invoiceDAO, IUserManager userManager, MovementDAO movementDAO, DtoMapper dtoMapper)
    {
        super(MovementConnection.FactoryName, MovementConnection.BillSystemQueue);

        this.invoiceDAO = invoiceDAO;
        this.userManager = userManager;
        this.movementDAO = movementDAO;
        this.dtoMapper = dtoMapper;
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
    public Integer generateMonthlyInvoices(Integer month, Integer year)
    {

        Pair<Calendar, Calendar> invoiceDateRange = DateHelper.getDateRange(month, year);

        List<VehicleMovement> vehicleMovements = movementDAO.getMovementsForVehicleInRange(invoiceDateRange.getFirst(), invoiceDateRange.getSecond());
        Integer amountCreated = Integer.valueOf(invoiceDAO.generate(vehicleMovements, invoiceDateRange.getFirst().getTime(), invoiceDateRange.getSecond().getTime()));

        return 0;
    }


}
