package road.carsystem.domain.netstate;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by geh on 14-5-14.
 */
public class Netstate
{
    @XStreamImplicit(itemFieldName="timestep")
    public List<TimeStep> timeSteps;

    public Netstate()
    {

    }

    public Netstate(TimeStep timeStep)
    {
        this.timeSteps = new ArrayList();
        this.timeSteps.add(timeStep);
    }

}
