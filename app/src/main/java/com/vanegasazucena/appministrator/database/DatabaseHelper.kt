package com.vanegasazucena.appministrator.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "catalog.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PRODUCTOS = "productos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_PRECIO = "precio"
        const val COLUMN_STOCK = "stock"
        const val COLUMN_IS_DELETED = "is_deleted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_PRECIO REAL NOT NULL,
                $COLUMN_STOCK INTEGER NOT NULL,
                $COLUMN_IS_DELETED INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed initial products
        val cv = ContentValues()

        cv.put(COLUMN_NOMBRE, "Laptop Pro 15")
        cv.put(COLUMN_PRECIO, 1200.00)
        cv.put(COLUMN_STOCK, 10)
        cv.put(COLUMN_IS_DELETED, 0)
        db.insert(TABLE_PRODUCTOS, null, cv)

        cv.clear()
        cv.put(COLUMN_NOMBRE, "Smartphone X")
        cv.put(COLUMN_PRECIO, 850.50)
        cv.put(COLUMN_STOCK, 25)
        cv.put(COLUMN_IS_DELETED, 0)
        db.insert(TABLE_PRODUCTOS, null, cv)

        cv.clear()
        cv.put(COLUMN_NOMBRE, "Auriculares Bluetooth")
        cv.put(COLUMN_PRECIO, 99.99)
        cv.put(COLUMN_STOCK, 50)
        cv.put(COLUMN_IS_DELETED, 0)
        db.insert(TABLE_PRODUCTOS, null, cv)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        onCreate(db)
    }
}
