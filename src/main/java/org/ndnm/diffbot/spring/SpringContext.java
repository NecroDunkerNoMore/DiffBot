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

package org.ndnm.diffbot.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public final class SpringContext {
    private static final ApplicationContext context = new AnnotationConfigApplicationContext(DiffBotConfiguration.class);

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

}
