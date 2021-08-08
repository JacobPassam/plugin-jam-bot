package com.github.jacobpassam.pluginjam.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JamEmbed {

    private static final Color DEFAULT_COLOR = new Color(251, 123, 50);
    private static final String DEFAULT_FOOTER = "Plugin Jam #2 | Plugin Dev Course";

    private String title;
    private String content;
    private Color color = DEFAULT_COLOR;

    private String footer = DEFAULT_FOOTER;
    private String footerIconUrl;

    private final List<Field> fields = new ArrayList<>();

    private boolean useThumbnail = true;
    private boolean useBigLogo = false;

    public JamEmbed withTitle(String title) {
        this.title = title;
        return this;
    }

    public JamEmbed withContent(String content) {
        this.content = content;
        return this;
    }

    public JamEmbed withColorOverride(Color color) {
        this.color = color;
        return this;
    }

    public JamEmbed withFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public JamEmbed withFooter(String footer, String iconUrl) {
        this.footer = footer;
        this.footerIconUrl = iconUrl;
        return this;
    }

    public JamEmbed withoutThumbnail() {
        this.useThumbnail = false;
        return this;
    }

    public JamEmbed addField(Field field) {
        fields.add(field);
        return this;
    }

    public JamEmbed withBigLogo() {
        this.useBigLogo = true;
        return this;
    }

    public MessageEmbed build() {
        EmbedBuilder builder = new EmbedBuilder();
        if (title != null) builder.setTitle(title);

        builder.setDescription(content);
        builder.setColor(color);

        if (useThumbnail) builder.setThumbnail("https://i.imgur.com/OVXUxlf.png");
        if (useBigLogo) builder.setImage("https://i.imgur.com/OVXUxlf.png");

        if (footer != null && footerIconUrl != null) builder.setFooter(footer, footerIconUrl);
        else if (footer != null) builder.setFooter(footer);

        for (Field field : fields) {
            builder.addField(field.getTitle(), field.getContent(), field.isInline());
        }

        return builder.build();
    }

    public MessageAction send(MessageChannel channel) {
        return channel.sendMessageEmbeds(build());
    }

}
