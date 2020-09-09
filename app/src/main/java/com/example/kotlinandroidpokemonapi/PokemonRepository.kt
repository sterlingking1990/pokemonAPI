package com.example.kotlinandroidpokemonapi

import com.example.kotlinandroidpokemonapi.Data.Pokemon
import com.example.kotlinandroidpokemonapi.Service.PokemonApi

class PokemonRepository(private val api:PokemonApi):BaseRepository(){
    suspend fun getAllPokemon():MutableList<Pokemon>?{
        val pokemonResponse = safeApiCall(
            call = {api.getPokemonCharacter().await()},
            errorMessage = "Error Fetching all pokemon"
        )

        return pokemonResponse?.results?.toMutableList();

    }

    suspend fun getPokemonDetail():Pokemon?{

        return safeApiCall(
        call={ api.getPokemonDetail("").await()},
        errorMessage = "Error fetching pokemon detail"
    )

    }



}