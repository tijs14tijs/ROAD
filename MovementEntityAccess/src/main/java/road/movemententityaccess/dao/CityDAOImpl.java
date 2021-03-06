package road.movemententityaccess.dao;

import road.movemententities.entities.City;
import road.movemententities.entities.CityRate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * {@inheritDoc}
 *
 * Created by Mitch on 28/03/14.
 *  Aidas 2014
 */
public class CityDAOImpl implements CityDAO
{
    private EntityManager em;

    public CityDAOImpl(EntityManagerFactory emf){
        this.em = emf.createEntityManager();
    }

    /**
     * {@inheritDoc}
     * @return the number of cities found
     */
    @Override
    public Long count()
    {
        Query query = em.createQuery("Select count(city) from City city");

        return (Long)query.getSingleResult();
    }

    /**
     * {@inheritDoc}
     * @param cityIdentifier The sumo City Identifier
     * @return the found City object.
     */
    @Override
    public City find(String cityIdentifier)
    {
        Query query = em.createQuery("select city from City city where city.cityId = :cityID");
        query.setParameter("cityID", cityIdentifier);

        List<City> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    /**
     * @return the found City object.
     */
    @Override
    public List<City> findAll()
    {
        Query query = em.createQuery("select city from City city");

        List<City> resultList = query.getResultList();
        return resultList;
    }

    /**
     * {@inheritDoc}
     * @param cityId the ID of the city to change it for
     * @param addDate the new date of the kilomter rate
     * @param price the new price of the kilometer rate
     * @return true when success, false when not
     */
    @Override
    public boolean adjustKilometerRate(String cityId, Date addDate, double price)
    {
        try
        {
            em.getTransaction().begin();
            City city = find(cityId);
            CityRate cityRate = new CityRate(city, addDate, price);
            em.merge(cityRate);
            em.getTransaction().commit();
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }

 }
