package pl.bkkuc.atomicitems.tools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorUtility {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>(.*?)</#([0-9A-Fa-f]{6})>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");

    private static final Map<String, ChatColor> COLOR_MAP = new ImmutableMap.Builder<String, ChatColor>()
            .put("&l", ChatColor.BOLD)
            .put("&o", ChatColor.ITALIC)
            .put("&n", ChatColor.UNDERLINE)
            .put("&m", ChatColor.STRIKETHROUGH)
            .put("&k", ChatColor.MAGIC)
            .put("§l", ChatColor.BOLD)
            .put("§o", ChatColor.ITALIC)
            .put("§n", ChatColor.UNDERLINE)
            .put("§m", ChatColor.STRIKETHROUGH)
            .put("§k", ChatColor.MAGIC)
            .build();

    public static String genHex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer stringBuffer = new StringBuffer(text.length()+4*8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(stringBuffer, ChatColor.COLOR_CHAR + "x"
                    + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                    + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                    + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5));
        }

        return matcher.appendTail(stringBuffer).toString();
    }

    public static String genGradient(String text, Color color1, Color color2) {
        String result = text;
        List<ChatColor> modifiers = Lists.newArrayList();
        for (Map.Entry<String, ChatColor> entry : COLOR_MAP.entrySet()) {
            if (result.contains(entry.getKey()))
                modifiers.add(entry.getValue());
            result = result.replace(entry.getKey(), "");
        }

        ChatColor[] colors = getGradientColors(color1, color2, result.length());
        char[] chars = result.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            stringBuilder.append(colors[i]);
            modifiers.forEach(stringBuilder::append);
            stringBuilder.append(chars[i]);
        }

        return stringBuilder.toString();
    }

    private static ChatColor[] getGradientColors(Color color1, Color color2, int step) {
        ChatColor[] colors = new ChatColor[step];
        int stepR = Math.abs(color1.getRed() - color2.getRed()) / (step-1);
        int stepG = Math.abs(color1.getGreen() - color2.getGreen()) / (step-1);
        int stepB = Math.abs(color1.getBlue() - color2.getBlue()) / (step-1);

        int[] direction = new int[] {
                color1.getRed() < color2.getRed() ? +1 : -1,
                color1.getGreen() < color2.getGreen() ? +1 : -1,
                color1.getBlue() < color2.getBlue() ? +1 : -1
        };
        for (int i = 0; i < step; i++) {
            Color color = new Color(color1.getRed() + ((stepR * i) * direction[0]), color1.getGreen() + ((stepG * i) * direction[1]), color1.getBlue() + ((stepB * i) * direction[2]));
            colors[i] = ChatColor.of(color);
        }

        return colors;
    }

    public static String color(String text) {
        String result = text;
        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(result);
        while (gradientMatcher.find()) {
            String start = gradientMatcher.group(1);
            String end = gradientMatcher.group(3);
            String content = gradientMatcher.group(2);
            result = result.replace(gradientMatcher.group(), genGradient(content, new Color(Integer.parseInt(start, 16)), new Color(Integer.parseInt(end, 16))));
        }

        return ChatColor.translateAlternateColorCodes('&', genHex(result));
    }

    public static List<String> color(List<String> list) {
        return list
                .stream()
                .map(ColorUtility::color)
                .collect(Collectors.toList());
    }
}