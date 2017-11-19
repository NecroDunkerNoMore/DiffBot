package org.ndnm.diffbot.util;

import java.util.List;

import javax.annotation.Resource;

import org.ndnm.diffbot.model.HtmlChangedEvent;
import org.springframework.stereotype.Component;


@Component
public class RedditPostFormatter {
    private static final String FOOTER = "^[FAQ](https://np.reddit.com/r/NecroDunkerNoMore/wiki/index)&nbsp;| ^[Source&nbsp;Code](https://github.com/NecroDunkerNoMore/DiffBot)&nbsp;| ^[PM&nbsp;Developer](https://www.reddit.com/message/compose?to=NecroDunkerNoMore&subject=NecroDunkerNoMore)&nbsp;| ^v%s";
    private static final String LINK_LINE_SUCCESS = "1. [Before](%s) --> [Now](%s) (%s)";
    private static final String LINE = "-----";

    @Resource(name = "diffBotVersion")
    private String diffBotVersion;


    public String format(List<HtmlChangedEvent> htmlChangedEvents) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Diffed:**");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator()); // reddit markdown needs 2 newlines to display one

        for(HtmlChangedEvent event : htmlChangedEvents) {

            sb.append(String.format(LINK_LINE_SUCCESS, event.getDiffUrl(), event.getDiffUrl(), TimeUtils.formatGmt(event.getDateCaptured())));


            sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }

        sb.append(LINE);
        sb.append(System.lineSeparator());
        sb.append(String.format(FOOTER, getDiffBotVersion()));

        return sb.toString();
    }


    private String getDiffBotVersion() {
        return diffBotVersion;
    }
}
