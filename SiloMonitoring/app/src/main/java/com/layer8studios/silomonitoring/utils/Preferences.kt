package com.layer8studios.silomonitoring.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.models.SiloHistoryEntry
import com.layer8studios.silomonitoring.utils.Utils.toLocalDate


object Preferences {
    private lateinit var preferences: SharedPreferences

    private const val NAME = "PREFERENCES"
    private const val MODE = Context.MODE_PRIVATE
    private val gson = GsonBuilder()
    private var isInitialized = false

    private const val SILOS = "SILOS"


    fun isInitialized() = isInitialized

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
        isInitialized = true
    }


    fun addSilo(silo: Silo) {
        val silos = getSilos()
        silos.add(silo)
        saveSilos(silos)
    }

    fun replaceSilo(oldSilo: Silo, newSilo: Silo) {
        val silos = getSilos()
        val silo = silos.find { it.name == oldSilo.name
                && it.content == oldSilo.content
                && it.capacity == oldSilo.capacity
        }
        val item = silos.indexOf(silo)
        silos[item] = newSilo
        saveSilos(silos)
    }

    fun removeSilo(silo: Silo) {
        val silos = getSilos()
        silos.remove(silo)
        saveSilos(silos)
    }

    fun getSilos(): ArrayList<Silo> {
        val json = preferences.getString(SILOS, null)
        val type = object: TypeToken<ArrayList<Silo>?>() { }.type
        val silos: ArrayList<Silo> = GsonBuilder().create().fromJson(json, type) ?: ArrayList()

        for(silo in silos) {
            silo.emptyingHistory.sortBy { it.amount }
            silo.emptyingHistory.sortBy { it.wasAdded }
            silo.emptyingHistory.sortByDescending { it.date.toLocalDate() }
        }

        return silos
    }


    private fun saveSilos(list: ArrayList<Silo>) {
        val json = gson.create().toJson(list)
        preferences.edit().putString(SILOS, json).apply()
    }

}