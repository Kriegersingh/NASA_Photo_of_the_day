package com.example.nasaphotooftheday.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.model.PhotoOfTheDayResponse
import com.example.nasaphotooftheday.viewModels.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.android.synthetic.main.frag_home.*
import kotlinx.android.synthetic.main.scrolling_content.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class HomeFrag : Fragment() {
    private var sheetBehavior: BottomSheetBehavior<View>? = null
    private var localMediaType: String? = null
    private var videoId: String? = null
    private var isPlayerReady = false
    private var datePicker: DatePickerDialog? = null
    private val calendarToday by lazy { Calendar.getInstance() }
    private var tempDate: String? = null
    val API_DATE_FORMAT = "YYYY-MM-dd"
    val UI_DATE_FORMAT = "dd MMM YY"
    val FAB_STATE_IMAGE_MIN = "Image_min"
    val FAB_STATE_IMAGE_MAX = "Image_max"
    val FAB_STATE_VIDEO_MIN = "Video_min"
    val FAB_STATE_VIDEO_MAX = "Video_max"
    private var maxTime : Long ? =null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onStart() {
        super.onStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sheetBehavior = BottomSheetBehavior.from(descriptionBottomSheet)
        if (calendarToday.get(Calendar.HOUR_OF_DAY)<10){
            calendarToday.add(Calendar.DAY_OF_MONTH , -1)
            maxTime = calendarToday.timeInMillis
        }
        tempDate = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault()).format(calendarToday.time)
        maxTime = calendarToday.timeInMillis
        tempDate?.let { date ->
            homeViewModel.fetchPhotoOfTheDay(date)
        }
        setObserver()
        setListeners()
    }

    private fun setObserver() {
        homeViewModel.getPhotoOfTheDay().observe(viewLifecycleOwner, Observer { response ->
            if (!response.title.isNullOrEmpty()) {
                populateViews(response)
            }
        })
    }

    private fun populateViews(photoOfTheDayResponse: PhotoOfTheDayResponse) {
        progressBar?.visibility = View.GONE
        titleView?.visibility = View.VISIBLE
        descriptionBottomSheet?.visibility = View.VISIBLE
        localMediaType = photoOfTheDayResponse.mediaType
        if (photoOfTheDayResponse.mediaType == "image") {
            initializeImage(photoOfTheDayResponse.url)
        } else if (photoOfTheDayResponse.mediaType == "video") {
            initializeVideo(photoOfTheDayResponse.url)
        }
        date?.text = SimpleDateFormat(UI_DATE_FORMAT, Locale.getDefault()).format(calendarToday.time)
        title?.text = photoOfTheDayResponse.title
        description?.text = photoOfTheDayResponse.explanation
    }

    private fun setListeners() {
        floatingButton?.setOnClickListener {
            openFullImage()
        }
        date?.setOnClickListener {
            showCalendar()
        }
    }


    private fun openFullImage() {
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_HIDDEN) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            title?.visibility = View.VISIBLE
            date?.visibility = View.VISIBLE
            if (localMediaType == "video") {
                videoView?.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.pause()
                    }
                })
                floatingButton.setState(FAB_STATE_VIDEO_MIN)
            } else {
                floatingButton.setState(FAB_STATE_IMAGE_MIN)
            }
        } else {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            title?.visibility = View.GONE
            date?.visibility = View.GONE
            if (localMediaType == "video") {
                videoView?.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.play()
                    }

                })
                floatingButton.setState(FAB_STATE_VIDEO_MAX)
            } else {
                floatingButton.setState(FAB_STATE_IMAGE_MAX)
            }
        }
    }

    private fun extractYTId(ytUrl: String): String? {
        var vId: String? = null
        val pattern: Pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\\\w/|embed/|watch?v=)([^#&?]*).*\$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
    }

    private fun showCalendar() {
        if (datePicker == null) {
            datePicker = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val tempCalender = Calendar.getInstance()
                    tempCalender.set(year, monthOfYear, dayOfMonth)
                    calendarToday.time = tempCalender.time
                    tempDate = SimpleDateFormat(
                        API_DATE_FORMAT,
                        Locale.getDefault()
                    ).format(tempCalender.time)
                    tempDate?.let { pickedDate ->
                        homeViewModel.fetchPhotoOfTheDay(pickedDate)
                        date?.text = SimpleDateFormat(UI_DATE_FORMAT, Locale.getDefault()).format(tempCalender.time)
                        setLoadingState()
                    }
                },
                calendarToday.get(Calendar.YEAR),
                calendarToday.get(Calendar.MONTH),
                calendarToday.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePicker?.datePicker?.maxDate = maxTime!!
        datePicker?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.release()
    }

    private fun setLoadingState() {
        videoView?.release()
        progressBar?.visibility = View.VISIBLE
        todayImage?.visibility = View.GONE
        videoView?.visibility = View.GONE
        titleView?.visibility = View.GONE
        descriptionBottomSheet?.visibility = View.GONE
        floatingButton?.visibility = View.GONE
    }

    private fun initializeImage(url: String) {
        videoView.release()
        videoView?.visibility = View.GONE
        todayImage?.visibility = View.VISIBLE
        floatingButton.setState(FAB_STATE_IMAGE_MIN)
        floatingButton?.visibility = View.VISIBLE
        Glide.with(this)
            .load(url)
            .centerCrop()
            .into(todayImage)
    }

    private fun initializeVideo(url: String) {
        todayImage.visibility = View.GONE
        floatingButton.setState(FAB_STATE_VIDEO_MIN)
        floatingButton.visibility = View.VISIBLE
        videoId = extractYTId(url)
        videoView?.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycle.addObserver(videoView)
        videoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                isPlayerReady = true
                youTubePlayer.loadVideo(videoId!!, 0f)
                youTubePlayer.pause()
            }
        })
    }

}
