package toti.templating.parameters;

import toti.templating.Parameter;

public class PlaceholderParameter implements Parameter {

     @Override
     public String getName() {
         return "placeholder";
     }

     @Override
     public String getCode(String value) {
         return String.format("Template.escapeHtml(translator.translate(\"%s\"))", value);
     }

}