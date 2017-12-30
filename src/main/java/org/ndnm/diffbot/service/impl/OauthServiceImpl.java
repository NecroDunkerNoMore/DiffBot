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
 * DiffBot - Diff webpages and post to reddit when a difference is found.
 * Copyright (C) 2017  github.com/NecroDunkerNoMore
 */

package org.ndnm.diffbot.service.impl;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ndnm.diffbot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;

@Component
public class OauthServiceImpl implements AuthService {
    private static final Logger LOG = LogManager.getLogger(OauthServiceImpl.class);

    @Resource(name = "maxAuthAttempts")
    private int maxAuthAttempts;
    private final Credentials credentials;


    @Autowired
    public OauthServiceImpl(Credentials credentials) {
        this.credentials = credentials;
    }


    @Override
    public boolean authenticate(RedditClient redditClient) {
        try {
            OAuthData oAuthData = redditClient.getOAuthHelper().easyAuth(getCredentials());
            redditClient.authenticate(oAuthData);
        } catch (Exception e) {
            LOG.error("Could not authenticate: %s", e.getMessage());
            return false;
        }

        return isAuthenticated(redditClient);
    }


    @Override
    public boolean isAuthenticated(RedditClient redditClient) {
        return redditClient.isAuthenticated();
    }


    private Credentials getCredentials() {
        return credentials;
    }


    @Override
    public int getMaxAuthAttempts() {
        return maxAuthAttempts;
    }

}
