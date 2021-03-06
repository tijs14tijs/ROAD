package road.billsystem.beans;

import road.billsystem.service.BillService;
import road.movementdtos.dtos.CityDto;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mitch
 */
@Named
@RequestScoped
public class PriceBean implements Serializable
{
    private String price;
    @Inject
    private BillService billService;

    public List<CityDto> getCities() {
        return billService.getCities();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String adjustRate(CityDto city)
    {
        if(price.matches("\\d+[,.]\\d{2}"))
        {
            billService.adjustKilometerRate(city, new Date(), Double.parseDouble(price));
            return "The kilometer rates has been changed successfully.";
        }
        else
        {
            return "Failed to adjust the kilometer rate. Enter a valid price.";
        }
    }
}