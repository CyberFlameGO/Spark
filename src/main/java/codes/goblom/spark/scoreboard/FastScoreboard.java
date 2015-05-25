/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.scoreboard;

import codes.goblom.spark.internals.monitor.Monitor;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.internals.policy.LoadPolicy;
import codes.goblom.spark.internals.policy.Policies;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 *
 * @author Goblom
 */
public class FastScoreboard extends Monitor {

//    public static void main(String[] args) {
//        LoadPolicy policy = Policies.newLoadPolicy(FastScoreboard.class);
//                   policy.constructor(String.class);
//                   policy.values("Test Scoreboard");
//                   
//        FastScoreboard fs = Monitors.loadNoStore(FastScoreboard.class, policy);
//                       fs.send(Bukkit.getPlayer("Goblom"));
//    }
    
    @Monitor.TickInterval
    private static long UPDATE_INTERVAL = 10L;
    
    private static final Map<String, OfflinePlayer> cache = Maps.newHashMap();

    private final Scoreboard scoreboard;
    private String title;
    private final Map<String, Integer> scores;
    private Objective main;
    private final List<Team> teams;
    private final List<Integer> removed;
    private Set<String> updated;

    public FastScoreboard(String title) {
        checkNotNull(title);
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.scores = Maps.newConcurrentMap();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(Sets.newHashSet());
    }

    @Deprecated //Untested
    public FastScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        this.main = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        
        checkNotNull(main);
        
        this.removed = Lists.newArrayList();
        this.updated = Collections.synchronizedSet(Sets.newHashSet());
        this.title = this.main.getDisplayName();
        this.scores = Maps.newConcurrentMap();
        this.teams = Collections.synchronizedList(Lists.newArrayList());

        for (String entry : scoreboard.getEntries()) {
            Set<Score> scores = scoreboard.getScores(entry);
            
            for (Score score : scores) {
                this.scores.put(score.getEntry(), score.getScore());
            }
        }
        
        this.scoreboard.getTeams().stream().forEach((team) -> { teams.add(team); });
    }
    
    public void add(String text, int score) {
        text = ChatColor.translateAlternateColorCodes('&', text);

        if (remove(score, text, false) || !scores.containsValue(score)) {
            updated.add(text);
        }

        scores.put(text, score);
    }

    public boolean remove(int score, String text) {
        return remove(score, text, true);
    }

    public boolean remove(int score, String n, boolean b) {
        String toRemove = get(score, n);

        if (toRemove == null) {
            return false;
        }

        scores.remove(toRemove);

        if (b) {
            removed.add(score);
        }

        return true;
    }

    public String get(int score, String n) {
        String str = null;

        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue().equals(score)
                    && !entry.getKey().equals(n)) {
                str = entry.getKey();
            }
        }

        return str;
    }

    private Map.Entry<Team, OfflinePlayer> createTeam(String text, int pos) {
        Team team;
        ChatColor color = ChatColor.values()[pos];
        OfflinePlayer result;

        if (!cache.containsKey(color.toString())) {
            cache.put(color.toString(), Bukkit.getOfflinePlayer(color.toString()));
        }

        result = cache.get(color.toString());

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        applyText(team, text, result);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, result);
    }

    private void applyText(Team team, String text, OfflinePlayer result) {
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();

        team.setPrefix(iterator.next());

        if (!team.hasPlayer(result)) {
            team.addPlayer(result);
        }

        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(team.getPrefix());
            String suffix = iterator.next();

            if (prefixColor == null) {
                prefixColor = "";
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
            }

            team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
        }
    }

    public void update() {
        if (updated.isEmpty()) {
            return;
        }

        if (main == null) {
            main = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy");
            main.setDisplayName(title);
            main.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        removed.stream().forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = main.getScore(s);

                if (score == null) {
                    continue;
                }

                if (score.getScore() != remove) {
                    continue;
                }

                scoreboard.resetScores(s);
            }
        });

        removed.clear();

        int index = scores.size();

        for (Map.Entry<String, Integer> text : scores.entrySet()) {
            Team t = scoreboard.getTeam(ChatColor.values()[text.getValue()].toString());
            Map.Entry<Team, OfflinePlayer> team;

            if (!updated.contains(text.getKey())) {
                continue;
            }

            if (t != null) {
                team = new AbstractMap.SimpleEntry<>(t,
                        Bukkit.getOfflinePlayer(ChatColor.values()[text.getValue()].toString()));

                applyText(team.getKey(), text.getKey(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(text.getKey(), text.getValue());
            }

            Integer score = text.getValue() != null ? text.getValue() : index;

            main.getScore(team.getValue()).setScore(score);
            index -= 1;
        }

        updated.clear();
    }

    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if (main != null) {
            main.setDisplayName(title);
        }
    }

    public void reset() {
        for (Team t : teams) {
            t.unregister();
        }
        teams.clear();
        scores.clear();
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void send(Player... players) {
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }
    
    public void send(Collection<? extends Player> players) {
        players.stream().forEach((player) -> {
            player.setScoreboard(scoreboard);
        });
    }
    
    public void sendAll() {
        send(Bukkit.getOnlinePlayers());
    }
}
