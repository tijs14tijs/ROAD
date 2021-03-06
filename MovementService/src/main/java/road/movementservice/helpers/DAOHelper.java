package road.movementservice.helpers;

import road.movementdtos.dtos.MovementUserDto;
import road.movemententities.entities.MovementUser;
import road.movemententityaccess.dao.LoginDAO;
import road.movementservice.mapper.DtoMapper;
import road.userservice.UserDAO;
import road.userservice.dto.UserDto;

import java.sql.DataTruncation;

/**
 * Created by geh on 21-5-14.
 */
public class DAOHelper
{
    /**
     * This method creates a MovementUser if they have not logged in before.
     * This way, the user service is treated as a federated login, and can be easily
     * stripped from the MovementService so that we can deploy a product that has even more modularity.
     * @param userDAO DAO for user objects
     * @param loginDAO DAO for logging in
     * @param dtoMapper mapper for entities to Data Transfer Objects
     * @param userName the username
     * @param password the password
     * @return MovementUSerDto, will be null if login is not valid.
     */
    public static MovementUserDto authenticate(UserDAO userDAO, LoginDAO loginDAO, DtoMapper dtoMapper, String userName, String password)
    {
        UserDto userDto = userDAO.login(userName, password);
        if(userDto != null)
        {
            MovementUser mUser = loginDAO.getUser(userDto.getUsername());
            if(mUser == null)
            {
                mUser = loginDAO.register(userDto.getUsername(), userDto.getEmail());
            }
            return dtoMapper.toMovementUserDto(mUser);
        }
        else
        {
            return null;
        }
    }
}
