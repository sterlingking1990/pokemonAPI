package com.example.kotlinandroidpokemonapi

import android.os.Bundle
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinandroidpokemonapi.Data.Pokemon
import com.example.kotlinandroidpokemonapi.Service.PokemonApiFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),PokemonClickInterface, IPokemonLoadable {
    private val pokemonService = PokemonApiFactory.pokeMonApi
    lateinit var editText:EditText

    var imageurl = "https://pokeres.bastionbot.org/images/pokemon/"
    private var isDetailLayout=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * When the program launches, get all pokemon's default number of characters i.e 20
         */
        getAllPokemon()

        /**
         * when the button is clicked, get all pokemon by entering the number of pokemon to get
         */
        btnLoadMore.setOnClickListener {
            editText=findViewById(R.id.etnumOfPokemonToLoad)

            getAllPokemon(editText.text.toString().toInt())
        }

    }

    /**
     * get all pokemon by allowing the user to enter how many pokemon to display
     */
    override fun getAllPokemon(numberOfPokemonToLoad:Int){
        GlobalScope.launch(Dispatchers.Main) {
            val pokemonAllService = pokemonService.getPokemonLimit(numberOfPokemonToLoad)
            try {
                val response = pokemonAllService.await()
                if (response.isSuccessful) {
                    val pokemonAll = response.body() //This is single object Tmdb Movie response
                    val pokemonList = pokemonAll?.results // This is list of TMDB Movie
                    var adapter = PokemonTemplateAdapter(pokemonList, this@MainActivity)
                    rvPokemon.adapter = adapter
                    rvPokemon.layoutManager = GridLayoutManager(
                        this@MainActivity,
                        3,
                        RecyclerView.VERTICAL,
                        false
                    )

                } else {
                    Toast.makeText(this@MainActivity,"Number of pokemon character could not be loaded",Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {

            }
        }
    }

    override fun getAllPokemon() {
        GlobalScope.launch(Dispatchers.Main) {
            val pokemonAllService = pokemonService.getPokemonCharacter()
            try {
                val response = pokemonAllService.await()
                if (response.isSuccessful) {
                    val pokemonAll = response.body() //This is single object Tmdb Movie response
                    val pokemonList = pokemonAll?.results // This is list of TMDB Movie
                    var adapter = PokemonTemplateAdapter(pokemonList, this@MainActivity)
                    rvPokemon.adapter = adapter
                    rvPokemon.layoutManager = GridLayoutManager(
                        this@MainActivity,
                        3,
                        RecyclerView.VERTICAL,
                        false
                    )

                } else {
                    Toast.makeText(this@MainActivity,"Number of pokemon character could not be loaded",Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {

            }
        }
    }


    /**
     * When the poke mon is clicked, display the detail
     */
    override fun onPokemonClick(pokemon: Pokemon, position: Int) {
        GlobalScope.launch(Dispatchers.Main){
            val pokemonDetailService = pokemonService.getPokemonDetail(pokemon.name)
            val detailResponse= pokemonDetailService?.await()
            val pokemonDetailBody=detailResponse.body()

            if (detailResponse != null) {
                if(detailResponse.isSuccessful){
                    if(isDetailLayout){
                        swapFrames(R.layout.activity_main)

                    }
                    else{
                        if (pokemonDetailBody != null) {
                            tvpokemonName.text=pokemonDetailBody.name
                            var splitUrl=pokemon.url.split("/")
                            var pokemonIdPos= splitUrl.size-2
                            var pokemonId=splitUrl[pokemonIdPos]
                            //use the id to return the appropriate image from the image json
                            var imageUrl = "$imageurl$pokemonId.png"

                            var imageView: ImageView = findViewById(R.id.imageViewHeader)
                            Picasso.get().load(imageUrl).into(imageView);

                            //get sprite
                            var frontImage=pokemonDetailBody.sprites.front_default
                            var imageSmall=findViewById<ImageView>(R.id.imageSmall)
                            Picasso.get().load(frontImage).into(imageSmall)

                            //get abilities
                            var string = ""
                            string += pokemonDetailBody.abilities.joinToString(", ") {
                                it.ability.name
                            }
                            tvAbilities.text = string
                            tvAbilities.movementMethod = ScrollingMovementMethod()

                            //get moves
                            var stringMove = ""
                            stringMove += pokemonDetailBody.moves.joinToString(", ") {
                                it.move.name
                            }
                            tvMove.text = stringMove
                            tvMove.movementMethod = ScrollingMovementMethod()

                            //get height and weight
                            tvHeight.text= pokemonDetailBody.height.toString()
                            tvWeight.text=pokemonDetailBody.weight.toString()


                        }
                        swapFrames(R.layout.pokemon_detail_slidein_template)

                    }

//                    pokemonDetailBody?.let { Log.d("pokemonDetail", it.weight.toString()) }
                }
            }
        }
    }

    /**
     * When a pokemon is clicked, slide in the detail of the pokemon using Constraint set animation
     */
    private fun swapFrames(layoutId: Int){
        val constraintSet=ConstraintSet()
        constraintSet.clone(this, layoutId)
        TransitionManager.beginDelayedTransition(ConstraintLayout)
        rvPokemon.visibility=View.GONE
        constraintSet.applyTo(ConstraintLayout)
        isDetailLayout=!isDetailLayout
    }
}