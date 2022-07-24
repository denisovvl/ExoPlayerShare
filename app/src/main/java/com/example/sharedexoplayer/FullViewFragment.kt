package com.example.sharedexoplayer

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.transition.TransitionValues
import com.example.sharedexoplayer.databinding.FullViewFragmentBinding
import com.google.android.exoplayer2.ui.PlayerView
import java.lang.Math.abs

class FullViewFragment : Fragment(), PlayerPositionProvider {

    private var _binding: FullViewFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ITEM_KEY = "itemKey"

        const val SHARE_NAME = "second"

        fun getInstance(item: String): FullViewFragment {
            return FullViewFragment().apply {
                arguments = Bundle().apply {
                    putString(ITEM_KEY, item)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FullViewFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.setOnClickListener { }
        binding.btn.setOnClickListener {
            getBackPlayer().setupBackAnim()
        }
        binding.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        binding.root.let { ViewCompat.setTransitionName(it as View, SHARE_NAME) }
        PlayerHolder.player?.let { player ->
            PlayerView.switchTargetView(
                player,
                getBackPlayer().getPlayer(),
                binding.playerView
            )
        }

        setupPosition()

        PlayerHolder.player?.play()
        PlayerHolder.onFirstRender = {
            PlayerHolder.onFirstRender = null
            MyAnimation().apply {
                val s = TransitionValues(binding.playerView)
                val e = TransitionValues(binding.root)
                captureStartValues(s)
                captureEndValues(e)
                targetView = binding.playerView
                createAnimator(binding.root, s, e)
                    ?.apply {
                        addListener(onEnd = {
                            getBackPlayer().getPlayer().visibility = View.INVISIBLE
                        })
                    }
                    ?.start()
            }
        }
        var startY = 0F
        var offset: Float = 0F
        binding.root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    offset = event.rawY - startY
                    v.layoutParams = (v.layoutParams as FrameLayout.LayoutParams).apply {
                        setMargins(leftMargin, offset.toInt(), rightMargin, bottomMargin)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(offset) > binding.root.height / 4) {
                        getBackPlayer().setupBackAnim()
                    } else {
                        moveVideoToStart(offset)
                    }
                    startY = 0F
                    offset = 0f
                }
            }
            false
        }
    }

    override fun getPlayer() = binding.playerView

    override fun getPlayerPosition(): PlayerPosition {
        val lp = binding.playerView.layoutParams as FrameLayout.LayoutParams
        val topMargin = (binding.root.layoutParams as FrameLayout.LayoutParams).topMargin
        return PlayerPosition(lp.width, lp.height, lp.gravity, topMargin)
    }

    private fun setupPosition() {
        val playerPosition = (getBackPlayer() as PlayerPositionProvider).getPlayerPosition()

        binding.playerView.layoutParams = FrameLayout.LayoutParams(
            playerPosition.width,
            playerPosition.height
        ).apply {
            setMargins(0, playerPosition.marginTop, 0, 0)
            gravity = playerPosition.gravity
        }
    }

    private fun getBackPlayer() = requireActivity()
        .supportFragmentManager
        .findFragmentByTag("MyFragmentList") as MyFragmentList

    private fun moveVideoToStart(offset: Float) {
        ValueAnimator.ofFloat(offset, 0F).apply {
            addUpdateListener { valAnim ->
                val topMargin = valAnim.animatedValue as Float
                binding.root.layoutParams =
                    (binding.root.layoutParams as FrameLayout.LayoutParams).apply {
                        setMargins(leftMargin, topMargin.toInt(), rightMargin, bottomMargin)
                    }
            }
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}