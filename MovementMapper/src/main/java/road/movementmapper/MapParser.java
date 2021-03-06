package road.movementmapper;

import road.movemententities.entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBElement;
import osm.jaxb.NodeType;
import osm.jaxb.OsmType;
import osm.jaxb.TagType;
import road.movementmapper.dao.EntityDAO;
import road.movementmapper.helpers.CoordinateConversion;
import sumo.jaxb.ConnectionType;
import sumo.jaxb.EdgeType;
import sumo.jaxb.LaneType;
import sumo.jaxb.NetType;

/**
 * This part of the parser should be used only when loading the initial map.
 *
 * @author tijs
 */
public class MapParser
{
    @Inject
    private EntityDAO entityDAO;
    @PersistenceContext(unitName = "MovementPU")
    private EntityManager em;

    /**
     * Parse the SUMO version of the net map.
     * Simply extract the useful information from the map and store it.
     * @param root The SUMO XML root element.
     */
    public void parseMap(JAXBElement<NetType> root)
    {
        if (root == null) 
        {
            throw new IllegalArgumentException();
        }

        /**
         * Persist edges, convert XMLtypes to JPA entities
         */
        List<Lane> lanes = new ArrayList();
        List<Edge> edges = new ArrayList();
        for (EdgeType xmlEdge : root.getValue().getEdge())
        {
            int prio = xmlEdge.getPriority() != null ? xmlEdge.getPriority().intValue() : 0;

            /**
             * If an edge is internal, it has no from and to.
             * Otherwise use the city if it exists or create one.
             */
            City from = null;
            City to = null;

            if (!"internal".equals(xmlEdge.getFunction()))
            {
                from = em.find(City.class, xmlEdge.getFrom());
                if(from == null)
                {
                    from = new City(xmlEdge.getFrom());
                }
                to = em.find(City.class, xmlEdge.getTo());
                if(to == null)
                {
                    to = new City(xmlEdge.getTo());
                }
            }

            Edge edge = new Edge(xmlEdge.getId(), xmlEdge.getFunction(), xmlEdge.getType(), from, to, prio);
            entityDAO.create(edge);

            /**
             * Add all drive lanes to their edge.
             */
            for (LaneType xmlLane : xmlEdge.getLane())
            {
                Lane lane = new Lane(edge, xmlLane.getId(), xmlLane.getIndex().intValue(), xmlLane.getSpeed(), xmlLane.getLength());

                //Parse coordinates from the shape attribute value.
                List<ShapeCoordinate> shape = new ArrayList<>();
                int sequence = 0;
                for(String rawCoor : xmlLane.getShape().split(" "))
                {
                    /* this converts the utm coordinates, converted by NetConvert, back to the original
                     * WGS84 latitude longitude coordinates. 31 is the zone and N is the hemisphere (northern)
                     * keep this in mind when converting from other files. */
                    rawCoor = "31,N," + rawCoor;
                    double[] coorNums = new CoordinateConversion().utm2LatLon(rawCoor);
                    ShapeCoordinate coor = new ShapeCoordinate(lane, sequence, coorNums[0], coorNums[1]);
                    shape.add(coor);
                    this.em.persist(coor);
                    sequence++;
                }

                lane.setShape(shape);
                edge.addLane(lane);
                entityDAO.create(lane);
                lanes.add(lane);
            }
            edge = (Edge) entityDAO.edit(edge);
            edges.add(edge);
        }

        Map<String, Edge> edgesMap = new HashMap<>();
        for(Edge edge : edges)
        {
            edgesMap.put(edge.getId(), edge);
        }

        Map<String, Lane> lanesMap = new HashMap<>();
        for(Lane lane : lanes)
        {
            lanesMap.put(lane.getLaneIdentifier(), lane);
        }

        System.out.println("About to flush");
        em.flush();

        /**
         * Lookup edges and lanes by their names.
         */
        for (ConnectionType xmlConn : root.getValue().getConnection())
        {
            String from_str = xmlConn.getFrom();
            String to_str = xmlConn.getTo();
            Integer fromLane_int = xmlConn.getFromLane().intValue();
            Integer toLane_int = xmlConn.getToLane().intValue();
            String via_str = xmlConn.getVia();
            String direction_str = xmlConn.getDir();
            String state_str = xmlConn.getState();

            Edge from = null, to = null;
            Lane fromLane = null, toLane = null, via = null;

            from = edgesMap.get(from_str);
            to = edgesMap.get(to_str);

            for(Lane lane : from.getLanes())
            {
                if(lane.getIndex() == fromLane_int)
                {
                    fromLane = lane;
                    break;
                }
            }

            for(Lane lane : to.getLanes())
            {
                if(lane.getIndex() == toLane_int)
                {
                    toLane = lane;
                    break;
                }
            }

            if(via_str != null)
            {
                via = lanesMap.get(via_str);
            }

            /**
             * Add links(FK's) in edge from and edge to.
             */
            if (from == null || to == null)
            {
                throw new IllegalArgumentException("SUMO syntax error: connection from or to cannot be found, from="+from_str+", to="+to_str);
            }

            Connection connection = new Connection(from, to, fromLane, toLane, direction_str, state_str);
            connection.setVia(via);
            from.addConnection(connection);
            to.addConnection(connection);
            fromLane.addLaneTo(toLane);
            toLane.addLaneFrom(fromLane);

            em.persist(connection);
            em.merge(from);
            em.merge(to);
        }// end for connections
    }
    
    /**
     * Enrich map data of lanes and edges with city names.
     *
     * @param root XML Open Street map root element
     */
    public void parseCities(JAXBElement<OsmType> root)
    {
        if (root == null)
        {
            throw new IllegalArgumentException();
        }

        // Node has tag <tag k='addr:city' v='Asten' />
        // In SUMO some edges are like this: <edge id="--90" from="-46" to="-44" priority="7" type="highway.secondary">
        // Parse all cities from OSM Map<node.id, node.tag[k='addr:city].v>
        for (NodeType node : root.getValue().getNode())
        {
            String id = node.getId();
            for (TagType tag : node.getTag())
            {
                if ("addr:city".equals(tag.getK()) || "name".equals(tag.getK()))
                {
                    City city = em.find(City.class, node.getId());
                    if(city == null)
                    {
                        city = new City(node.getId(), tag.getV());
                        em.persist(city);
                    }
                    else
                    {
                        city.setCityName(tag.getV());
                        em.merge(city);
                    }
                }
            }
        }
    }
}
