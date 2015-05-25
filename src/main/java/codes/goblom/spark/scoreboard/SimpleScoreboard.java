/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.scoreboard;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public class SimpleScoreboard {
    private static final char COLOR_CHAR = '§';
    
    @Getter
    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    
    private final String title;
    private Map<String, Integer> scores = Maps.newLinkedHashMap();
    private List<Team> teams = Lists.newArrayList();
    
    public SimpleScoreboard blankLine() {
        return add(" ");
    }
    
    public SimpleScoreboard add(String line) {
        return add(line, null);
    }
    
    public SimpleScoreboard add(String line, Integer score) {
        checkArgument(line.length() < 48, "Text cannot be over 48 characters in length");
        line = fixDuplicates(line);
        scores.put(line, score);
        
        return this;
    }

    private String fixDuplicates(String line) {
        while (scores.containsKey(line)) {
            line += COLOR_CHAR + 'r';
        }
        
        if (line.length() > 48) {
            line = line.substring(0, 47);
        }
        
        return ChatColor.translateAlternateColorCodes('&', line);
    }
    
    private Map.Entry<Team, String> createTeam(String name) {
        String result = "";
        if (name.length() <= 16) {
            return new AbstractMap.SimpleEntry<Team, String>(null, name);
        }

        Team team = scoreboard.registerNewTeam("text-" + scoreboard.getTeams().size());
        Iterator<String> iterator = Splitter.fixedLength(16).split(name).iterator();
        team.setPrefix(iterator.next());
        result = iterator.next();
        
        if (name.length() > 32) {
            team.setSuffix(iterator.next());
        }
        
        teams.add(team);
        
        return new AbstractMap.SimpleEntry<>(team, result);
    }
    
    public void build() {
        Objective obj = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
        obj.setDisplayName(title);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Map.Entry<Team, String> team = createTeam(text.getKey());
            Integer score = text.getValue() != null ? text.getValue() : index;
            OfflinePlayer player = Bukkit.getOfflinePlayer(team.getValue());
            if (team.getKey() != null) {
                team.getKey().addPlayer(player);
            }
            obj.getScore(player).setScore(score);
            index -= 1;
        }
    }

    public void reset() {
        scores.clear();
        for (Team t : teams) {
            t.unregister();
        }
        teams.clear();
    }

    public void send(Player... players) {
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }
}
