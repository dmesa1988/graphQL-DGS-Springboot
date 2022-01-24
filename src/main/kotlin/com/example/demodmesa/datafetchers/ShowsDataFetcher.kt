package com.example.demodmesa.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ShowsDataFetcher {
    private val shows = listOf(
        Show("Stranger Things", 2016),
        Show("Ozark", 2017),
        Show("The Crown", 2016),
        Show("Dead to Me", 2019),
        Show("Orange is the New Black", 2013)).toMutableList()


    @DgsQuery
    fun getShows(@InputArgument titleFilter : String?): List<Show> {
        return if(titleFilter != null) {
            shows.filter { it.title.lowercase().contains(titleFilter.lowercase()) }
        } else {
            shows
        }
    }

    @DgsMutation
    fun addShow(@InputArgument title : String, releaseYear: Int): Show {
        shows.add(Show(title, releaseYear))
        return shows.first { it.title == title }
    }

    @DgsMutation
    fun deleteShow(@InputArgument title : String): Boolean {
        return try{
            shows.remove(shows.first { it.title == title })
            true
        } catch (e: NoSuchElementException){
            false
        }
    }



    data class Show(val title: String, val releaseYear: Int)
}