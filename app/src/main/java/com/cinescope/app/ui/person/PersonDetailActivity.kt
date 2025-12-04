package com.cinescope.app.ui.person

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cinescope.app.databinding.ActivityPersonDetailBinding
import com.cinescope.app.ui.details.MovieDetailActivity
import com.cinescope.app.ui.common.MoviesSmallAdapter
import com.cinescope.app.util.Constants
import com.cinescope.app.util.gone
import com.cinescope.app.util.loadImage
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PersonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonDetailBinding
    private lateinit var viewModel: PersonDetailViewModel
    private lateinit var filmographyAdapter: MoviesSmallAdapter
    
    private var personId: Int = -1
    private var personName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personId = intent.getIntExtra(Constants.EXTRA_PERSON_ID, -1)
        personName = intent.getStringExtra(Constants.EXTRA_PERSON_NAME) ?: ""

        viewModel = ViewModelProvider(this)[PersonDetailViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        if (personId != -1) {
            viewModel.loadAllPersonData(personId)
        } else {
            Snackbar.make(binding.root, "Invalid person ID", Snackbar.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }
    }

    private fun setupRecyclerView() {
        filmographyAdapter = MoviesSmallAdapter { movie ->
            val intent = Intent(this, MovieDetailActivity::class.java)
            // Use traktId for API calls, fallback to imdbId
            val movieId = movie.traktId?.toString() ?: movie.imdbId
            intent.putExtra(Constants.EXTRA_IMDB_ID, movieId)
            intent.putExtra(Constants.EXTRA_MOVIE_TITLE, movie.title)
            startActivity(intent)
        }
        
        binding.rvFilmography.apply {
            layoutManager = LinearLayoutManager(this@PersonDetailActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = filmographyAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.personState.collect { state ->
                when (state) {
                    is PersonDetailState.Loading -> binding.progressBar.visible()
                    is PersonDetailState.Success -> {
                        binding.progressBar.gone()
                        displayPersonDetails(state.person)
                    }
                    is PersonDetailState.Error -> {
                        binding.progressBar.gone()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filmographyState.collect { state ->
                when (state) {
                    is FilmographyState.Success -> {
                        // Convert FilmographyMovie to Movie for the adapter
                        val filmographyMovies = state.filmography.cast?.sortedByDescending { it.releaseDate } ?: emptyList()
                        if (filmographyMovies.isNotEmpty()) {
                            val movies = filmographyMovies.map { fm ->
                                com.cinescope.app.data.model.Movie(
                                    imdbId = fm.imdbId ?: "trakt-${fm.traktId}", // Use imdbId, fallback to traktId
                                    traktId = fm.traktId,
                                    title = fm.title,
                                    posterPath = fm.posterPath, // Already full URL from backend
                                    releaseDate = fm.releaseDate,
                                    voteAverage = fm.voteAverage ?: 0.0,
                                    overview = fm.overview,
                                    tmdbId = fm.tmdbId
                                )
                            }
                            filmographyAdapter.submitList(movies)
                            binding.rvFilmography.visible()
                        } else {
                            binding.rvFilmography.gone()
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun displayPersonDetails(person: com.cinescope.app.data.model.Person) {
        binding.apply {
            // Profile Image - use full URL directly (backend provides complete URL)
            val profileUrl = if (person.profilePath?.startsWith("http") == true) {
                person.profilePath
            } else if (!person.profilePath.isNullOrEmpty()) {
                "${Constants.TMDB_IMAGE_BASE_URL}${Constants.PROFILE_SIZE_LARGE}${person.profilePath}"
            } else {
                null
            }
            ivProfile.loadImage(profileUrl)
            
            tvName.text = person.name
            tvKnownFor.text = person.knownForDepartment
            tvBiography.text = if (!person.biography.isNullOrEmpty()) person.biography else "No biography available."
            
            tvBirthday.text = person.birthday ?: "Unknown"
            tvPlaceOfBirth.text = person.placeOfBirth ?: "Unknown"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
