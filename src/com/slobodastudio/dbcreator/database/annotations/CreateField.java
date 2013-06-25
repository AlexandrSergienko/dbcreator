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
 * Anotation is used for describe class field for creating filed in DB.
 *
 * @author Alexandr Sergienko
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreateField {

    /**
     * Set field name, default name is class field name
     *
     * @return table name
     */
    String FieldName() default CreatorOpenHelper.NO_VALUE;

    /**
     * Word separator is like class name is mainTree converted word is main_tree if split character is "_"
     *
     * @return Separator
     */
    String SplitWordsCharacter() default CreatorOpenHelper.NO_VALUE;

    /**
     * Add string PRIMARY KEY if true, empty string otherwise.
     */
    boolean PrimaryKey() default false;

    /**
     * Add string FOREIGN KEY if true, empty string otherwise.
     */
    String ForeignKey() default CreatorOpenHelper.NO_VALUE;

    /**
     * Add string NOT NULL if true, empty string otherwise.
     */
    boolean NotNull() default false;

    /**
     * Add string AUTOINCREMENT if true, empty string otherwise.
     */
    boolean Autoincrement() default false;

    /**
     * Additional string for creating field in table with default value.
     *
     * @return Default value
     */
    String Default() default CreatorOpenHelper.NO_VALUE;

    /**
     * Indicate that field was added in some versions.
     * <br/> The string have to be in depend format: versionNumber:
     *
     * @return 1
     */
    int AddingVersion() default 1;

    /**
     * Additional string for creating field in table, For example foreign key
     *
     * @return Additional string
     */
    String AdditionalSqlString() default "";
}
