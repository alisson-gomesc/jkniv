package net.sf.jkniv.whinstone.cassandra.model;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MxtData
{
    private static final Logger LOG                  = LoggerFactory.getLogger(MxtData.class);
    private static final XPath  XPATH                = XPathFactory.newInstance().newXPath();
    
    private static final String ATTR_GENERATION_DATE = "generation_date";
    private static final String ATTR_IPV4            = "ipv4";
    
    private static final String PATH_FIRMWARE        = "/POSITION/FIRMWARE";
    private static final String PATH_GPS             = "/POSITION/GPS";
    private static final String PATH_FLAGSTATE       = "/POSITION/GPS/FLAG_STATE";
    private static final String PATH_HARDWAREMONITOR = "/POSITION/HARDWARE_MONITOR";
    
    private String              customerId;
    private String              objectId;
    private String              evtDate;                                                      // POSITIONS/POSITION/@generation_date
    private Date                evtUtcDt;                                                     // POSITIONS/POSITION/@generation_date
    private String              ipv4;                                                         // POSITIONS/POSITION/@ipv4
    private String              serialNumber;                                                 // POSITIONS/POSITION/FIRMWARE/SERIAL
    private Short               protocol;                                                     // POSITIONS/POSITION/FIRMWARE/PROTOCOL
    private Short               transmissionReason;                                           // POSITIONS/POSITION/FIRMWARE/TRANSMISSION_REASON
    private Float               lat;                                                          // POSITIONS/POSITION/GPS/LATITUDE
    private Float               lng;                                                          // POSITIONS/POSITION/GPS/LONGITUDE
    private Float               alt;                                                          // POSITIONS/POSITION/GPS/ALTITUDE
    private Short               svn;                                                          // POSITIONS/POSITION/GPS/SVN
    private Float               speed;                                                        // POSITIONS/POSITION/GPS/SPEED
    private Short               direction;                                                    // POSITIONS/POSITION/GPS/COURSE
    private Integer             hodometer;                                                    // POSITIONS/POSITION/GPS/HODOMETER
    private Short               gprsConn;                                                     // POSITIONS/POSITION/GPS/FLAG_STATE/GPRS_CONNECTION
    private Short               gpsSignal;                                                    // POSITIONS/POSITION/GPS/FLAG_STATE/GPS_SIGNAL
    private Short               gpsFailure;                                                   // POSITIONS/POSITION/GPS/FLAG_STATE/GPS_ANTENNA_FAILURE
    private Short               gpsDisconn;                                                   // POSITIONS/POSITION/GPS/FLAG_STATE/GPS_ANTENNA_DISCONNECTED
    private Short               gpsSleep;                                                     // POSITIONS/POSITION/GPS/FLAG_STATE/GPS_SLEEP
    private Short               excessSpeed;                                                  // POSITIONS/POSITION/GPS/FLAG_STATE/EXCESS_SPEED
    private Short               gsmJamming;                                                   // POSITIONS/POSITION/GPS/FLAG_STATE/GSM_JAMMING
    private Short               panic;                                                        // POSITIONS/POSITION/HARDWARE_MONITOR/INPUTS/PANIC
    private Short               ignition;                                                     // POSITIONS/POSITION/HARDWARE_MONITOR/INPUTS/IGNITION
    private Short               acessoryCount;                                                // POSITIONS/POSITION/HARDWARE_MONITOR/ACCESSORY_COUNT
    private Integer             hourmeter;                                                    // POSITIONS/POSITION/HARDWARE_MONITOR/HOURMETER
    private Float               powerSupply;                                                  // POSITIONS/POSITION/HARDWARE_MONITOR/POWER_SUPPLY
    private Float               temperature;                                                  // POSITIONS/POSITION/HARDWARE_MONITOR/TEMPERATURE
    private Float               batteryUsed;                                                  // POSITIONS/POSITION/HARDWARE_MONITOR/BATTERY_USED
    private Short               antiTheftStatus;                                              // POSITIONS/POSITION/HARDWARE_MONITOR/FLAG_STATE/ANTI_THEFT_STATUS
    private Document            doc;
    
    public MxtData()
    {
    }
    
    public MxtData(String positionNode)
    {
        this.customerId = "9999";
        this.objectId = "OMN7176";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        // docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
        // "http://www.w3.org/2001/XMLSchema");
        // docBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
        // "sqlegance-0.1.xsd");
        
        DocumentBuilder docBuilder = null;
        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(positionNode));
            doc = docBuilder.parse(is);
            doc.normalize();
            processMainAttributes();
            processFirmwareData();
            processGpsData();
            processFlagStateData();
            processHardwareMonitorData();
        }
        catch (ParserConfigurationException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException("Error in parser the xml file [" + positionNode
                    + "]. ParserConfigurationException: " + e.getMessage()
                    + ". Verify if the name from file start with '/' and contains the package, because the path is absolute");
        }
        catch (SAXException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException(
                    "Error in parser the xml file [" + positionNode + "]. SAXException: " + e.getMessage());
        }
        catch (IOException e)
        {
            // FIXME exception design create ParserException
            throw new RuntimeException(
                    "Error in parser the xml file [" + positionNode + "]. IOException: " + e.getMessage());
        }
        catch (ParseException e)
        {
            // TODO Auto-generated catch block
            throw new RuntimeException(
                    "Error in parser date [" + positionNode + "]. ParseException: " + e.getMessage());
        }
    }
    
    /**
     * /POSITIONS/POSITION/FIRMWARE/SERIAL
     * /POSITIONS/POSITION/FIRMWARE/PROTOCOL
     * /POSITIONS/POSITION/FIRMWARE/TRANSMISSION_REASON
     */
    private void processFirmwareData()
    {
        Element nodeSerial = (Element) evaluateXpath(PATH_FIRMWARE + "/SERIAL");
        Element nodeProtocol = (Element) evaluateXpath(PATH_FIRMWARE + "/PROTOCOL");
        Element nodeReason = (Element) evaluateXpath(PATH_FIRMWARE + "/TRANSMISSION_REASON");
        this.serialNumber = nodeSerial.getTextContent();
        this.protocol = valueOfShort(nodeProtocol.getTextContent());
        if (nodeReason != null)
            this.transmissionReason = valueOfShort(nodeReason.getTextContent());
    }
    
    private void processGpsData()
    {
        Element nodeLat = (Element) evaluateXpath(PATH_GPS + "/LATITUDE");
        Element nodeLng = (Element) evaluateXpath(PATH_GPS + "/LONGITUDE");
        Element nodeAlt = (Element) evaluateXpath(PATH_GPS + "/ALTITUDE");
        Element nodeSvn = (Element) evaluateXpath(PATH_GPS + "/SVN");
        Element nodeSpeed = (Element) evaluateXpath(PATH_GPS + "/SPEED");
        Element nodeDirection = (Element) evaluateXpath(PATH_GPS + "/COURSE");
        Element nodeHodometer = (Element) evaluateXpath(PATH_GPS + "/HODOMETER");
        
        if (nodeLat != null)
            this.lat = valueOfFloat(nodeLat.getTextContent());
        if (nodeLng != null)
            this.lng = valueOfFloat(nodeLng.getTextContent());
        if (nodeAlt != null)
            this.alt = valueOfFloat(nodeAlt.getTextContent());
        if (nodeSvn != null)
            this.svn = valueOfShort(nodeSvn.getTextContent());
        if (nodeSpeed != null)
            this.speed = valueOfFloat(nodeSpeed.getTextContent());
        if (nodeDirection != null)
            this.direction = valueOfShort(nodeDirection.getTextContent());
        if (nodeHodometer != null)
            this.hodometer = valueOfInt(nodeHodometer.getTextContent());
    }
    
    private void processFlagStateData()
    {
        Element nodeGprsConn = (Element) evaluateXpath(PATH_FLAGSTATE + "/GPRS_CONNECTION");
        Element nodeGpsSignal = (Element) evaluateXpath(PATH_FLAGSTATE + "/GPS_SIGNAL");
        Element nodeGpsAntennaFail = (Element) evaluateXpath(PATH_FLAGSTATE + "/GPS_ANTENNA_FAILURE");
        Element nodeGpsAntennaDisco = (Element) evaluateXpath(PATH_FLAGSTATE + "/GPS_ANTENNA_DISCONNECTED");
        Element nodeGpsSleep = (Element) evaluateXpath(PATH_FLAGSTATE + "/GPS_SLEEP");
        Element nodeExcessSpeed = (Element) evaluateXpath(PATH_FLAGSTATE + "/EXCESS_SPEED");
        Element nodeGsmJamming = (Element) evaluateXpath(PATH_FLAGSTATE + "/GSM_JAMMING");
        
        if (nodeGprsConn != null)
            this.gprsConn = valueOfShort(nodeGprsConn.getTextContent());
        if (nodeGpsSignal != null)
            this.gpsSignal = valueOfShort(nodeGpsSignal.getTextContent());
        if (nodeGpsAntennaFail != null)
            this.gpsFailure = valueOfShort(nodeGpsAntennaFail.getTextContent());
        if (nodeGpsAntennaDisco != null)
            this.gpsDisconn = valueOfShort(nodeGpsAntennaDisco.getTextContent());
        if (nodeGpsSleep != null)
            this.gpsSleep = valueOfShort(nodeGpsSleep.getTextContent());
        if (nodeExcessSpeed != null)
            this.excessSpeed = valueOfShort(nodeExcessSpeed.getTextContent());
        if (nodeGsmJamming != null)
            this.gsmJamming = valueOfShort(nodeGsmJamming.getTextContent());
    }
    
    private void processHardwareMonitorData()
    {
        Element nodePanic = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/INPUTS/PANIC");
        Element nodeIgnition = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/INPUTS/IGNITION");
        Element nodeAccessoryCount = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/ACCESSORY_COUNT");
        Element nodeHourmeter = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/HOURMETER");
        Element nodePowerSupply = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/POWER_SUPPLY");
        Element nodeTemperature = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/TEMPERATURE");
        Element nodeBattery = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/BATTERY_USED");
        Element nodeTheft = (Element) evaluateXpath(PATH_HARDWAREMONITOR + "/FLAG_STATE/ANTI_THEFT_STATUS");
        
        if (nodePanic != null)
            this.panic = valueOfShort(nodePanic.getTextContent());
        if (nodeIgnition != null)
            this.ignition = valueOfShort(nodeIgnition.getTextContent());
        if (nodeAccessoryCount != null)
            this.acessoryCount = valueOfShort(nodeAccessoryCount.getTextContent());
        if (nodeHourmeter != null)
            this.hourmeter = valueOfInt(nodeHourmeter.getTextContent());
        if (nodePowerSupply != null)
            this.powerSupply = valueOfFloat(nodePowerSupply.getTextContent());
        if (nodeTemperature != null)
            this.temperature = valueOfFloat(nodeTemperature.getTextContent());
        if (nodeBattery != null)
            this.batteryUsed = valueOfFloat(nodeBattery.getTextContent());
        if (nodeTheft != null)
            this.antiTheftStatus = valueOfShort(nodeTheft.getTextContent());
    }
    
    private void processMainAttributes() throws ParseException
    {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        Node firstNode = doc.getFirstChild();
        if (firstNode.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element) firstNode;
            this.evtUtcDt = valueOfDate(element.getAttribute(ATTR_GENERATION_DATE));
            this.evtDate = sdf2.format(evtUtcDt);
            this.ipv4 = element.getAttribute(ATTR_IPV4);
        }
    }
    
    /**
     * Evaluate the compiled XPath expression in the specified context and
     * return the result as the specified type.
     * 
     * @param expressionXpath
     * @param element
     * @return
     */
    private Node evaluateXpath(String expressionXpath)
    {
        Node node = null;
        try
        {
            XPathExpression exp = XPATH.compile(expressionXpath);
            node = (Node) exp.evaluate(doc, XPathConstants.NODE);
        }
        catch (XPathExpressionException e)
        {
            LOG.info("XPath is wrong: " + expressionXpath);
        }
        return node;
    }
    
    private Integer valueOfInt(String s)
    {
        Integer v = null;
        if (s != null)
        {
            try
            {
                v = Integer.valueOf(s);
            }
            catch (NumberFormatException ignore)
            {
            }
        }
        return v;
    }
    
    private Short valueOfShort(String s)
    {
        Short v = null;
        if (s != null)
        {
            try
            {
                v = Short.valueOf(s);
            }
            catch (NumberFormatException ignore)
            {
            }
        }
        return v;
    }
    
    private Float valueOfFloat(String s)
    {
        Float v = null;
        if (s != null)
        {
            try
            {
                v = Float.valueOf(s);
            }
            catch (NumberFormatException ignore)
            {
            }
        }
        return v;
    }
    
    private Date valueOfDate(String s)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date v = null;
        if (s != null)
        {
            try
            {
                v = sdf.parse(s);
            }
            catch (ParseException ignore)
            {
            }
        }
        return v;
    }
    
    public String getCustomerId()
    {
        return customerId;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public String getEvtDate()
    {
        return evtDate;
    }
    
    public Date getEvtUtcDt()
    {
        return evtUtcDt;
    }
    
    public String getIpv4()
    {
        return ipv4;
    }
    
    public String getSerialNumber()
    {
        return serialNumber;
    }
    
    public Short getProtocol()
    {
        return protocol;
    }
    
    public Short getTransmissionReason()
    {
        return transmissionReason;
    }
    
    public Float getLat()
    {
        return lat;
    }
    
    public Float getLng()
    {
        return lng;
    }
    
    public Float getAlt()
    {
        return alt;
    }
    
    public Short getSvn()
    {
        return svn;
    }
    
    public Float getSpeed()
    {
        return speed;
    }
    
    public Short getDirection()
    {
        return direction;
    }
    
    public Integer getHodometer()
    {
        return hodometer;
    }
    
    public Short getGprsConn()
    {
        return gprsConn;
    }
    
    public Short getGpsSignal()
    {
        return gpsSignal;
    }
    
    public Short getGpsFailure()
    {
        return gpsFailure;
    }
    
    public Short getGpsDisconn()
    {
        return gpsDisconn;
    }
    
    public Short getGpsSleep()
    {
        return gpsSleep;
    }
    
    public Short getExcessSpeed()
    {
        return excessSpeed;
    }
    
    public Short getGsmJamming()
    {
        return gsmJamming;
    }
    
    public Short getPanic()
    {
        return panic;
    }
    
    public Short getIgnition()
    {
        return ignition;
    }
    
    public Short getAcessoryCount()
    {
        return acessoryCount;
    }
    
    public Integer getHourmeter()
    {
        return hourmeter;
    }
    
    public Float getPowerSupply()
    {
        return powerSupply;
    }
    
    public Float getTemperature()
    {
        return temperature;
    }
    
    public Float getBatteryUsed()
    {
        return batteryUsed;
    }
    
    public Short getAntiTheftStatus()
    {
        return antiTheftStatus;
    }
    
    @Override
    public String toString()
    {
        return "MxtData [customerId=" + customerId + ", objectId=" + objectId + ", evtUtcDt=" + evtUtcDt + ", ipv4="
                + ipv4 + ", serialNumber=" + serialNumber + ", protocol=" + protocol + ", transmissionReason="
                + transmissionReason + ", lat=" + lat + ", lng=" + lng + ", svn=" + svn + ", speed=" + speed
                + ", direction=" + direction + ", hodometer=" + hodometer + ", panic=" + panic + ", ignition="
                + ignition + ", hourmeter=" + hourmeter + "]";
    }
    
}
