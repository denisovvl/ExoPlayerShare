package com.example.sharedexoplayer

import android.graphics.Color
import android.graphics.SurfaceTexture
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.example.sharedexoplayer.databinding.MyFragmentListBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.transition.Hold

class MyFragmentList : Fragment(), PlayerPositionProvider {

    private var _binding: MyFragmentListBinding? = null
    private val binding get() = _binding!!

    private val player by lazy {
        PlayerHolder.player ?: ExoPlayer.Builder(requireActivity()).build()
            .also { PlayerHolder.player = it }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyFragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.playerView.player = player
        binding.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        PlayerHolder.startPlayer()
        binding.playerView.setOnClickListener {
            onClickItem("", binding.playerView)
        }
    }

    override fun getPlayerPosition(): PlayerPosition {
        val lp = binding.playerView.layoutParams as FrameLayout.LayoutParams
        return PlayerPosition(lp.width, lp.height, lp.gravity, lp.topMargin)
    }

    override fun getPlayer() = binding.playerView

    fun setupBackAnim() {
        setupPosition()
        binding.playerView.doOnLayout {
            PlayerView.switchTargetView(player, getTopFragment().getPlayer(), binding.playerView)
            PlayerHolder.player?.play()
            PlayerHolder.onFirstRender = {
                requireActivity().supportFragmentManager.popBackStackImmediate()
                PlayerHolder.onFirstRender = null
                MyAnimation().apply {
                    val s = TransitionValues(binding.playerView)
                    val e = TransitionValues(binding.place)
                    captureStartValues(s)
                    captureEndValues(e)
                    targetView = binding.playerView
                    createAnimator(binding.root, s, e)
                        ?.apply {
                            addListener(
                                onStart = {
                                    binding.playerView.visibility = View.VISIBLE
                                },
                                onEnd = {
                                    val placeLP = binding.place.layoutParams as FrameLayout.LayoutParams
                                    binding.playerView.layoutParams = FrameLayout.LayoutParams(
                                        placeLP.width,
                                        placeLP.height
                                    ).apply {
                                        setMargins(0, placeLP.topMargin, 0, 0)
                                        gravity = placeLP.gravity
                                    }
                                })
                        }
                        ?.start()
                }
            }
        }
    }

    private fun onClickItem(item: String, view: View) {
        requireActivity().supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.conteiner, FullViewFragment.getInstance(item), "FullViewFragment")
            .addToBackStack(null)
            .commit()
    }

    private fun setupPosition() {
        val playerPosition = getTopFragment().getPlayerPosition()
        binding.playerView.layoutParams = FrameLayout.LayoutParams(
            playerPosition.width,
            playerPosition.height
        ).apply {
            setMargins(0, playerPosition.marginTop, 0, 0)
            gravity = playerPosition.gravity
        }
    }

    private fun getTopFragment() = (requireActivity()
        .supportFragmentManager
        .findFragmentByTag("FullViewFragment") as FullViewFragment)

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        _binding = null
    }

    companion object {

        const val SHARED_NAME = "first"

        fun getData(): List<String> {
            return arrayListOf(
                "https://cdn1.ozone.ru/s3/multimedia-y/wc1200/6284063794.jpg",
                "https://cdn1.ozone.ru/multimedia/wc1200/1023564818.jpg",
                "https://cdn1.ozone.ru/s3/multimedia-6/wc1200/6118106754.jpg",
                "https://cdn1.ozone.ru/s3/multimedia-t/wc1200/6278353937.jpg"
            )
        }
    }
}