package com.reo.running.runnershigh

import androidx.room.*

@Dao
interface AddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAddress(address: Address)

    @Query("SELECT * FROM ADDRESS")
    fun findAll(): List<Address>

    @Update
    fun updateAddress(address: Address)

    @Delete
    fun delete(address: Address)
}