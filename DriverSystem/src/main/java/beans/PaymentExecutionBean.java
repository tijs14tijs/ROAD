package beans;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.PayPalRESTException;
import domain.infoobjects.PaymentSession;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Niek on 13/05/14.
 * © Aidas 2014
 */
@RequestScoped
@Named
public class PaymentExecutionBean
{
    @Inject
    private UserBean userSession;

    private boolean failed;
    private String invoiceID;

    public boolean isFailed()
    {
        return failed;
    }

    public String getInvoiceID()
    {
        return invoiceID;
    }

    /**
     * After confirming the PayPal transaction on the PayPal page, the user will be redirected to this page.
     * The execution of the payment will be handled in this method
     */
    @PostConstruct
    public void initExecution(){
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

        //Set the invoice ID
        this.invoiceID = context.getRequestParameterMap().get("invoiceID");
        boolean success = Boolean.parseBoolean(context.getRequestParameterMap().get("success"));

        //Get the PayerID from the paypal response
        String payerID = context.getRequestParameterMap().get("PayerID");


        //Fail when the authorization was unsuccessful
        if(!success)
        {
            failed = true;
            return;
        }

        //Create a DAte object that is set to ten minutes ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -10);
        Date expired = calendar.getTime();

        PaymentSession session = userSession.getAndClearPaymentSession();

        //Validate the session, check the timestamps (max of 10 minutes) and validate the invoiceID
        if(session == null || session.getCreatedDate().before(expired) || !session.getInvoiceID().equals(invoiceID))
        {
            failed = true;
            return;
        }

        Map<String, String> sdkConfig = new HashMap<String, String>();
        sdkConfig.put("mode", "sandbox");

        try {

            //Create the APIContext using the AccessToken from the session
            APIContext apiContext = new APIContext(session.getAccesstoken());
            apiContext.setConfigurationMap(sdkConfig);

            //Get the payment using the REST api
            Payment payment = Payment.get(apiContext, session.getPaymentID());

            //Create a PaymentExecution and set the PayerID
            PaymentExecution execution = new PaymentExecution(payerID);


            //Execute the final payment
            Payment resultingPayment = payment.execute(apiContext, execution);


            //If the state of the resultingPayment is approved, set failed to false.
            if(resultingPayment.getState().equals("approved")){
                this.failed = false;
            } else {
                this.failed = true;
            }
        } catch(PayPalRESTException ex){
            ex.printStackTrace();
            this.failed = true;
        }
    }


}
