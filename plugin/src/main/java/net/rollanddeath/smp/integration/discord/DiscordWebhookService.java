package net.rollanddeath.smp.integration.discord;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class DiscordWebhookService {

    private static final String DEFAULT_AVATAR = "https://static.wikia.nocookie.net/minecraft_gamepedia/images/5/51/Missing_texture_block.png";

    private final RollAndDeathSMP plugin;
    private final TeamManager teamManager;
    private final HttpClient httpClient;
    private final URI webhookUri;
    private final boolean enabled;

    public DiscordWebhookService(RollAndDeathSMP plugin, TeamManager teamManager, String webhookUrl, boolean enabled) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.httpClient = HttpClient.newHttpClient();
        this.webhookUri = isBlank(webhookUrl) ? null : URI.create(webhookUrl.trim());
        this.enabled = enabled && this.webhookUri != null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void sendServerStatus(boolean online) {
        if (!enabled) {
            return;
        }
        String status = online ? "Servidor en línea" : "Servidor apagado";
        int color = online ? 0x43B581 : 0x992D22;
        String json = JsonBuilder.create()
                .username("Servidor")
                .avatar("https://mc-heads.net/avatar/Steve/128")
                .embedTitle(status)
                .embedDescription("Estado actualizado: " + status)
                .embedColor(color)
                .build();
        post(json);
    }

    public void sendChatMessage(Player player, Component message) {
        if (!enabled || player == null) {
            return;
        }
        Team team = teamManager.getTeam(player.getUniqueId());
        NamedTextColor teamColor = team != null ? team.getColor() : null;
        String teamTag = formatTeamTag(team);
        String content = PlainTextComponentSerializer.plainText().serialize(message);
        String username = player.getName();
        if (!teamTag.isEmpty()) {
            username = username + " " + teamTag;
        }
        String avatar = playerAvatar(player.getUniqueId());
        String json = JsonBuilder.create()
                .username(username)
                .avatar(avatar)
            .embedDescription(content)
                .embedColor(colorOf(teamColor))
                .timestamp(Instant.now())
                .build();
        post(json);
    }

    public void sendDeathMessage(Player victim, Component deathMessage) {
        if (!enabled || victim == null) {
            return;
        }
        Team team = teamManager.getTeam(victim.getUniqueId());
        NamedTextColor teamColor = team != null ? team.getColor() : null;
        String avatar = playerAvatar(victim.getUniqueId());
        String username = victim.getName();
        if (!formatTeamTag(team).isEmpty()) {
            username += " " + formatTeamTag(team);
        }
        String description = PlainTextComponentSerializer.plainText().serialize(deathMessage);
        String json = JsonBuilder.create()
                .username("Defunción")
                .avatar(avatar)
                .embedTitle(username)
            .embedDescription(description)
                .embedColor(colorOf(teamColor))
                .timestamp(Instant.now())
                .build();
        post(json);
    }

    public void sendEventAnnouncement(String title, String description, NamedTextColor typeColor) {
        if (!enabled) {
            return;
        }
        int color = colorOf(typeColor);
        String json = JsonBuilder.create()
                .username("Eventos")
                .avatar("https://mc-heads.net/avatar/Herobrine/128")
                .embedTitle(title)
            .embedDescription(description)
                .embedColor(color)
                .timestamp(Instant.now())
                .build();
        post(json);
    }

    public void sendAdminAnnouncement(CommandSender sender, String message) {
        if (!enabled) {
            return;
        }
        String name = sender.getName();
        if (sender instanceof Player player) {
            Team team = teamManager.getTeam(player.getUniqueId());
            String teamTag = formatTeamTag(team);
            if (!teamTag.isEmpty()) {
                name += " " + teamTag;
            }
        }
        String json = JsonBuilder.create()
            .username("Anuncio")
            .avatar(sender instanceof Player player ? playerAvatar(player.getUniqueId()) : DEFAULT_AVATAR)
            .embedTitle("Anuncio de " + name)
            .embedDescription(message)
                .embedColor(0xFAA61A)
                .timestamp(Instant.now())
                .build();
        post(json);
    }

    private void post(String json) {
        if (!enabled) {
            return;
        }
        HttpRequest request = HttpRequest.newBuilder(webhookUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        CompletableFuture<HttpResponse<Void>> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        future.whenComplete((response, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.WARNING, "No se pudo enviar mensaje a Discord", throwable);
            } else if (response.statusCode() >= 400) {
                plugin.getLogger().log(Level.WARNING, "Webhook de Discord respondió con estado " + response.statusCode());
            }
        });
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String playerAvatar(UUID uuid) {
        if (uuid == null) {
            return DEFAULT_AVATAR;
        }
        return "https://mc-heads.net/avatar/" + uuid.toString() + "/128";
    }

    private static String formatTeamTag(Team team) {
        if (team == null) {
            return "";
        }
        return "[" + team.getName() + "]";
    }

    private static int colorOf(NamedTextColor color) {
        if (color == null) {
            return 0x5865F2; // Discord blurple default
        }
        return color.value();
    }

    private static String escape(String raw) {
        if (raw == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(raw.length() + 32);
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    out.append('\\').append(c);
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        out.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    private static final class JsonBuilder {
        private final StringBuilder embeds = new StringBuilder();
        private String username;
        private String avatar;
        private String title;
        private String description;
        private Integer color;
        private Instant timestamp;

        private JsonBuilder() {
        }

        public static JsonBuilder create() {
            return new JsonBuilder();
        }

        public JsonBuilder username(String value) {
            this.username = value;
            return this;
        }

        public JsonBuilder avatar(String value) {
            this.avatar = value;
            return this;
        }

        public JsonBuilder embedTitle(String value) {
            this.title = value;
            return this;
        }

        public JsonBuilder embedDescription(String value) {
            this.description = value;
            return this;
        }

        public JsonBuilder embedColor(int value) {
            this.color = value;
            return this;
        }

        public JsonBuilder timestamp(Instant instant) {
            this.timestamp = instant;
            return this;
        }

        public String build() {
            StringBuilder json = new StringBuilder(256);
            json.append('{');
            if (username != null) {
                json.append("\"username\":\"").append(escape(username)).append('\"');
            }
            if (avatar != null) {
                appendComma(json);
                json.append("\"avatar_url\":\"").append(escape(avatar)).append('\"');
            }
            json.append(",\"embeds\":[{");
            boolean hasField = false;
            if (title != null) {
                json.append("\"title\":\"").append(escape(title)).append('\"');
                hasField = true;
            }
            if (description != null) {
                if (hasField) {
                    json.append(',');
                }
                json.append("\"description\":\"").append(escape(description)).append('\"');
                hasField = true;
            }
            if (color != null) {
                if (hasField) {
                    json.append(',');
                }
                json.append("\"color\":").append(color);
                hasField = true;
            }
            if (timestamp != null) {
                if (hasField) {
                    json.append(',');
                }
                json.append("\"timestamp\":\"").append(timestamp.toString()).append('\"');
            }
            json.append("}]");
            json.append('}');
            return json.toString();
        }

        private static void appendComma(StringBuilder json) {
            if (json.charAt(json.length() - 1) != '{' && json.charAt(json.length() - 1) != ',') {
                json.append(',');
            }
        }
    }
}
