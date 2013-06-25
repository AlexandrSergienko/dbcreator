package com.slobodastudio.dbcreator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.slobodastudio.dbcreator.database.annotations.CreateField;
import com.slobodastudio.dbcreator.database.annotations.CreateTable;

import java.lang.reflect.Field;

/**
 * Created by alex on 17.06.13.
 */
public abstract class CreatorOpenHelper extends SQLiteOpenHelper {

    public CreatorOpenHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
    }


    public static final String NO_VALUE = "#%NO VALUE%#";
    private static final String TAG = CreatorOpenHelper.class.getSimpleName();


    public void createTable(SQLiteDatabase db, Class cls) {
        db.execSQL(getCreateTableScript(cls));
    }

    /**
     * The function convert read class and find annotation and fields for make string, that can execute in sql
     * database.
     *
     * @param cls - class with annotation CreateTable.
     * @return String if annotation was found or null otherwise.
     */
    public static String getCreateTableScript(Class cls) {

        CreateTable tableAnn = (CreateTable) cls.getAnnotation(CreateTable.class);
        if (tableAnn != null) {
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE ");
            String tableName = "";
            if (!tableAnn.TableName().equals(NO_VALUE)) {
                tableName = tableAnn.TableName();
                sql.append(tableName);
            } else {
                tableName = cls.getSimpleName();
                if (tableAnn.SplitWordsCharacter().equals(NO_VALUE)) {
                    sql.append(tableName.toLowerCase());
                } else {
                    sql.append(devideWords(tableName, tableAnn.SplitWordsCharacter()).toLowerCase());
                }
            }
            Field fields[] = cls.getDeclaredFields();
            sql.append(" (");
            sql.append(createFieldsScript(fields));
            if (getPrimaryKeysQuantity(fields) > 1) {
                sql.append(", ");
                sql.append(createPrimaryKeysScript(fields));
            }
            StringBuilder foreignKeys = createForeignKeysScript(fields);
            if (foreignKeys.length() > 0) {
                sql.append(", ");
                sql.append(foreignKeys);
            }
            sql.append(")");
            return sql.toString();
        } else {
            Log.e(TAG + ":createTable(Class cls)", "Error. The class doesn't contain createTable annotation!");
        }
        return null;
    }

    public static String createConstants(Class cls) {

        CreateTable tableAnn = (CreateTable) cls.getAnnotation(CreateTable.class);
        if (tableAnn != null) {
            StringBuilder constants = new StringBuilder();
            String tableName = "";
            if (!tableAnn.TableName().equals(NO_VALUE)) {
                tableName = tableAnn.TableName();
            } else {
                tableName = cls.getSimpleName();
                if (!tableAnn.SplitWordsCharacter().equals(NO_VALUE)) {
                    tableName = devideWords(tableName, tableAnn.SplitWordsCharacter()).toLowerCase();
                }
            }
            constants.append("public static final String ");
            constants.append("TABLE_NAME = \"");
            constants.append(tableName);
            constants.append("\";\n");
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(CreateField.class)) {
                    constants.append("public static final String ");
                    constants.append(getTableFieldName(field).toString().toUpperCase());
                    constants.append(" = \"");
                    constants.append(getTableFieldName(field).toString());
                    constants.append("\";\n");
                }
            }
            return constants.toString();
        } else {
            Log.e(TAG + ":createCursorParser(Class cls)",
                    "Error. The class isn't contain createTable annotation!");
        }
        return null;
    }

    /**
     * @param field
     * @return string type or null if unsupported type.
     */
    private static String getFieldType(Field field) {

        if (field.getType() == int.class || field.getType() == Integer.class) {
            return "Int";
        } else if (field.getType() == long.class || field.getType() == Long.class) {
            return "Long";
        } else if (field.getType() == float.class || field.getType() == Float.class) {
            return "Float";
        } else if (field.getType() == double.class || field.getType() == Double.class) {
            return "Double";
        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            return "Boolean";
        } else if (field.getType() == String.class) {
            return "String";
        } else {
            return null;
        }
    }

    private static StringBuilder putValidData(Field field, String obj) {

        StringBuilder putStr = new StringBuilder();
        putStr.append("if(this.");
        putStr.append(field.getName());
        putStr.append(" != ");
        if (field.getType() == int.class || field.getType() == long.class) {
            putStr.append("0){\n");
        } else if (field.getType() == float.class || field.getType() == double.class) {
            putStr.append("0){\n");
        } else if (field.getType() == boolean.class) {
            putStr.append("false){\n");
        } else {
            putStr.append("null){\n");
        }
        putStr.append(obj);
        putStr.append(".put(");
        putStr.append(field.getName().toUpperCase());
        putStr.append(",");
        putStr.append("this.");
        putStr.append(field.getName());
        putStr.append(");\n}\n");
        return putStr;
    }

    public static StringBuilder getTableFieldName(Field field) {

        CreateField tableField = field.getAnnotation(CreateField.class);
        StringBuilder sql = new StringBuilder();
        if (tableField != null) {
            String fieldName = "";
            if (tableField.FieldName().equals(NO_VALUE)) {
                if (tableField.SplitWordsCharacter().equals(NO_VALUE)) {
                    fieldName = field.getName();
                } else {
                    fieldName = devideWords(field.getName(), tableField.SplitWordsCharacter()).toLowerCase();
                }
            } else {
                fieldName = tableField.FieldName();
                if (tableField.SplitWordsCharacter().equals(NO_VALUE)) {
                    fieldName = fieldName.toLowerCase();
                } else {
                    fieldName = devideWords(fieldName, tableField.SplitWordsCharacter()).toLowerCase();
                }
            }
            sql.append(fieldName);
        }
        return sql;
    }

    private static String devideWords(String words, String separator) {

        String[] r = words.split("(?=\\p{Lu})");
        StringBuilder ds = new StringBuilder();
        boolean first = false;
        for (String s : r) {
            if (first) {
                ds.append(separator);
            } else {
                first = true;
            }
            ds.append(s.toLowerCase());
        }
        return ds.toString();
    }

    private static String getSQLiteType(Field field) {

        if (field.getType() == int.class || field.getType() == long.class || field.getType() == Integer.class
                || field.getType() == Long.class || field.getType() == boolean.class || field.getType() == Boolean.class) {
            return "INTEGER";
        } else if (field.getType() == float.class || field.getType() == double.class
                || field.getType() == Float.class || field.getType() == Double.class) {
            return "REAL";
        } else {
            return "TEXT";
        }
    }

    private static StringBuilder createFieldsScript(Field fields[]) {

        StringBuilder sql = new StringBuilder();
        boolean first = true;
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                if (first) {
                    first = false;
                } else {
                    sql.append(", ");
                }
                CreateField tableField = field.getAnnotation(CreateField.class);
                sql.append(getTableFieldName(field));
                sql.append(" ");
                sql.append(getSQLiteType(field));
                if (tableField.NotNull()) {
                    sql.append(" NOT NULL");
                }
                if (tableField.PrimaryKey() && getPrimaryKeysQuantity(fields) == 1) {
                    sql.append(" PRIMARY KEY");
                    if (tableField.Autoincrement()) {
                        sql.append(" AUTOINCREMENT");
                    }
                }
                if (!tableField.Default().equals(NO_VALUE)) {
                    sql.append(" DEFAULT ");
                    sql.append(tableField.Default());
                }
            }
        }
        return sql;
    }

    private static int getPrimaryKeysQuantity(Field fields[]) {

        int quantity = 0;
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                CreateField tableField = field.getAnnotation(CreateField.class);
                if (tableField.PrimaryKey()) {
                    quantity++;
                }
            }
        }
        return quantity;
    }

    private static StringBuilder createPrimaryKeysScript(Field fields[]) {

        StringBuilder sql = new StringBuilder();
        boolean first = true;
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                CreateField tableField = field.getAnnotation(CreateField.class);
                if (tableField.PrimaryKey()) {
                    if (first) {
                        first = false;
                        sql.append("PRIMARY KEY (");
                    } else {
                        sql.append(", ");
                    }
                    sql.append(getTableFieldName(field));
                }
            }
        }
        if (sql.length() == 0) {
            throw new SQLiteException("The table must has at least one primary key field.");
        }
        sql.append(") ");
        return sql;
    }

    private static StringBuilder createForeignKeysScript(Field fields[]) {

        StringBuilder sql = new StringBuilder();
        boolean first = true;
        String tables = "";
        for (Field field : fields) {
            if (field.isAnnotationPresent(CreateField.class)) {
                CreateField tableField = field.getAnnotation(CreateField.class);
                if (!tableField.ForeignKey().equals(NO_VALUE) && !tables.contains(tableField.
                        ForeignKey().substring(0, tableField.ForeignKey().indexOf("(")) + ";")) {
                    if (first) {
                        first = false;
                    } else {
                        sql.append(", ");
                    }
                    boolean otherFirst = true;
                    sql.append("FOREIGN KEY (");
                    sql.append(getTableFieldName(field));
                    StringBuilder references = new StringBuilder();
                    String reference = tableField.ForeignKey();
                    String table = reference.substring(0, reference.indexOf("("));
                    tables += table + ";";
                    for (Field otherField : fields) {
                        if (otherField.isAnnotationPresent(CreateField.class)) {
                            CreateField otherTableField = otherField.getAnnotation(CreateField.class);
                            String otherReference = otherTableField.ForeignKey();
                            if (field != otherField && !otherTableField.ForeignKey().equals(NO_VALUE)
                                    && table.equals(otherReference.substring(0, otherReference.
                                    indexOf("(")))) {
                                sql.append(", ");
                                sql.append(getTableFieldName(otherField));
                                references.append(", ");
                                references.append(otherReference.substring(otherReference.
                                        indexOf("(") + 1, otherReference.indexOf(")")));
                            }
                        }
                    }
                    sql.append(") REFERENCES ");
                    sql.append(reference.substring(0, reference.indexOf(")")));
                    if (references.length() > 0) {
                        sql.append(references);
                    }
                    sql.append(")");
                }
            }
        }
        return sql;
    }
}

