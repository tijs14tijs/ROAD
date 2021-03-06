/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package road.carsystem.api;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import org.primefaces.model.UploadedFile;
import road.cardts.connections.CarClient;
import road.carsystem.domain.SocketResponse;
import road.carsystem.domain.netstate.*;
import road.movementdtos.sumo.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.TimerService;
import javax.websocket.Session;
import java.io.Serializable;
import java.util.List;

/**
 * This class simulates an actual data feed, by parsing an xml file, splitting it up
 * into timesteps, and sending the timesteps one by one, as if it were the actual cars sending them.
 */
@Singleton
public class CarSimulator implements Serializable
{
    @Resource
    private TimerService timerService;
    private XStream xStream;
    private Gson gson;

    private CarClient carClient;
    private Session session;
    private List<FcdTimeStep> timeSteps;
    private int sequence = 0;

    public CarSimulator()
    {

    }

    @PostConstruct
    private void init()
    {
        //With xStream you must explicitly tell it to process the annotations of the files, otherwise it won't work.
        this.xStream = new XStream();
        this.xStream.setMode(XStream.NO_REFERENCES);
        this.xStream.processAnnotations(FcdExport.class);
        this.xStream.processAnnotations(FcdTimeStep.class);
        this.xStream.processAnnotations(FcdVehicle.class);
        this.xStream.processAnnotations(Response.class);
        this.xStream.processAnnotations(SocketResponse.class);

        this.gson = new Gson();

        this.carClient = new CarClient();
        this.carClient.start();
    }

    public void runSimulation(Session session, UploadedFile file)
    {
        try
        {
            this.session = session;
            FcdExport export = ((FcdExport)this.xStream.fromXML(file.getInputstream()));
            this.timeSteps = export.getTimeSteps();
            this.sequence = 0;
            this.setTimer(0);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * @param wait time in seconds to wait
     */
    private void setTimer(int wait)
    {
        this.timerService.createTimer(wait * 1000, null);
    }

    @Timeout
    private void sendTimeStep()
    {
        FcdTimeStep step = this.timeSteps.get(this.sequence);
        SocketResponse response = new SocketResponse();
        response.timeStep = step;

        if(this.sendMovement(CarSocket.apiKey, this.sequence, step))
        {
            response.message = "Successfully sent movement. Sequence number " + this.sequence + " of " + this.timeSteps.size();
        }
        else
        {
            response.message = "Failed to send movement! Sequence number " + this.sequence + " of " + this.timeSteps.size();
        }

        this.session.getAsyncRemote().sendText(this.xStream.toXML(response)); //this.gson.toJson(response));

        if(this.sequence + 1 < this.timeSteps.size())
        {
            this.setTimer((int)(this.timeSteps.get(sequence + 1).getTime() - step.getTime()));
            this.sequence++;
        }
    }

    /**
     * method for adding movements
     *
     * @param api_key The api key, as a header parameter (autorisatiecode)
     * @param sequenceNumber The sequence number of the movements file
     * @param step A single timestep with the relevant movements to be pushed
     * @return a boolean to notify of the success or failure of the operation
     */
    public Boolean sendMovement(String api_key, int sequenceNumber, FcdTimeStep step)
    {
        String xmlMovement = this.xStream.toXML(new FcdExport(step));
        String result = this.carClient.addMovement(api_key, (long)sequenceNumber, xmlMovement);

        Boolean success = false;
        Response response = (Response)this.xStream.fromXML(result);
        String status;

        switch(response.status)
        {
            case "ok":
                status = "Successfully added movement for vehicle with id " + response.VEHICLE_ID;
                success = true;
                break;
            case "error":
                status = "API Key not valid";
                break;
            default:
                status = "Unrecognizable response status";
                break;
        }

        System.out.println(status);
        return success;
    }
}
