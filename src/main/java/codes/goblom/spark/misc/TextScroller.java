package codes.goblom.spark.misc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.ChatColor;

/**
 * A util to scroll coloured Strings
 *
 * @author Chinwe
 */
public class TextScroller {

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private String message = null;
        private int width = Integer.MIN_VALUE, spaceBetween = 3;
        
        private Builder() { }
        
        public Builder withMessage(String message) {
            this.message = message;
            
            return this;
        }
        
        public Builder withWidth(int width) {
            this.width = width;
            
            return this;
        }
        
        public Builder withSpaceBetween(int spaceBetween) {
            this.spaceBetween = spaceBetween;
            
            return this;
        }
        
        public TextScroller build() {
            checkNotNull(message, "A TextScroller message cannot be null");
            checkArgument(width <= 0, "A TextScroller width cannot be 0");
            
            return new TextScroller(message, width, spaceBetween);
        }
    }
    
    private static final char COLOUR_CHAR = '§';
    
    private int position;
    private List<String> list = Lists.newArrayList();
    private ChatColor colour = ChatColor.RESET;

    /**
     * @param message The String to scroll
     * @param width The width of the window to scroll across (i.e. 16 for signs)
     * @param spaceBetween The amount of spaces between each repetition
     */
    private TextScroller(String message, int width, int spaceBetween) {
        // Validation
        // String is too short for window
        if (message.length() < width) {
            StringBuilder sb = new StringBuilder(message);
            while (sb.length() < width) {
                sb.append(" ");
            }
            message = sb.toString();
        }

        // Allow for colours which add 2 to the width
        width -= 2;

        // Invalid width/space size
        if (width < 1) {
            width = 1;
        }
        if (spaceBetween < 0) {
            spaceBetween = 0;
        }

        // Change to §
        message = ChatColor.translateAlternateColorCodes('&', message);

        // Add substrings
        for (int i = 0; i < message.length() - width; i++) {
            list.add(message.substring(i, i + width));
        }

        // Add space between repeats
        StringBuilder space = new StringBuilder();
        for (int i = 0; i < spaceBetween; ++i) {
            list.add(message.substring(message.length() - width + (i > width ? width : i), message.length()) + space);
            if (space.length() < width) {
                space.append(" ");
            }
        }

        // Wrap
        for (int i = 0; i < width - spaceBetween; ++i) {
            list.add(message.substring(message.length() - width + spaceBetween + i, message.length()) + space + message.substring(0, i));
        }

        // Join up
        for (int i = 0; i < spaceBetween; i++) {
            if (i > space.length()) {
                break;
            }
            list.add(space.substring(0, space.length() - i) + message.substring(0, width - (spaceBetween > width ? width : spaceBetween) + i));
        }
    }

    /**
     * @return Gets the next String to display
     */
    public String next() {
        StringBuilder sb = getNext();
        if (sb.charAt(sb.length() - 1) == COLOUR_CHAR) {
            sb.setCharAt(sb.length() - 1, ' ');
        }

        if (sb.charAt(0) == COLOUR_CHAR) {
            ChatColor c = ChatColor.getByChar(sb.charAt(1));
            if (c != null) {
                colour = c;
                sb = getNext();
                if (sb.charAt(0) != ' ') {
                    sb.setCharAt(0, ' ');
                }
            }
        }

        return colour + sb.toString();

    }

    private StringBuilder getNext() {
        return new StringBuilder(list.get(position++ % list.size()).substring(0));
    }

}
