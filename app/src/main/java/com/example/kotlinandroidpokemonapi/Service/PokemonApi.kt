package com.example.kotlinandroidpokemonapi.Service
import com.example.kotlinandroidpokemonapi.Data.PokeMonList
import com.example.kotlinandroidpokemonapi.Data.Pokemon
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * REST Interface for retrieving relevant pokemon service
 */
interface PokemonApi {
    @GET("pokemon")
    fun getPokemonCharacter():Deferred<Response<PokeMonList>>

    @GET("pokemon/{name}")
    fun getPokemonDetail(@Path("name") key: String): Deferred<Response<Pokemon>>

    @GET("pokemon")
    fun getPokemonLimit(@Query("limit") ket:Int):Deferred<Response<PokeMonList>>


}