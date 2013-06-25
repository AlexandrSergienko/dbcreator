/*
 * Copyright (C) 2011 Alexey Danilov
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.slobodastudio.dbcreator.database.annotations;

import com.slobodastudio.dbcreator.database.CreatorOpenHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotation is used for describe class table for creating table in DB.
 *
 * @author Alexandr Sergienko
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateTable {

    /**
     * Field set table name, default table name is class name
     *
     * @return table name
     */
    String TableName() default CreatorOpenHelper.NO_VALUE;

    /**
     * Word separator is like class name is mainTree converted word is main_tree if split character is "_"
     *
     * @return Separator
     */
    String SplitWordsCharacter() default CreatorOpenHelper.NO_VALUE;

    /**
     * Additional string for creating table, For example foreign key
     *
     * @return Additional string
     */
    String AdditionalSqlString() default "";

    /**
     * Field provide info that table was updated
     *
     * @return table name
     */
    boolean Update() default false;

}
