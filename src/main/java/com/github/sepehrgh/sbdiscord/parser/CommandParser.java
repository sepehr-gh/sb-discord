package com.github.sepehrgh.sbdiscord.parser;

import com.github.sepehrgh.sbdiscord.annotations.DiscordParameter;
import com.github.sepehrgh.sbdiscord.exceptions.CommandParseException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.sepehrgh.sbdiscord.ReservedKeys.TO_REPLACE;

public class CommandParser {
    private final Method method;
    private final String DOUBLE_QUOTE_REGEX = "\"([^\"]*)\"";

    public CommandParser(Method method) {
        this.method = method;
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
                    if (hadAnAssignableParam){
                        throw new CommandParseException(
                            "Can't use none-assignable-param after using one"
                        );
                    }
                    hadAnAssignableParam = true;
                    String paramName = this.getAssignableParamName(input);
                    int indexOfParameter = this.getIndexOfParameter(paramName, methodParameters);
                    output[indexOfParameter] = input.replace(paramName + "=", "");
                }else {
                    output[i] = input;
                }
            }
        }

        return output;
    }

    private int getIndexOfParameter(String parameterName, @Nullable Parameter[] methodParameters) throws CommandParseException {
        if (methodParameters == null){
            methodParameters = this.method.getParameters();
        }

        for (int i = 0; i < methodParameters.length; i++) {
            if (methodParameters[i].getName().equals(parameterName) || methodParameters[i].getAnnotation(
                    DiscordParameter.class
            ).name().equals(parameterName))
                return i;
        }

        throw new CommandParseException(String.format("No parameter found with name '%s'", parameterName));
    }

    public String[] parse(String input) throws Exception {
        List<String> texts = new ArrayList<>();

        this.extractTextsIntoList(input, texts);

        String[] params = input.split(" ");
        this.fillTexts(params, texts);

        return this.parse(params);
    }

    private void extractTextsIntoList(String input, List<String> texts){
        if (input.contains("\"")){
            Pattern p = Pattern.compile(DOUBLE_QUOTE_REGEX);
            Matcher m = p.matcher(input);
            while (m.find()) {
                String txt = m.group(1);
                input = input.replace("\""+ txt +"\"", TO_REPLACE);
                texts.add(txt);
            }
        }
    }

    private void fillTexts(String[] params, List<String> texts){
        int z = 0;
        for (int i = 0; i < params.length; i++){
            if (params[i].equals(TO_REPLACE)){
                params[i] = texts.get(z);
                z++;
            }
        }
    }

    private String getAssignableParamName(String input){
        if (!input.contains("="))
            throw new IllegalStateException("Input is not an AssignableParam");
        return input.split("=")[0].trim();
    }

    private boolean isAssignableParam(String input) {
        if (!input.contains("="))
            return false;
        String[] split = input.split("=");
        for (Parameter parameter : this.method.getParameters()) {
            DiscordParameter discordParameter = parameter.getAnnotation(DiscordParameter.class);
            String name = null;
            if (discordParameter != null){
                name = discordParameter.name().equals("") ? null : discordParameter.name();
            }else {
                name = parameter.getName();
            }

            if (name != null && name.equals(split[0]))
                return true;
        }
        return false;
    }

}
