/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2017  github.com/NecroDunkerNoMore
 */

package org.ndnm.diffbot.service;


import java.util.List;

import org.ndnm.diffbot.model.RedditUser;
import org.ndnm.diffbot.model.diff.DiffResult;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;

public interface RedditService extends HealthCheckableService {
    boolean performAuth();

    boolean isAuthenticated();

    String postDiffResult(DiffResult diffResult);

    Listing<Message> getUnreadMessages();

    void markMessageRead(Message message);

    void replyToMessage(RedditUser redditUser, boolean isSubscribed);

    int notifySubscribersOfPosts(List<String> postUrls);

    boolean processMail();

    int getMaxAuthAttempts();
}
