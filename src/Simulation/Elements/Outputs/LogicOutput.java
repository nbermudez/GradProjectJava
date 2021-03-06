/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Outputs;

import Exceptions.ArchException;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import java.awt.Font;
import java.awt.Graphics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class LogicOutput extends BaseElement {

    public boolean isOpen;
    
    public LogicOutput(int x, int y) {
        super(x, y);
        this.isOpen = false;
    }

    public LogicOutput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        this.isOpen = Boolean.parseBoolean(extraParams[0]);
        if (isOpen)
            voltages[0] = Configuration.LOGIC_0_VOLTAGE;
        else
            voltages[0] = Configuration.LOGIC_1_VOLTAGE;
    }

    public LogicOutput(int x, int y, int x2, int y2, int flags) {
        super(x, y, x2, y2, flags);
        this.isOpen = false;
    }

    @Override
    public void setPoints() {
        super.setPoints();
        lead1 = interpolatePoint(point1, point2, 1 - 12 / dn);
    }

    @Override
    public int getPostCount() {
        return 1;
    }

    @Override
    public void draw(Graphics g) {
        Font f = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(f);
        g.setColor(needsHighlight() ? selectedColor : defaultColor);
        String s = (voltages[0] < Configuration.LOGIC_1_VOLTAGE) ? "L" : "H";
        if (Configuration.LOGIC_VALUES_AS_NUMBER) {
            s = (voltages[0] < Configuration.LOGIC_1_VOLTAGE) ? "0" : "1";
        }
        //value = s;
        setBbox(point1, lead1, 0);
        drawCenteredText(g, s, x2, y2, true);
        setVoltageColor(g, voltages[0]);
        drawThickLine(g, point1, lead1);
        drawPosts(g);
    }

    @Override
    public void doStep() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", LogicOutput.class.getName());
        
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(Boolean.toString(isOpen));
        
        element.appendChild(extraParam0);
        
        return element;
    }
}
