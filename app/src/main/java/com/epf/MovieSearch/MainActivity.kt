package com.epf.MovieSearch


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.epf.MovieSearch.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setOnNavigationItemSelectedListener { item ->
            val destinationId = item.itemId

            if (destinationId == R.id.navigation_home ||
                destinationId == R.id.navigation_categori ||
                destinationId == R.id.navigation_recherche ||
                destinationId == R.id.navigation_favori ||
                destinationId == R.id.navigation_qr_code ||
                destinationId == R.id.navigation_detail_movie


                    ) {
                val backStackName = "stack_$destinationId"
                val inclusive = true

                navController.navigate(destinationId)

                true
            } else {
                false
            }
        }

    }

}

