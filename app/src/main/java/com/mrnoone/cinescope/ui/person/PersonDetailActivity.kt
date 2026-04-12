package com.mrnoone.cinescope.ui.person

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrnoone.cinescope.R
import com.mrnoone.cinescope.databinding.ActivityPersonDetailBinding
import com.mrnoone.cinescope.ui.details.MovieDetailActivity
import com.mrnoone.cinescope.ui.common.MoviesSmallAdapter
import com.mrnoone.cinescope.util.Constants
import com.mrnoone.cinescope.util.gone
import com.mrnoone.cinescope.util.loadImage
import com.mrnoone.cinescope.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class PersonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonDetailBinding
    private lateinit var viewModel: PersonDetailViewModel
    private lateinit var filmographyAdapter: MoviesSmallAdapter

    private var personId: Int = -1
    private var personName: String = ""
    private var isBiographyExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        personId = intent.getIntExtra(Constants.EXTRA_PERSON_ID, -1)
        personName = intent.getStringExtra(Constants.EXTRA_PERSON_NAME) ?: ""

        viewModel = ViewModelProvider(this)[PersonDetailViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupViews()
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

    private fun setupViews() {
        // Share Button
        binding.btnShare.setOnClickListener {
            sharePerson()
        }

        // Favorite Button
        binding.btnFavorite.setOnClickListener {
            Snackbar.make(binding.root, "Favorite feature coming soon", Snackbar.LENGTH_SHORT).show()
        }

        // Read More/Less Button
        binding.btnReadMore.setOnClickListener {
            toggleBiography()
        }

        // View All Filmography
        binding.btnViewAll.setOnClickListener {
            Snackbar.make(binding.root, "Full filmography coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Share person details
     */
    private fun sharePerson() {
        val shareText = """
            Check out ${personName}

            View more details in CineScope AI
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, personName)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share $personName"))
    }

    /**
     * Toggle biography read more/less
     */
    private fun toggleBiography() {
        isBiographyExpanded = !isBiographyExpanded
        binding.tvBiography.maxLines = if (isBiographyExpanded) Int.MAX_VALUE else 5
        binding.btnReadMore.apply {
            text = if (isBiographyExpanded) {
                getString(R.string.read_less)
            } else {
                getString(R.string.read_more)
            }
            icon = if (isBiographyExpanded) {
                getDrawable(android.R.drawable.arrow_up_float)
            } else {
                getDrawable(android.R.drawable.arrow_down_float)
            }
        }
    }

    /**
     * Calculate age from birthday
     */
    private fun calculateAge(birthday: String?): Int {
        if (birthday.isNullOrEmpty() || birthday == "Unknown") return 0

        return try {
            val birthDate = LocalDate.parse(birthday, DateTimeFormatter.ISO_LOCAL_DATE)
            val today = LocalDate.now()
            Period.between(birthDate, today).years
        } catch (e: Exception) {
            0
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
                                com.mrnoone.cinescope.data.model.Movie(
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
                            binding.emptyFilmography.gone()

                            // Update movies count
                            binding.tvMoviesCount.text = movies.size.toString()
                        } else {
                            binding.rvFilmography.gone()
                            binding.emptyFilmography.visible()
                            binding.tvMoviesCount.text = "0"
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun displayPersonDetails(person: com.mrnoone.cinescope.data.model.Person) {
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
            ivProfileBackground.loadImage(profileUrl) // Same image for background

            // Name and Known For
            tvName.text = person.name
            chipKnownFor.text = person.knownForDepartment ?: "Acting"
            tvKnownFor.text = person.knownForDepartment ?: "Acting"

            // Calculate and display age
            val age = calculateAge(person.birthday)
            tvAge.text = if (age > 0) age.toString() else "-"

            // Popularity
            tvPopularity.text = person.popularity?.let { String.format("%.1f", it) } ?: "-"

            // Biography
            val biography = if (!person.biography.isNullOrEmpty()) {
                person.biography
            } else {
                "No biography available."
            }
            tvBiography.text = biography

            // Show/hide read more button based on biography length
            if (biography.length < 250) {
                btnReadMore.gone()
            } else {
                btnReadMore.visible()
            }

            // Personal Info
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
