/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package road.policesystem.beans;

import com.ocpsoft.pretty.PrettyContext;
import road.policesystem.utils.Utilities;
import road.movementdtos.dtos.MovementUserDto;

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
 * This bean stores the user login session,
 * @author Mitch
 */
@Named("userBean") @SessionScoped
public class UserBean implements Serializable
{
    // Store the authenticated user details.
    private MovementUserDto loggedinUser;

    private String loginRedirect = " ";


    public void setLoggedinUser(MovementUserDto loggedinUser)
    {
        this.loggedinUser = loggedinUser;
    }

    public MovementUserDto getLoggedinUser()
    {
        return loggedinUser;
    }

    /**
     * Logout a user, then redirect to the login page.
     */
    public void logout()
    {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.invalidateSession();
            ec.redirect(Utilities.getContextRoot() + "/login/");

        } catch (IOException e) {
            Logger.getLogger(SessionBean.class.getName()).log(Level.SEVERE, null, e);
        }
    }


    /**
     * Get the redirection URL.
     * @return The redirection URL.
     */
    public String getLoginRedirect()
    {
        return loginRedirect;
    }


    /**
     * Redirect to the login page if the user has not been authenticated yet.
     */
    public void redirectIfNotLoggedIn()
    {

        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        if(this.loggedinUser == null){
            try
            {
                this.loginRedirect = PrettyContext.getCurrentInstance().getRequestURL().toURL();
                context.redirect(Utilities.getContextRoot() + "/login/");

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
