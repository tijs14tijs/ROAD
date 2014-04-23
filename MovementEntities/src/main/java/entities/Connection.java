package entities;

import javax.persistence.*;

/**
 * Created by Niek on 14/03/14.
 * © Aidas 2014
 */
@Entity
public class Connection
{
    @Id
    @GeneratedValue
    private int id;
    // required
    @ManyToOne
    private Edge from;

    @ManyToOne
    private Edge to;

    @ManyToOne
    private Lane fromLane;

    @ManyToOne
    private Lane toLane;

    @Enumerated(EnumType.STRING)
    private ConnectionDirection direction;

    @Enumerated(EnumType.STRING)
    private ConnectionState state;

    // optional
    @ManyToOne
    private Lane via;

    //region Properties
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Edge getFrom()
    {
        return from;
    }

    public void setFrom(Edge from)
    {
        this.from = from;
    }

    public Edge getTo()
    {
        return to;
    }

    public void setTo(Edge to)
    {
        this.to = to;
    }

    public Lane getFromLane()
    {
        return fromLane;
    }

    public void setFromLane(Lane fromLane)
    {
        this.fromLane = fromLane;
    }

    public Lane getToLane()
    {
        return toLane;
    }

    public void setToLane(Lane toLane)
    {
        this.toLane = toLane;
    }

    public ConnectionDirection getDirection()
    {
        return direction;
    }

    public void setDirection(ConnectionDirection direction)
    {
        this.direction = direction;
    }

    public ConnectionState getState()
    {
        return state;
    }

    public void setState(ConnectionState state)
    {
        this.state = state;
    }

    public Lane getVia()
    {
        return via;
    }
    //endregion

    public Connection()
    {
    }

    public enum ConnectionDirection
    {
        STRAIGHT("s"), TURN("t"), LEFT("l"), RIGHT("r"), PARTIALLY_LEFT("L"), PARTIALLY_RIGHT("R"), INVALID("XX");

        private String text;

        ConnectionDirection(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return this.text;
        }

        public static ConnectionDirection fromString(String s)
        {
            if (s != null)
            {
                for (ConnectionDirection cd : ConnectionDirection.values())
                {
                    if (s.equals(cd.text))
                    {
                        return cd;
                    }
                }
            }
            return ConnectionDirection.INVALID;
        }
    }

    public enum ConnectionState
    {
        // TODO add conversion strings
        DEAD_END("-"), 
        EQUAL("="), 
        MINOR_LINK("m"), 
        MAJOR_LINK("M"), 
        CONTROLLER_OFF("O"), 
        YELLOW_FLASHING("o"), 
        YELLOW_MINOR_LINK("y"), 
        YELLOW_MAJOR_LINK("Y"), 
        RED("r"), 
        GREEN_MINOR("g"), 
        GREEN_MAJOR("G");
        
        /*
		 * <xsd:attribute name="state" use="required">
		 * <xsd:enumeration value="M"/>
		 * <xsd:enumeration value="m"/>
		 * <xsd:enumeration value="o"/>
		 * <xsd:enumeration value="="/>
		 * <xsd:enumeration value="-"/>
		 * <xsd:enumeration value="s"/>
		 * <xsd:enumeration value="w"/>
		 */

        private String text;

        ConnectionState(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return this.text;
        }

        public static ConnectionState fromString(String s)
        {
            if (s != null)
            {
                for (ConnectionState cs : ConnectionState.values())
                {
                    if (s.equals(cs.text))
                    {
                        return cs;
                    }
                }
            }
            return ConnectionState.DEAD_END;
        }
    }

    public Connection(Edge from, Edge to, Lane fromLane, Lane toLane, String direction, String state)
    {
        if (from == null || to == null || fromLane == null || toLane == null || direction == null || state == null)
            throw new IllegalArgumentException("Required connection attributes are empty");
        this.from = from;
        this.to = to;
        this.fromLane = fromLane;
        this.toLane = toLane;
        this.direction = ConnectionDirection.valueOf(direction);
        this.state = ConnectionState.valueOf(state);
    }

    public void setVia(Lane via)
    {
        this.via = via;
    }

}