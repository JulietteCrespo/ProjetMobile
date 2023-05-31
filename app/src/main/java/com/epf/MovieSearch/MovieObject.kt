package com.epf.MovieSearch

import com.google.gson.JsonObject

data class MovieObject(
    var page : Int,
    var results : Array<movieJsonObject>
)

data class movieJsonObject(
    var original_title : String,
    var overview : String
)


