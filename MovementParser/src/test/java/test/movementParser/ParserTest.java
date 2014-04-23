package test.movementParser;
import static org.junit.Assert.*;

import java.io.File;

import javax.inject.Inject;

import movementParser.MovementParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import dao.ConnectionDAO;
import dao.EdgeDAO;
import dao.LaneDAO;




public class ParserTest
{
	MovementParser parser;
	
	
	/**
	 * Inject DAO's from MovementEntityAccess
	 */
	@Inject
	private EdgeDAO edgeDAO;
	
	@Inject
	private LaneDAO laneDAO;
	
	@Inject
	private ConnectionDAO connectionDAO;
	
	@Before
	public void setup() {
		parser = new MovementParser();
	}
	
	@Test
	public void testParseSumo() {
		// Parse SUMO XML file and create entities in database
		parser.parseSUMO(new File("res/PTS-ESD-2.net.xml"));
		/**
		 * Check if edges, lanes and connections persist to the database 
		 * by counting the elements.
		 */
		assertTrue(edgeDAO.count() == 867);
		assertTrue(laneDAO.count() == 3707);
		assertTrue(connectionDAO.count() == 4290);
		
		
	}
	
	
}