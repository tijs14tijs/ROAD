/*
 * Copyright by AIDaS.
 */

package road.userservice;

import road.security.Security;
import road.userservice.converters.UserConverter;
import road.userservice.dto.Right;
import road.userservice.dto.UserDto;
import road.userservice.entities.UserEntity;
import road.userservice.exceptions.UserSystemException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class representing the implementation of the {@link UserDAO}.
 *
 * @author Geert
 */
public class UserDAOImpl implements UserDAO
{

    /**
     * The entity manager of the database.
     */
    private EntityManager em;

    /**
     * Create a new instance of the {@link UserDAOImpl} class.
     *
     * @param emf the {@link EntityManagerFactory} from which the {@link EntityManager} can be created.
     */
    public UserDAOImpl(EntityManagerFactory emf)
    {
        this.em = emf.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void demandRight(String authId, Right right) throws UserSystemException
    {
        if (!this.verifyRight(authId, right))
        {
            throw new UserSystemException("The user doesn't have to provided right.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto login(String username, String password)
    {
        UserEntity user = road.utils.Query.getSingleOrDefault(
                this.em.createQuery("SELECT u FROM USERS u WHERE u.username = :username", UserEntity.class)
                        .setParameter("username", username)
        );
        UserDto userDto = null;

        try
        {
            if (user != null && user.getPassword().equals(Security.processPassword(password, username, user.getSalt())))
            {
                userDto = UserConverter.toUserDto(user);
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeySpecException ex)
        {
            Logger.getLogger(UserDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return userDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto register(String username, String password) throws UserSystemException
    {
        if (username == null || username.isEmpty() || username.length() > 28)
        {
            throw new UserSystemException("The username is mandatory and can have a maximum length of 28 characters.");
        } else if (password == null || password.isEmpty() || password.length() < 8 || password.length() > 4000)
        {
            throw new UserSystemException("The password is mandatory and must have a length between 8 - 4000 characters.");
        }

        try
        {
            byte[] salt = Security.generateSalt();
            String processedPassword = Security.processPassword(password, username, salt);
            UserEntity newuser = new UserEntity(username, processedPassword, salt);
            this.em.persist(newuser);
            UserDto userdto = new UserDto(newuser.getId(), newuser.getUsername());
            return userdto;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeySpecException ex)
        {
            Logger.getLogger(UserDAOImpl.class.getName()).log(Level.SEVERE, null, ex);

            throw new UserSystemException(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verifyRight(String username, Right right)
    {
        return this.em.createQuery("SELECT COUNT(u) FROM USERS u WHERE u.username = :username AND (:right IN(u.rights) OR :right IN(u.roles.rights))", int.class)
                .setParameter("username", username)
                .setParameter("right", right)
                .getFirstResult() == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String changePassword(int id, String oldPassword, String newPassword, String newPasswordValidate)
    {
        if (newPassword == null || newPassword.isEmpty() || newPasswordValidate == null || newPasswordValidate.isEmpty())
        {
            return "Your new password cannot be left empty.";
        } else if (!newPassword.equals(newPasswordValidate))
        {
            return "The entered passwords are not the same.";
        } else if (oldPassword == null || oldPassword.isEmpty())
        {
            return "Please enter you password.";
        }

        UserEntity user = this.em.find(UserEntity.class, id);

        try
        {
            if (user == null)
            {
                return "Unknown user.";
            } else if (!user.getPassword().equals(Security.processPassword(oldPassword, user.getUsername(), user.getSalt())))
            {
                return "The entered password is incorrect.";
            }

            user.setPassword(Security.processPassword(newPassword, user.getUsername(), user.getSalt()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeySpecException ex)
        {
            Logger.getLogger(UserDAOImpl.class.getName()).log(Level.SEVERE, null, ex);

            return "Internal server error.";
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeDetails(int id, String name, String street, String houseNumber, String postalCode, String city)
    {
        UserEntity user = this.em.find(UserEntity.class, id);

        if (user == null)
        {
            return false;
        }

        user.setName(name);
        user.setStreet(street);
        user.setHouseNumber(houseNumber);
        user.setPostalCode(postalCode);
        user.setCity(city);
        this.em.persist(user);

        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param searchQuery the name to search for
     * @return List of found users
     */
    @Override
    public List<UserEntity> findUsersByName(String searchQuery)
    {
        Query query = em.createQuery("Select user from USERS user WHERE user.username like :username or user.name like :username");
        query.setParameter("username", "%" + searchQuery + "%");

        return query.getResultList();
    }
}