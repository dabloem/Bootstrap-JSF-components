/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bootstrap.jsf.component;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.html_basic.HtmlBasicInputRenderer;
import java.io.IOException;
import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author duncan
 */
public class InputTextRenderer extends HtmlBasicInputRenderer {
    
    private static final Attribute[] INPUT_ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.INPUTTEXT);
    private static final Attribute[] OUTPUT_ATTRIBUTES = AttributeManager.getAttributes(AttributeManager.Key.OUTPUTTEXT);
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        rendererParamsNotNull(context, component);
    }
    
    @Override
    protected void getEndTextToRender(FacesContext context, UIComponent component, String currentValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        assert(writer != null);
        
        boolean shouldWriteIdAttribute = false;
        boolean isOutput = false;
        
        Iterator<FacesMessage> messages = context.getMessages(component.getClientId());
        writer.startElement("div", component);
        
        if (messages.hasNext()){
            Severity severity = messages.next().getSeverity();
            String stringSeverity = "";
            if (severity.equals(FacesMessage.SEVERITY_ERROR)) stringSeverity = "error";
            else if (severity.equals(FacesMessage.SEVERITY_FATAL)) stringSeverity = "success";
            else if (severity.equals(FacesMessage.SEVERITY_WARN)) stringSeverity = "warning";
            
            writer.writeAttribute("class", "control-group " + stringSeverity, "class");            
        } else {
            writer.writeAttribute("class", "control-group", "class");
        }
        
        writer.startElement("label", component);
        writer.writeAttribute("for", component.getClientId(), "for");
        writer.writeAttribute("class", "control-label", "class");
        writer.writeText(component.getAttributes().get("label"), "value");
        writer.endElement("label");
        

        String style = (String) component.getAttributes().get("style");
        String styleClass = (String) component.getAttributes().get("styleClass");
        String dir = (String) component.getAttributes().get("dir");
        String lang = (String) component.getAttributes().get("lang");
        String title = (String) component.getAttributes().get("title");
        
        if (component instanceof UIInput) {
            writer.startElement("div", component);
            writer.writeAttribute("class", "controls", "class");
            
            writer.startElement("input", component);
            writeIdAttributeIfNecessary(context, writer, component);
            writer.writeAttribute("type", "text", null);
            writer.writeAttribute("name", (component.getClientId(context)), "clientId");

            // only output the autocomplete attribute if the value
            // is 'off' since its lack of presence will be interpreted
            // as 'on' by the browser
            if ("off".equals(component.getAttributes().get("autocomplete"))) {
                writer.writeAttribute("autocomplete", "off", "autocomplete");
            }

            // render default text specified
            if (currentValue != null) {
                writer.writeAttribute("value", currentValue, "value");
            }
            if (null != styleClass) {
                writer.writeAttribute("class", styleClass, "styleClass");
            }

            // style is rendered as a passthur attribute
            RenderKitUtils.renderPassThruAttributes(context, writer, component, INPUT_ATTRIBUTES, getNonOnChangeBehaviors(component));
            RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);

            RenderKitUtils.renderOnchange(context, component, false);


            writer.endElement("input");
            
            rendererParamsNotNull(context, component);
            if (!shouldEncodeChildren(component)) {
                return;
            }
            if (component.getChildCount() > 0) {
                for (UIComponent kid : component.getChildren()) {
                    encodeRecursive(context, kid);
                }
            }
//            writer.startElement("span", component);
//            writer.writeAttribute("class", "help-inline", "class");
//            writer.writeText("help", "helpp");
//            writer.endElement("span");
            
            
            writer.endElement("div");
            writer.endElement("div");

        } else if (isOutput = (component instanceof UIOutput)) {
            if (styleClass != null
                 || style != null
                 || dir != null
                 || lang != null
                 || title != null
                 || (shouldWriteIdAttribute = shouldWriteIdAttribute(component))) {
                writer.startElement("span", component);
                writeIdAttributeIfNecessary(context, writer, component);
                if (null != styleClass) {
                    writer.writeAttribute("class", styleClass, "styleClass");
                }
                // style is rendered as a passthru attribute
                RenderKitUtils.renderPassThruAttributes(context, writer, component, OUTPUT_ATTRIBUTES);
            }
            if (currentValue != null) {
                Object val = component.getAttributes().get("escape");
                if ((val != null) && Boolean.valueOf(val.toString())) {
                    writer.writeText(currentValue, component, "value");
                } else {
                    writer.write(currentValue);
                }
            }
        }
        if (isOutput && (styleClass != null
                 || style != null
                 || dir != null
                 || lang != null
                 || title != null
                 || (shouldWriteIdAttribute))) {
            writer.endElement("span");
        }
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

        boolean renderChildren = WebConfiguration.getInstance()
                .isOptionEnabled(WebConfiguration.BooleanWebContextInitParameter.AllowTextChildren);

        if (!renderChildren) {
            return;
        }

        rendererParamsNotNull(context, component);

        if (!shouldEncodeChildren(component)) {
            return;
        }

        if (component.getChildCount() > 0) {
            for (UIComponent kid : component.getChildren()) {
                encodeRecursive(context, kid);
            }
        }
    }    
    
}
