/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bootstrap.jsf.component;

import com.sun.faces.renderkit.html_basic.HtmlBasicRenderer;
import java.io.IOException;
import java.util.*;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessages;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

/**
 *
 * @author duncan
 */
@FacesRenderer(componentFamily="javax.faces.Messages", rendererType="javax.faces.Messages")
public class MessagesRenderer extends HtmlBasicRenderer {
    
    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException{
        rendererParamsNotNull(facesContext, component);
        
        if (!shouldEncode(component)){
            return;
        }
        
        boolean mustRender = shouldWriteIdAttribute(component);
        
        UIMessages uiMessages = (UIMessages) component;
        ResponseWriter writer = facesContext.getResponseWriter();
        
        assert(writer != null);
        
        String clientId = uiMessages.getClientId(facesContext);
        Iterator<FacesMessage> allMessages = uiMessages.isGlobalOnly() ? facesContext.getMessages(null) : facesContext.getMessages();
        Map<String, List<FacesMessage>> messages = new HashMap<String, List<FacesMessage>>();
        messages.put("info", new ArrayList<FacesMessage>());  //Bootstrap info
        messages.put("warn", new ArrayList<FacesMessage>());  //Bootstrap warning
        messages.put("error", new ArrayList<FacesMessage>()); //Bootstrap Error
        messages.put("fatal", new ArrayList<FacesMessage>()); //Bootstrap Success
        
        while (allMessages.hasNext()){
            FacesMessage message = allMessages.next();
            Severity severity = message.getSeverity();
            if (message.isRendered() && !uiMessages.isRedisplay()){
                continue;
            }
            
            if(severity.equals(FacesMessage.SEVERITY_INFO)) messages.get("info").add(message);
            else if(severity.equals(FacesMessage.SEVERITY_WARN)) messages.get("warn").add(message);
            else if(severity.equals(FacesMessage.SEVERITY_ERROR)) messages.get("error").add(message);
            else if(severity.equals(FacesMessage.SEVERITY_FATAL)) messages.get("fatal").add(message);
        }
        
        writer.startElement("div", uiMessages);
        writer.writeAttribute("id", clientId, "id");

        for (String severity : messages.keySet()){
            List<FacesMessage> severityMessages = messages.get(severity);
            if (severityMessages.size() > 0){
                encodeSeverityMessages(facesContext, uiMessages, severity, severityMessages);
            }
        }
            
        writer.endElement("div");
    }

    private void encodeSeverityMessages(FacesContext facesContext, UIMessages uiMessages, String severity, List<FacesMessage> messages) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String styleClassPrefix = "";
        if (!"warn".equals(severity)){
            if ("fatal".equals(severity)){
                styleClassPrefix = "alert-success";
            } else {
                styleClassPrefix = "alert-"+severity;
            }
        }
        
        writer.startElement("div", null);
//        if (messages.size() > 1) styleClassPrefix = styleClassPrefix + " alert-block";
        
        writer.writeAttribute("class", "alert alert-block "+styleClassPrefix, null);
        
        writer.startElement("a", null);
        writer.writeAttribute("class", "close", null);
        writer.writeAttribute("data-dismiss","alert", null);
        writer.writeAttribute("href", "#", null);
        writer.write("&times;");
        writer.endElement("a");
        
        writer.startElement("ul", null);
        for (FacesMessage msg : messages){
            String summary = msg.getSummary() != null ? msg.getSummary() : "";
            String detail = msg.getDetail() != null ? msg.getDetail() : summary;
            
            writer.startElement("li", null);

            if (uiMessages.isShowSummary()){
                writer.startElement("strong", null);
                writer.writeText(summary, null);
                writer.endElement("strong");
            }
            
            if (uiMessages.isShowDetail()){
                writer.writeText(" "+detail, null);
            }
            
            writer.endElement("li");
            msg.rendered();
        }
        writer.endElement("ul");
        writer.endElement("div");
    }
    
    
}
