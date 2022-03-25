package io.github.sepgh.sbdiscord.parser;

import io.github.sepgh.sbdiscord.annotations.DiscordParameter;
import io.github.sepgh.sbdiscord.exceptions.CommandParseException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.sepgh.sbdiscord.ReservedKeys.TO_REPLACE;

public class CommandParser {
    private final Method method;
    private final String DOUBLE_QUOTE_REGEX = "\"([^\"]*)\"";

    public CommandParser(Method method) {
        this.method = method;
    }

    /**
     * Parser for slash commands
     * @param options options sent by slash command
     * @return object array to call the method with
     * @throws CommandParseException when parameter is missing or is required
     */
    public Object[] parse(List<OptionMapping> options) throws CommandParseException {
        Parameter[] methodParameters = this.method.getParameters();
        Object[] output = new Object[methodParameters.length];

        for(int i = 0; i < methodParameters.length; i++){
            DiscordParameter discordParameter = methodParameters[i].getAnnotation(DiscordParameter.class);

            // Find any option mapping that matches the name of current parameter in the loop
            for (OptionMapping optionMapping: options){
                if (optionMapping.getName().equals(discordParameter.name())){
                    output[i] = this.getOptionValue(optionMapping, methodParameters[i]);
                    break;
                }
            }

            if ((output[i] == null || options.size() < i + 1) && discordParameter.required()){
                throw new CommandParseException(
                        String.format(
                                "Parameter is missing: %s",
                                discordParameter.name()
                        )
                );
            }
        }

        return output;
    }

    public String[] parse(String[] inputs) throws CommandParseException {
        Parameter[] methodParameters = this.method.getParameters();
        String[] output = new String[methodParameters.length];

        boolean hadAnAssignableParam = false;
        for (int i = 0; i < methodParameters.length; i++){
            if (inputs.length < i + 1){
                DiscordParameter discordParameter = methodParameters[i].getAnnotation(DiscordParameter.class);
                if (discordParameter.required()) {
                    throw new CommandParseException(
                            String.format(
                                "Parameter is missing: %s",
                                discordParameter.name()
                            )
                    );
                }else {
                    output[i] = discordParameter.value();
                }
            }else {
                String input = inputs[i];
                if (this.isAssignableParam(input)){
                    hadAnAssignableParam = true;
                    String paramName = this.getAssignableParamName(input);
                    int indexOfParameter = this.getIndexOfParameter(paramName, methodParameters);
                    output[indexOfParameter] = input.replace(paramName + "=", "");
                }else {
                    if (hadAnAssignableParam){
                        throw new CommandParseException(
                                "Can't use none-assignable-param after using one"
                        );
                    }
                    output[i] = input;
                }
            }
        }

        return output;
    }


    public String[] parse(String input) throws CommandParseException {
        List<String> texts = new ArrayList<>();

        input = this.extractTextsIntoList(input, texts);

        String[] params = input.split(" ");
        params = this.parse(params);
        this.fillTexts(params, texts);

        return params;
    }

    private Object getOptionValue(OptionMapping optionMapping, Parameter parameter) {
        switch (optionMapping.getType()) {
            case BOOLEAN:
                return optionMapping.getAsBoolean();
            case ROLE:
                return optionMapping.getAsRole();
            case CHANNEL:
                return optionMapping.getAsGuildChannel();
            case INTEGER:
            case NUMBER:
                if (parameter.getType().equals(Integer.class)) {
                    return new Long(optionMapping.getAsLong()).intValue();
                }else if (parameter.getType().equals(Double.class)){
                    return optionMapping.getAsDouble();
                }
                return optionMapping.getAsLong();
            case USER:
                return optionMapping.getAsMember();
            case STRING:
                return optionMapping.getAsString();
            case MENTIONABLE:
                return optionMapping.getAsMentionable();
            default:
                return null;
        }
    }

    private int getIndexOfParameter(String parameterName, Parameter[] methodParameters) throws CommandParseException {
        for (int i = 0; i < methodParameters.length; i++) {
            if (methodParameters[i].getName().equals(parameterName) || methodParameters[i].getAnnotation(
                    DiscordParameter.class
            ).name().equals(parameterName))
                return i;
        }

        throw new CommandParseException(String.format("Strange! No parameter found with name '%s'", parameterName));
    }

    private String extractTextsIntoList(String input, List<String> texts){
        if (input.contains("\"")){
            Pattern p = Pattern.compile(DOUBLE_QUOTE_REGEX);
            Matcher m = p.matcher(input);
            while (m.find()) {
                String txt = m.group(1);
                input = input.replace("\""+ txt +"\"", TO_REPLACE);
                texts.add(txt);
            }
        }
        return input;
    }

    private void fillTexts(String[] params, List<String> texts){
        int z = 0;
        for (int i = 0; i < params.length; i++){
            if (params[i].contains(TO_REPLACE)){
                params[i] = params[i].replace(TO_REPLACE, texts.get(z));
                z++;
            }
        }
    }

    private String getAssignableParamName(String input){
        return input.split("=")[0].trim();
    }

    private boolean isAssignableParam(String input) {
        if (!input.contains("="))
            return false;
        String[] split = input.split("=");
        for (Parameter parameter : this.method.getParameters()) {
            DiscordParameter discordParameter = parameter.getAnnotation(DiscordParameter.class);
            String name = null;
            if (discordParameter != null && !discordParameter.name().equals("")){
                name = discordParameter.name();
            }else {
                name = parameter.getName();
            }

            if (name != null && name.equals(split[0]))
                return true;
        }
        return false;
    }

}
