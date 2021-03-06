/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package road.driversystem.beans;

import road.movementdtos.dtos.MovementUserDto;
import road.userservice.dto.UserDto;
import com.ocpsoft.pretty.PrettyContext;
import road.driversystem.domain.infoobjects.PaymentSession;
import road.driversystem.utils.Utlities;

import javax.ejb.SessionBean;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mitch
 */
@Named("userBean") @SessionScoped
public class UserBean implements Serializable
{
    private MovementUserDto loggedinUser;

    private String loginRedirect = " ";
    private PaymentSession paymentSession;


    public void setLoggedinUser(MovementUserDto loggedinUser)
    {
        this.loggedinUser = loggedinUser;
    }

    public MovementUserDto getLoggedinUser()
    {
        return loggedinUser;
    }

    public void logout()
    {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.invalidateSession();
            ec.redirect(Utlities.getContextRoot() + "/login/");

        } catch (IOException e) {
            Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public String getLoginRedirect()
    {
        return loginRedirect;
    }

    public void redirectIfNotLoggedIn()
    {

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if(this.loggedinUser == null){
            try
            {
                this.loginRedirect = PrettyContext.getCurrentInstance().getRequestURL().toURL();
                context.redirect(Utlities.getContextRoot() + "/login/");

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setPaymentSession(PaymentSession session){
        this.paymentSession = session;
    }

    public PaymentSession getAndClearPaymentSession(){
        PaymentSession toReturn = this.paymentSession;
        this.paymentSession = null;
        return toReturn;
    }
}
