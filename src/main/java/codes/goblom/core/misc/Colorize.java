/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public class Colorize {

    private static final Random RANDOM = new Random();

    private static final char[] COLORS = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] SYTLES = {'l', 'n', 'o', 'k', 'm'}; //do not use r
    private static final char[] ALL_COLORS = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'n', 'o', 'k', 'm'}; //do not use r

    public static String getRandomColorCode(boolean withExtra) {
        char[] toRandomize = (withExtra ? ALL_COLORS : COLORS);

        return "&" + String.valueOf(toRandomize[RANDOM.nextInt(toRandomize.length)]);
    }

    public static Stylize style(String toStyle) {
        return new Stylize(toStyle);
    }

    public static class Stylize {

        private final String toStyle;

        protected Stylize(String toStyle) {
            this.toStyle = toStyle;
        }

        public String toRainbow() {
            StringBuilder sb = new StringBuilder();

            for (char c : toStyle.toCharArray()) {
                sb.append(Colorize.getRandomColorCode(false) + String.valueOf(c));
            }

            return ChatColor.translateAlternateColorCodes('&', sb.toString());
        }

        public String toGarbage() {
            List<Character> characters = Lists.newArrayList();

            for (char c : toStyle.toCharArray()) {
                characters.add(c);
            }

            StringBuilder sb = new StringBuilder(toStyle.length());
            while (!characters.isEmpty()) {
                int randPicker = (int) (Math.random() * characters.size());
                sb.append(characters.remove(randPicker));
            }

            return sb.toString();
        }

        public String toStripe(ChatColor one, ChatColor two) {
            StringBuilder sb = new StringBuilder();
            boolean colA = true;
            for (char c : toStyle.toCharArray()) {
                if (colA) {
                    sb.append(one);
                } else {
                    sb.append(two);
                }
                
                sb.append(c);
                colA = !colA;
            }
            return sb.toString();
        }
    }
}
