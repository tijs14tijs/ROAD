//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.03.28 at 12:44:22 PM CET 
//

package sumo.movements.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for vehicleType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="vehicleType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pos" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="speed" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vehicleType", propOrder = { "value" })
public class VehicleType
{

	@XmlValue
	protected String value;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "pos")
	protected Float pos;
	@XmlAttribute(name = "speed")
	protected Float speed;

	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value)
	{
		this.id = value;
	}

	/**
	 * Gets the value of the pos property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getPos()
	{
		return pos;
	}

	/**
	 * Sets the value of the pos property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setPos(Float value)
	{
		this.pos = value;
	}

	/**
	 * Gets the value of the speed property.
	 * 
	 * @return possible object is {@link Float }
	 * 
	 */
	public Float getSpeed()
	{
		return speed;
	}

	/**
	 * Sets the value of the speed property.
	 * 
	 * @param value
	 *            allowed object is {@link Float }
	 * 
	 */
	public void setSpeed(Float value)
	{
		this.speed = value;
	}

}
